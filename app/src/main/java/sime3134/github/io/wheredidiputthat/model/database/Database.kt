package sime3134.github.io.wheredidiputthat.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import sime3134.github.io.wheredidiputthat.model.entities.Item
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.model.entities.Storage

@Database(entities = [ Item::class, Location::class, Room::class, Storage::class], version=1)
abstract class Database : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun locationDao(): LocationDao
    abstract fun roomDao(): RoomDao
    abstract fun storageDao(): StorageDao
}