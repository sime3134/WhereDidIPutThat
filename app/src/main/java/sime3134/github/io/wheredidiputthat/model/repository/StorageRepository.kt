package sime3134.github.io.wheredidiputthat.model.repository

import kotlinx.coroutines.flow.Flow
import sime3134.github.io.wheredidiputthat.model.database.Database
import sime3134.github.io.wheredidiputthat.model.entities.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(private val database: Database) {

    fun getStorageInRoomFlow(roomId: Long): Flow<List<Storage>> {
        return database.storageDao().getStorageInRoom(roomId)
    }

    fun getStorageFlow(storageId: Long): Flow<Storage> {
        return database.storageDao().getStorage(storageId)
    }

    suspend fun insertStorage(storage: Storage): Long {
        return database.storageDao().insertStorage(storage)
    }

    suspend fun deleteStorage(storage: Storage) {
        database.storageDao().deleteStorage(storage)
    }

    suspend fun saveStorage(storage: Storage) {
        database.storageDao().updateStorage(storage)
    }

    suspend fun deleteStorageIfNew(storageId: Long) {
        database.storageDao().deleteStorageIfNew(storageId)
    }
}