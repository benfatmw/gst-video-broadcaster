package com.kalyzee.kontroller_services_api_implem.system.update.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;

import java.util.List;

@Dao
public interface UpdateSessionDao {

    @Query("SELECT * FROM updateSessionModel")
    List<UpdateSessionModel> getAll();

    @Query("SELECT * FROM updateSessionModel WHERE sessionId = :id")
    UpdateSessionModel getById(String id);

    @Query("UPDATE updateSessionModel SET state=:state WHERE sessionId = :id")
    void updateState(String state, String id);

    @Insert
    void insert(UpdateSessionModel... updateSessionModels);

    @Query("DELETE FROM UpdateSessionModel WHERE sessionId = :id")
    void deleteById(String id);

}
