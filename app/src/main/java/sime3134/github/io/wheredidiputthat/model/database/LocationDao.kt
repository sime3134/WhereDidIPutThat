package sime3134.github.io.wheredidiputthat.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sime3134.github.io.wheredidiputthat.model.entities.Location

@Dao
interface LocationDao {
    @Query("SELECT * FROM Location")
    fun getLocationsFlow(): Flow<List<Location>>

    @Query("SELECT * FROM Location")
    suspend fun getLocations(): List<Location>?

    @Query("SELECT * FROM Location WHERE id = (:id)")
    fun getLocationFlow(id: Long): Flow<Location>

    @Query("SELECT * FROM Location WHERE id = (:id)")
    suspend fun getLocation(id: Long): Location?
    @Insert
    suspend fun insertLocation(location: Location): Long
    @Delete
    suspend fun deleteLocation(location: Location)

    @Update
    suspend fun saveLocation(location: Location)
    @Query("DELETE FROM Location WHERE id = (:locationId) AND isNew = 1")
    suspend fun deleteLocationIfNew(locationId: Long)
}