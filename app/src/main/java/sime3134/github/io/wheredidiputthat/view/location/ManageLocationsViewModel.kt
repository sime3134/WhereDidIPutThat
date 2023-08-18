package sime3134.github.io.wheredidiputthat.view.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import javax.inject.Inject

@HiltViewModel
class ManageLocationsViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locations: MutableStateFlow<List<Location>> = MutableStateFlow(emptyList())
    val locations: StateFlow<List<Location>>
        get() = _locations.asStateFlow()

    init {
        viewModelScope.launch {
            locationRepository.getLocationsFlow().collect {
                _locations.value = it
            }
        }
    }

    suspend fun insertLocation(): Long {
        val location = Location(title = "")
        return locationRepository.insertLocation(location)
    }

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.deleteLocation(location)
        }
    }
}