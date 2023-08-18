package sime3134.github.io.wheredidiputthat.model.repository

import kotlinx.coroutines.flow.Flow
import sime3134.github.io.wheredidiputthat.model.database.Database
import sime3134.github.io.wheredidiputthat.model.entities.Room
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(private val database: Database) {

    fun getRoomsInLocationFlow(locationId: Long): Flow<List<Room>> {
        return database.roomDao().getRoomsInLocation(locationId)
    }

    fun getRoomFlow(roomId: Long): Flow<Room> {
        return database.roomDao().getRoom(roomId)
    }

    suspend fun insertRoom(room: Room): Long {
        return database.roomDao().insertRoom(room)
    }

    suspend fun deleteRoom(room: Room) {
        database.roomDao().deleteRoom(room)
    }

    suspend fun saveRoom(room: Room) {
        database.roomDao().saveRoom(room)
    }

    suspend fun deleteRoomIfNew(id: Long) {
        database.roomDao().deleteRoomIfNew(id)
    }

}