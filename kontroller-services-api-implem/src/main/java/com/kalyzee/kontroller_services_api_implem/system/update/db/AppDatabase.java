package com.kalyzee.kontroller_services_api_implem.system.update.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.kalyzee.kontroller_services_api.dtos.system.update.ImageTypeConverter;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateModeConverter;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api_implem.system.update.dao.DownloadSessionDao;
import com.kalyzee.kontroller_services_api_implem.system.update.dao.UpdateSessionDao;


@Database(entities = {UpdateSessionModel.class, DownloadSessionModel.class}, version = 1)
@TypeConverters({ImageTypeConverter.class, UpdateModeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UpdateSessionDao UpdateSessionDao();
    public abstract DownloadSessionDao DownloadSessionDao();
}

