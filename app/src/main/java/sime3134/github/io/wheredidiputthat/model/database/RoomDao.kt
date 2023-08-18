package sime3134.github.io.wheredidiputthat.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sime3134.github.io.wheredidiputthat.model.entities.Room

@Dao
interface RoomDao {
    @Query("SELECT * FROM Room WHERE locationId = (:locationId)")
    fun getRoomsInLocation(locationId: Long): Flow<List<Room>>

    @Query("SELECT * FROM Room WHERE id = (:id)")
    fun getRoom(id: Long): Flow<Room>

    @Insert
    suspend fun insertRoom(room: Room): Long
    @Delete
    suspend fun deleteRoom(room: Room)

    @Update
    suspend fun saveRoom(room: Room)

    @Query("DELETE FROM Room WHERE id = (:id) AND isNew = 1")
    suspend fun deleteRoomIfNew(id: Long)
}