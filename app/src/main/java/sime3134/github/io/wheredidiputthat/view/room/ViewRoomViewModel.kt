package sime3134.github.io.wheredidiputthat.view.room

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.model.entities.Storage
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import sime3134.github.io.wheredidiputthat.model.repository.RoomRepository
import sime3134.github.io.wheredidiputthat.model.repository.StorageRepository
import javax.inject.Inject

@HiltViewModel
class ViewRoomViewModel @Inject constructor(
    roomRepository: RoomRepository,
    private val storageRepository: StorageRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
    ): ViewModel() {

    private val roomId = savedStateHandle.get<Long>("roomId")

    private val _room: MutableStateFlow<Room?> = MutableStateFlow(null)
    val room: StateFlow<Room?>
        get() = _room.asStateFlow()

    private val _storage: MutableStateFlow<List<Storage>> = MutableStateFlow(emptyList())
    val storage: StateFlow<List<Storage>>
        get() = _storage.asStateFlow()

    private val _location: MutableStateFlow<Location?> = MutableStateFlow(null)

    val location: StateFlow<Location?>
        get() = _location.asStateFlow()

    init {
        viewModelScope.launch {
            if (roomId != null) {
                launch {
                    roomRepository.getRoomFlow(roomId).collect {
                        _room.value = it
                    }
                }
                launch {
                    storageRepository.getStorageInRoomFlow(roomId).collect { _storage.value = it }
                }
            }
        }
    }

    fun getLocation(locationId: Long) {
        viewModelScope.launch {
            locationRepository.getLocationFlow(locationId).collect {
                _location.value = it
            }
        }
    }

    suspend fun insertStorage(): Long? {
        val roomId = _room.value?.id
        return if (roomId != null) {
            val storage = Storage(title = "", roomId = roomId)
            storageRepository.insertStorage(storage)
        } else {
            null
        }
    }

    fun deleteStorage(storage: Storage) {
        viewModelScope.launch {
            storageRepository.deleteStorage(storage)
        }
    }
}