package sime3134.github.io.wheredidiputthat.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sime3134.github.io.wheredidiputthat.model.entities.Storage

@Dao
interface StorageDao {
    @Query("SELECT * FROM Storage WHERE roomId = (:roomId)")
    fun getStorageInRoom(roomId: Long): Flow<List<Storage>>

    @Query("SELECT * FROM Storage WHERE id = (:id)")
    fun getStorage(id: Long): Flow<Storage>
    @Insert
    suspend fun insertStorage(storage: Storage): Long
    @Delete
    suspend fun deleteStorage(storage: Storage)
    @Update
    suspend fun updateStorage(storage: Storage)

    @Query("DELETE FROM Storage WHERE id = (:id) AND isNew = 1")
    suspend fun deleteStorageIfNew(id: Long)
}