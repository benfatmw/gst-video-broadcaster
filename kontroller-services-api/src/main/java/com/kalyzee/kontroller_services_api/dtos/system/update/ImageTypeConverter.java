package com.kalyzee.kontroller_services_api.dtos.system.update;

import androidx.room.TypeConverter;

public class ImageTypeConverter {

    @TypeConverter
    public String fromImageType(ImageType imageType) {
        return imageType.getString();
    }

    @TypeConverter
    public ImageType toImageType(String imageType) {
        return ImageType.value(imageType);
    }
}
