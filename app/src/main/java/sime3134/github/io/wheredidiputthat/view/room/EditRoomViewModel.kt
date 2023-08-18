package sime3134.github.io.wheredidiputthat.view.room

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
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import sime3134.github.io.wheredidiputthat.model.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class EditRoomViewModel @Inject constructor(
    locationRepository: LocationRepository,
    private val roomRepository: RoomRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val roomId = savedStateHandle.get<Long>("roomId")
    private val _locations: MutableStateFlow<List<Location>> = MutableStateFlow(emptyList())
    val locations: StateFlow<List<Location>>
        get() = _locations.asStateFlow()

    private val _room: MutableStateFlow<Room?> = MutableStateFlow(null)
    val room: StateFlow<Room?>
        get() = _room.asStateFlow()

    init {
        viewModelScope.launch {
            if (roomId != null) {
                roomRepository.getRoomFlow(roomId).collect { room ->
                    _room.value = room
                }
            }
        }
        viewModelScope.launch {
            locationRepository.getLocationsFlow().collect { locations ->
                _locations.value = locations
            }
        }
    }

    fun updateRoom(onUpdate: (Room) -> Room?) {
        _room.update { oldRoom ->
            oldRoom?.let(onUpdate)
        }
    }

    fun saveRoom() {
        viewModelScope.launch {
            if(room.value?.isNew == true) {
                updateRoom { room ->
                    room.copy(
                        isNew = false
                    )
                }
            }
            room.value?.let { room ->
                roomRepository.saveRoom(room)
            }
        }
    }

    fun deleteRoomIfNew() {
        viewModelScope.launch {
            if(roomId == null) return@launch
            roomRepository.deleteRoomIfNew(roomId)
        }
    }
}