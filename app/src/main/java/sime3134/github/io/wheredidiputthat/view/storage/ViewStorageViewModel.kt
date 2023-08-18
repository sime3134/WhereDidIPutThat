package sime3134.github.io.wheredidiputthat.view.storage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.model.entities.Item
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.model.entities.Storage
import sime3134.github.io.wheredidiputthat.model.repository.ItemRepository
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import sime3134.github.io.wheredidiputthat.model.repository.RoomRepository
import sime3134.github.io.wheredidiputthat.model.repository.StorageRepository
import javax.inject.Inject

@HiltViewModel
class ViewStorageViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val storageRepository: StorageRepository,
    private val locationRepository: LocationRepository,
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val storageId = savedStateHandle.get<Long>("storageId")

    private val _storage: MutableStateFlow<Storage?> = MutableStateFlow(null)
    val storage: StateFlow<Storage?>
        get() = _storage.asStateFlow()

    private val _items: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<Item>>
        get() = _items.asStateFlow()

    private val _room: MutableStateFlow<Room?> = MutableStateFlow(null)
    val room: StateFlow<Room?>
        get() = _room.asStateFlow()

    private val _location: MutableStateFlow<Location?> = MutableStateFlow(null)
    val location: StateFlow<Location?>
        get() = _location.asStateFlow()

    init {
        viewModelScope.launch {
            if (storageId != null) {
                launch {
                    storageRepository.getStorageFlow(storageId).collect {
                        _storage.value = it
                    }
                }
                launch {
                    itemRepository.getItemsInStorage(storageId).collect { _items.value = it }
                }
            }
        }
    }

    fun getRoom(roomId: Long) {
        viewModelScope.launch {
            roomRepository.getRoomFlow(roomId).collect {
                _room.value = it
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

    suspend fun insertItem(): Long? {
        val storageId = _storage.value?.id
        return if (storageId != null) {
            val item = Item(title = "", storageId = storageId)
            itemRepository.insertItem(item)
        } else {
            null
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemRepository.deleteItem(item)
        }
    }
}