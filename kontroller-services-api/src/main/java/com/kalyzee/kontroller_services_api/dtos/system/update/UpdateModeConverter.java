package com.kalyzee.kontroller_services_api.dtos.system.update;

import androidx.room.TypeConverter;

public class UpdateModeConverter {
    @TypeConverter
    public String fromUpdateMode(UpdateMode updateMode) {
        return updateMode.getString();
    }

    @TypeConverter
    public UpdateMode toUpdateMode(String updateMode) {
        return UpdateMode.value(updateMode);
    }
}
