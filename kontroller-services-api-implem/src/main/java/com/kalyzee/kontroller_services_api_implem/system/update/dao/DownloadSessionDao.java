package com.kalyzee.kontroller_services_api_implem.system.update.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;

import java.util.List;

@Dao
public interface DownloadSessionDao {

    @Query("SELECT * FROM downloadsessionModel")
    List<DownloadSessionModel> getAll();

    @Query("SELECT * FROM downloadsessionModel WHERE sessionId = :id")
    DownloadSessionModel getById(String id);

    @Insert
    void insert(DownloadSessionModel... downloadsessions);

    @Query("DELETE FROM downloadsessionModel WHERE sessionId = :id")
    void deleteById(String id);

    @Query("UPDATE downloadsessionModel SET state=:state WHERE sessionId = :id")
    void updateState(String state, String id);
}
