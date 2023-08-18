package sime3134.github.io.wheredidiputthat.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.model.SettingsRepository
import sime3134.github.io.wheredidiputthat.model.entities.Item
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.model.repository.ItemRepository
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import sime3134.github.io.wheredidiputthat.model.repository.RoomRepository
import javax.inject.Inject

private const val RECENT_ITEMS_LIMIT = 5
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val roomRepository: RoomRepository,
    itemRepository: ItemRepository,
    locationRepository: LocationRepository
    ) : ViewModel() {

    private val _locations: MutableStateFlow<List<Location>> = MutableStateFlow(emptyList())
    val locations: StateFlow<List<Location>>
        get() = _locations.asStateFlow()

    private val _rooms: MutableStateFlow<List<Room>> = MutableStateFlow(emptyList())
    val rooms: StateFlow<List<Room>>
        get() = _rooms.asStateFlow()

    private val _recentItems: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())
    val recentItems: StateFlow<List<Item>>
        get() = _recentItems.asStateFlow()

    private val _locationId: MutableStateFlow<Long?> = MutableStateFlow(null)
    val locationId: StateFlow<Long?>
        get() = _locationId.asStateFlow()

    private val _location: MutableStateFlow<Location?> = MutableStateFlow(null)
    val location: StateFlow<Location?>
        get() = _location.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                locationRepository.getLocationsFlow().collect {
                    _locations.value = it
                }
            }

            launch {
                settingsRepository.getLastLocationId().collect {
                    _locationId.value = it
                }
            }
            launch { itemRepository.getRecentItems(RECENT_ITEMS_LIMIT).collect { _recentItems.value = it } }
        }

        _locationId.flatMapLatest {
            it?.let { roomRepository.getRoomsInLocationFlow(it) } ?: flowOf(emptyList())
        }.onEach {
            _rooms.value = it
        }.launchIn(viewModelScope)

        _locationId.flatMapLatest {
            it?.let { locationRepository.getLocationFlow(it) } ?: flowOf(null)
        }.onEach {
            _location.value = it
        }.launchIn(viewModelScope)
    }

    suspend fun insertRoom(): Long? {
        val locationId = _locationId.value
        return if (locationId != null) {
            val room = Room(title = "", locationId = locationId)
            roomRepository.insertRoom(room)
        } else {
            null
        }
    }

    fun setLocation(id: Long) {
        viewModelScope.launch {
            settingsRepository.setLastLocationId(id)
        }

    }
}