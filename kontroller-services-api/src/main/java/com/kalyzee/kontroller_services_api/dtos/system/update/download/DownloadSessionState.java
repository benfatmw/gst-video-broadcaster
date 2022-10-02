package com.kalyzee.kontroller_services_api.dtos.system.update.download;

import android.util.SparseArray;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.concurrent.atomic.AtomicInteger;

public class DownloadSessionState {

    public static final int IDLE = 0;
    public static final int ERROR = 1;
    public static final int RUNNING = 2;
    public static final int FINISHED = 3;
    public static final int ABORTED = 4;
    private static final SparseArray<String> DOWNLOAD_STATE_MAP = new SparseArray<>();

    static {
        DOWNLOAD_STATE_MAP.put(0, "IDLE");
        DOWNLOAD_STATE_MAP.put(1, "ERROR");
        DOWNLOAD_STATE_MAP.put(2, "RUNNING");
        DOWNLOAD_STATE_MAP.put(3, "FINISHED");
        DOWNLOAD_STATE_MAP.put(4, "ABORTED");
    }

    /**
     * Allowed state transitions. It's a map: key is a state, value is a set of states that
     * are allowed to transition to from key.
     */
    private static final ImmutableMap<Integer, ImmutableSet<Integer>> TRANSITIONS =
            ImmutableMap.<Integer, ImmutableSet<Integer>>builder()
                    .put(IDLE, ImmutableSet.of(IDLE, ERROR, RUNNING))
                    .put(ERROR, ImmutableSet.of(IDLE))
                    .put(RUNNING, ImmutableSet.of(
                            IDLE, ERROR, FINISHED))
                    .put(FINISHED, ImmutableSet.of(IDLE))
                    .put(ABORTED, ImmutableSet.of(IDLE))
                    .build();

    private AtomicInteger state;

    public DownloadSessionState(int state) {
        this.state = new AtomicInteger(state);
    }

    /**
     * Returns updater state.
     */
    public int get() {
        return state.get();
    }

    /**
     * Sets the downloader state.
     *
     * @throws InvalidTransitionException if transition is not allowed.
     */
    public void set(int newState) throws InvalidTransitionException {
        int oldState = state.get();
        if (!TRANSITIONS.get(oldState).contains(newState)) {
            throw new InvalidTransitionException("Can't transition from " + oldState + " to " + newState);
        }
        state.set(newState);
    }

    /**
     * Converts status code to status name.
     */
    public static String getStateText(int stateCode) {
        return DOWNLOAD_STATE_MAP.get(stateCode);
    }

    /**
     * Converts status name to status code.
     */
    public static int getStateInt(String stateName) {
        /**
         * We use this function instead of #indexOfValue
         * because indexOfValue(E value) method the value is compared with the array elements
         * by reference (not logical value):
         */
        return indexOfValueByValue (DOWNLOAD_STATE_MAP, stateName);
    }

    /**
     * Returns an index for which would return the
     * specified value, or a negative number if no keys map to the
     * specified value.
     * <p>Beware that this is a linear search, unlike lookups by key,
     * and that multiple keys can map to the same value and this will
     * find only one of them.
     */
    public static int indexOfValueByValue(SparseArray<String> array, String value) {
        for (int i = 0; i < array.size(); i++) {
            if (value == null) {
                if (array.get(i) == null) {
                    return i;
                }
            } else {
                if (value.equals(array.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }
    /**
     * Defines invalid state transition exception.
     */
    public static class InvalidTransitionException extends Exception {
        public InvalidTransitionException(String msg) {
            super(msg);
        }
    }
}
