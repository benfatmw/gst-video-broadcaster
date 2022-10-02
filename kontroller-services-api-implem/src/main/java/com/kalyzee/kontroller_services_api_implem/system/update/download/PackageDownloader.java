package com.kalyzee.kontroller_services_api_implem.system.update.download;

import static com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState.ABORTED;
import static com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState.ERROR;
import static com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState.FINISHED;
import static com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState.RUNNING;
import static com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState.getStateText;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api.exceptions.system.update.download.DownloadBinaryFailureException;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadStatusCallback;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PackageDownloader implements Runnable {

    private static final String TAG = "PackageDownloader";

    private  static final int OKHTTP_REQUEST_MAX_RETRY_VALUE = 10;
    private  static final int OKHTTP_REQUEST_RETRIAL_INTERVAL_IN_MS = 500;
    private static final int CHUNK_SIZE = 512 * 1024;
    private static final String SHA_256_CHECKSUM = "SHA-256";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String RESPONSE_DOES_NOT_CONTAIN_FILE_ERROR = "Response does not contain a file.";
    private static final String FAILED_TO_DOWNLOAD_PACKAGE = "Failed to download package. download url: ";
    private static final String DOWNLOAD_PACKAGE_INTERRUPTED = "Download package has been interrupted. download url:";
    private static final String PACKAGE_VERIFICATION_INTERRUPTED = "Package verification has been interrupted.";
    private static final String TMP_FILE_TRANSFER_INTERRUPTED = "Temporary file transfer to the final location has been interrupted.";
    private static final String FAILED_TO_DOWNLOAD_FILE = "Failed to download file.";
    private static final String MAX_RETRY_ATTEMPTS_REACHED = "Failed to execute okhttp request. Max retry attempts reached";

    private final OkHttpClient client;
    private final Object lock = new Object();
    private final DownloadSessionModel downloadSession;
    private long currentDownloadedSize = 0;

    private boolean isCancelled = false;

    private IDownloadStatusCallback downloadSessionCallback = null;

    public PackageDownloader(OkHttpClient client, DownloadSessionModel downloadSession) {
        this.client = client;
        this.downloadSession = downloadSession;
    }

    public PackageDownloader(OkHttpClient client,
                             DownloadSessionModel downloadSession,
                             IDownloadStatusCallback downloadSessionCallback) {
        this.client = client;
        this.downloadSession = downloadSession;
        this.downloadSessionCallback = downloadSessionCallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Optional<IDownloadStatusCallback> getDownloadSessionCallback() {
        synchronized (lock) {
            return downloadSessionCallback == null
                    ? Optional.empty()
                    : Optional.of(downloadSessionCallback);
        }
    }

    public void terminate() {
        isCancelled = true;
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        File tmpFile = null;
        try {
            /** Set download session state */
            downloadSession.setState(getStateText(RUNNING));

            /**
             *  First create a tmp file for downloading. The tmp file is moved to the final/default
             *  location on success.
             */
            tmpFile = File.createTempFile(downloadSession.getSha256Fingerprint(), ".tmp");
            tmpFile.deleteOnExit();

            /** Download the file to a temporary directory */
            downloadPackage(client, downloadSession.getUrl(), tmpFile);

            /** Check the downloaded tmpfile SHA-256 checksum.*/
            String computedSha256 = getFileChecksum(tmpFile,
                    MessageDigest.getInstance(SHA_256_CHECKSUM),
                    downloadSession.getSha256Fingerprint());
            if (!computedSha256.equals(downloadSession.getSha256Fingerprint())) {
                throw new SecurityException(("Checksum " + SHA_256_CHECKSUM
                        + " not matched for file " + tmpFile.getAbsolutePath()
                        + ": expected= " + downloadSession.getSha256Fingerprint()
                        + "but computed= " + computedSha256));
            } else {
                Log.i(TAG, String.format(" File: %s integrity is verified.", tmpFile.getAbsolutePath()));
            }

            /** Downloaded tmpfile is moved to the default download location */
            moveFile(tmpFile.getAbsolutePath(),
                    downloadSession.getFileLocation() + downloadSession.getFileName());

            /** Set download session state */
            downloadSession.setState(getStateText(FINISHED));
            /** Invoke DownloadSessionCallback#onSuccess if present */
            getDownloadSessionCallback().ifPresent(callback -> callback.onSuccess());
            Log.i(TAG, String.format("File: %s%s download finish with success. ",
                    downloadSession.getFileLocation(), downloadSession.getFileName()));
        } catch (InterruptedIOException | InterruptedException e1) {
            Log.e(TAG, DOWNLOAD_PACKAGE_INTERRUPTED + downloadSession.getUrl(), e1);
            /** Set download session state */
            downloadSession.setState(getStateText(ABORTED));
        } catch (IOException | NoSuchAlgorithmException | RuntimeException e2) {
            Log.e(TAG, FAILED_TO_DOWNLOAD_PACKAGE + downloadSession.getUrl(), e2);
            /** Set download session state */
            downloadSession.setState(getStateText(ERROR));
            /** Invoke DownloadSessionCallback#onFailure if present */
            getDownloadSessionCallback().ifPresent(callback
                    -> callback.onFailure(ExceptionUtils.getStackTrace(
                    new DownloadBinaryFailureException(FAILED_TO_DOWNLOAD_FILE, e2))));
        }
        /** Remove the downloaded tmp file */
        deleteFile(tmpFile);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void downloadPackage(OkHttpClient client, String downloadUrl, File outputFile) throws InterruptedException, IOException {
        /** Download the package to the output file */
        Request request = new Request.Builder()
                .url(downloadUrl).build();
        /**
         * We noticed that okhttp request invoked right after internet connection fails due to hostname resolution error
         * (DNS server not set).
         * As a workaround we added a retry mechanism (with max_attempts).
         * @todo Find a smarter way to detect DNS availability to avoid this workaround
         */
        Response response = executeRequestWithRetry(client, request);
        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            throw new IllegalStateException(RESPONSE_DOES_NOT_CONTAIN_FILE_ERROR);
        }
        long length = Long.parseLong(Objects.requireNonNull(response.header(CONTENT_LENGTH, "1")));

        try (BufferedInputStream input = new BufferedInputStream(responseBody.byteStream());
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] dataBuffer = new byte[8 * 1024]; /** Default buffer size of BufferedInputStream is 8ko */
            int readBytes;
            int chunksNum = 0;
            while ((readBytes = input.read(dataBuffer)) != -1 && !isCancelled) {
                currentDownloadedSize += readBytes;
                outputStream.write(dataBuffer, 0, readBytes);
                /**
                 * Default buffer size of BufferedInputStream is 8ko. No need to change it!
                 * Send a download progress notification every #CHUNK_SIZE bytes download.
                 */
                if ((currentDownloadedSize / CHUNK_SIZE) > chunksNum) {
                    long finalTotalBytes = currentDownloadedSize;
                    /** Invoke DownloadSessionCallback#onProgress if present */
                    getDownloadSessionCallback().ifPresent(callback -> callback.onProgress(finalTotalBytes, length));
                    Log.d(TAG, "Binary download in progress: readBytes: " + currentDownloadedSize + ", totalBytes: " + length);
                    chunksNum++;
                }
            }
        }
        if (isCancelled) {
            throw new InterruptedIOException(DOWNLOAD_PACKAGE_INTERRUPTED);
        }
    }

    private Response executeRequestWithRetry(OkHttpClient client, Request request) throws InterruptedException, IOException {
        int count = 0;
        while (count < OKHTTP_REQUEST_MAX_RETRY_VALUE) {
            try {
                Response response = client.newCall(request).execute();
                return response;
            } catch (Exception e) {
                count++;
                Thread.sleep(OKHTTP_REQUEST_RETRIAL_INTERVAL_IN_MS);
            }
        }
        throw new IOException(MAX_RETRY_ATTEMPTS_REACHED);
    }

    /**
     * Calculates the checksum of a {@link File}.
     *
     * @param file   file to the hash of.
     * @param digest
     * @return the checksum of the file.
     */
    private String getFileChecksum(File file, MessageDigest digest, String expected) throws IOException {
        /** Get file input stream for reading the file content */
        FileInputStream fis = new FileInputStream(file);
        /** Create byte array to read data in chunks */
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        /** Read file data and update in message digest */
        while ((bytesCount = fis.read(byteArray)) != -1 && !isCancelled) {
            digest.update(byteArray, 0, bytesCount);
        }
        /** Close the stream; We don't need it now. */
        fis.close();
        /** Get the hash's bytes */
        byte[] bytes = digest.digest();
        /**
         * This bytes[] has bytes in decimal format
         * Convert it to hexadecimal format
         */
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        if (isCancelled) {
            throw new InterruptedIOException(PACKAGE_VERIFICATION_INTERRUPTED);
        }
        return sb.toString();
    }

    private void moveFile(String srcAbsolutePath, String destAbsolutePath) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        in = new FileInputStream(srcAbsolutePath);
        out = new FileOutputStream(destAbsolutePath);
        byte[] buffer = new byte[CHUNK_SIZE];
        int read;
        while ((read = in.read(buffer)) != -1 && !isCancelled) {
            out.write(buffer, 0, read);
        }
        in.close();
        in = null;
        /** Write the output file */
        out.flush();
        out.close();
        out = null;
        /** Delete the original file */
        new File(srcAbsolutePath).delete();
        if (isCancelled) {
            throw new InterruptedIOException(TMP_FILE_TRANSFER_INTERRUPTED);
        }
    }

    private void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        file.delete();
    }
}