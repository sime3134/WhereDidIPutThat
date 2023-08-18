package sime3134.github.io.wheredidiputthat.view.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.model.SettingsRepository
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import sime3134.github.io.wheredidiputthat.model.repository.RoomRepository
import javax.inject.Inject

@HiltViewModel
class SetupAddRoomViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val locationRepository: LocationRepository,
    private val roomRepository: RoomRepository
): ViewModel() {

    private val _status: MutableStateFlow<Result<Unit>?> = MutableStateFlow(null)
    val status: StateFlow<Result<Unit>?> = _status.asStateFlow()

    fun addLocationAndRoom(location: Location, room: Room) {
        viewModelScope.launch {
            try {
                val id = locationRepository.insertLocation(location)
                settingsRepository.setLastLocationId(id)
                room.locationId = id
                roomRepository.insertRoom(room)
                _status.emit(Result.success(Unit))
            } catch (e: Exception) {
                _status.emit(Result.failure(e))
            }
        }
    }
}