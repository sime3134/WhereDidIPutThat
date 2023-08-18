package sime3134.github.io.wheredidiputthat.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sime3134.github.io.wheredidiputthat.model.entities.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM Item WHERE lastViewed IS NOT NULL ORDER BY lastViewed DESC LIMIT :count")
    fun getRecentItems(count: Int): Flow<List<Item>>

    @Query("SELECT * FROM Item WHERE storageId = (:storageId)")
    fun getItemsInStorage(storageId: Long): Flow<List<Item>>

    @Query("SELECT * FROM Item WHERE id = (:id)")
    fun getItem(id: Long): Flow<Item>
    @Insert
    suspend fun insertItem(item: Item): Long
    @Delete
    suspend fun deleteItem(item: Item)
    @Update
    suspend fun updateItem(item: Item)
    @Query("DELETE FROM Item WHERE id = (:id) AND isNew = 1")
    suspend fun deleteItemIfNew(id: Long)
}