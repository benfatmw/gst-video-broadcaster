package com.kalyzee.panel_connection_manager.mappers.video;


import com.kalyzee.kontroller_services_api.dtos.video.RecordSessionContext;
import com.kalyzee.kontroller_services_api.dtos.video.VideoInformation;

public class GetRecordSessionContextResponseContent extends RecordSessionContext {
    public GetRecordSessionContextResponseContent(VideoInformation videoInformation, String state) {
        super(videoInformation, state);
    }
}
