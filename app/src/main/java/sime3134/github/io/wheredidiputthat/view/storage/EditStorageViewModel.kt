package sime3134.github.io.wheredidiputthat.view.storage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.model.entities.Storage
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import sime3134.github.io.wheredidiputthat.model.repository.RoomRepository
import sime3134.github.io.wheredidiputthat.model.repository.StorageRepository
import javax.inject.Inject

@HiltViewModel
class EditStorageViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val storageRepository: StorageRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val storageId = savedStateHandle.get<Long>("storageId")

    private val _locations: MutableStateFlow<List<Location>> = MutableStateFlow(emptyList())
    val locations: StateFlow<List<Location>>
        get() = _locations.asStateFlow()

    private val _rooms: MutableStateFlow<List<Room>> = MutableStateFlow(emptyList())
    val rooms: StateFlow<List<Room>>
        get() = _rooms.asStateFlow()

    private val _room: MutableStateFlow<Room?> = MutableStateFlow(null)
    val room: StateFlow<Room?>
        get() = _room.asStateFlow()

    private val _storage: MutableStateFlow<Storage?> = MutableStateFlow(null)
    val storage: StateFlow<Storage?>
        get() = _storage.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepository.getLocationsFlow().collect { locations ->
                _locations.value = locations
            }
        }
        viewModelScope.launch {
            if (storageId != null) {
                storageRepository.getStorageFlow(storageId).collect { storage ->
                    if(storage != null) {
                        getRoom(storage.roomId)
                        _storage.value = storage
                    }
                }
            }
        }
    }

    fun updateStorage(onUpdate: (Storage) -> Storage?) {
        _storage.update { oldStorage ->
            oldStorage?.let(onUpdate)
        }
    }

    fun saveStorage(title: String, description: String) {
        viewModelScope.launch {
                updateStorage { storage ->
                    storage.copy(
                        isNew = false,
                        title = title,
                        description = description
                    )
                }
            _storage.value?.let { storage ->
                storageRepository.saveStorage(storage)
            }
        }
    }

    fun deleteStorageIfNew() {
        viewModelScope.launch {
            if(storageId == null) return@launch
            storageRepository.deleteStorageIfNew(storageId)
        }
    }

    fun onLocationSelected(locationId: Long) {
        viewModelScope.launch {
            roomRepository.getRoomsInLocationFlow(locationId).collect { rooms ->
                _rooms.value = rooms
            }
        }
    }

    private fun getRoom(roomId: Long?) {
        viewModelScope.launch {
            if (roomId != null) {
                roomRepository.getRoomFlow(roomId).collect {
                    _room.value = it
                }
            }
        }
    }
}