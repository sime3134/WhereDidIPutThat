package sime3134.github.io.wheredidiputthat.model.repository

import kotlinx.coroutines.flow.Flow
import sime3134.github.io.wheredidiputthat.model.database.Database
import sime3134.github.io.wheredidiputthat.model.entities.Item
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(private val database: Database) {

    fun getRecentItems(count: Int): Flow<List<Item>> {
        return database.itemDao().getRecentItems(count)
    }

    fun getItemsInStorage(storageId: Long): Flow<List<Item>> {
        return database.itemDao().getItemsInStorage(storageId)
    }

    fun getItemFlow(itemId: Long): Flow<Item> {
        return database.itemDao().getItem(itemId)
    }

    suspend fun insertItem(item: Item): Long {
        return database.itemDao().insertItem(item)
    }

    suspend fun deleteItem(item: Item) {
        database.itemDao().deleteItem(item)
    }

    suspend fun saveItem(item: Item) {
        database.itemDao().updateItem(item)
    }

    suspend fun deleteItemIfNew(itemId: Long) {
        database.itemDao().deleteItemIfNew(itemId)
    }
}