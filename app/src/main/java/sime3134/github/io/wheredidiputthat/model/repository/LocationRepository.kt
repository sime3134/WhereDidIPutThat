package sime3134.github.io.wheredidiputthat.model.repository

import kotlinx.coroutines.flow.Flow
import sime3134.github.io.wheredidiputthat.model.database.Database
import sime3134.github.io.wheredidiputthat.model.entities.Location
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(private val database: Database) {

    fun getLocationsFlow(): Flow<List<Location>> {
        return database.locationDao().getLocationsFlow()
    }

    fun getLocationFlow(locationId: Long): Flow<Location> {
        return database.locationDao().getLocationFlow(locationId)
    }

    suspend fun insertLocation(location: Location): Long {
        return database.locationDao().insertLocation(location)
    }

    suspend fun deleteLocation(location: Location) {
        database.locationDao().deleteLocation(location)
    }

    suspend fun saveLocation(location: Location) {
        database.locationDao().saveLocation(location)
    }

    suspend fun deleteLocationIfNew(locationId: Long) {
        database.locationDao().deleteLocationIfNew(locationId)
    }
}