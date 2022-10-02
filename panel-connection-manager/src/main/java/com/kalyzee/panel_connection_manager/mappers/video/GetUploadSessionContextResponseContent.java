package com.kalyzee.panel_connection_manager.mappers.video;


import com.kalyzee.kontroller_services_api.dtos.video.UploadSessionContext;

public class GetUploadSessionContextResponseContent extends UploadSessionContext {
    public GetUploadSessionContextResponseContent(int errorStatus, String state) {
        super(errorStatus, state);
    }
}
