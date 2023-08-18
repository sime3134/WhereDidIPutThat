package sime3134.github.io.wheredidiputthat

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp
import sime3134.github.io.wheredidiputthat.model.repository.ItemRepository
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import sime3134.github.io.wheredidiputthat.model.repository.RoomRepository
import sime3134.github.io.wheredidiputthat.model.repository.StorageRepository

@HiltAndroidApp
class WDIPTApplication : Application() {
}