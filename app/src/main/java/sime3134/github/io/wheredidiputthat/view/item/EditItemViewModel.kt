package sime3134.github.io.wheredidiputthat.view.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
class EditItemViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val storageRepository: StorageRepository,
    private val locationRepository: LocationRepository,
    private val itemRepository: ItemRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val itemId = savedStateHandle.get<Long>("itemId")

    private val _locations: MutableStateFlow<List<Location>> = MutableStateFlow(emptyList())
    val locations: StateFlow<List<Location>>
        get() = _locations.asStateFlow()

    private val _rooms: MutableStateFlow<List<Room>> = MutableStateFlow(emptyList())
    val rooms: StateFlow<List<Room>>
        get() = _rooms.asStateFlow()

    private val _room: MutableStateFlow<Room?> = MutableStateFlow(null)
    val room: StateFlow<Room?>
        get() = _room.asStateFlow()

    private val _storages: MutableStateFlow<List<Storage>> = MutableStateFlow(emptyList())
    val storages: StateFlow<List<Storage>>
        get() = _storages.asStateFlow()

    private val _storage: MutableStateFlow<Storage?> = MutableStateFlow(null)
    val storage: StateFlow<Storage?>
        get() = _storage.asStateFlow()

    private val _item: MutableStateFlow<Item?> = MutableStateFlow(null)
    val item: StateFlow<Item?>
        get() = _item.asStateFlow()

    init {
        viewModelScope.launch {
            if (itemId != null) {
                itemRepository.getItemFlow(itemId).collect { item ->
                    getStorage(item.storageId)
                    _item.value = item
                }
            }
        }
    }

    fun updateItem(onUpdate: (Item) -> Item?) {
        _item.update { oldItem ->
            oldItem?.let(onUpdate)
        }
    }

    fun saveItem(title: String, description: String) {
        viewModelScope.launch {
            updateItem { item ->
                item.copy(
                    isNew = false,
                    title = title,
                    description = description,
                    lastViewed = System.currentTimeMillis()
                )
            }
            _item.value?.let { item ->
                itemRepository.saveItem(item)
            }
        }
    }

    fun deleteItemIfNew() {
        viewModelScope.launch {
            if(itemId == null) return@launch
            itemRepository.deleteItemIfNew(itemId)
        }
    }

    fun onLocationSelected(locationId: Long) {
        viewModelScope.launch {
            roomRepository.getRoomsInLocationFlow(locationId).collect { rooms ->
                _rooms.value = rooms
            }
        }
    }

    fun onRoomSelected(roomId: Long) {
        viewModelScope.launch {
            storageRepository.getStorageInRoomFlow(roomId).collect { storages ->
                _storages.value = storages
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

    private fun getStorage(storageId: Long?) {
        viewModelScope.launch {
            if (storageId != null) {
                storageRepository.getStorageFlow(storageId).collect { storage ->
                    getRoom(storage.roomId)
                    _storage.value = storage
                }
            }
        }
    }
}