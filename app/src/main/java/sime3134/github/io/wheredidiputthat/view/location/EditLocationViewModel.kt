package sime3134.github.io.wheredidiputthat.view.location

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
import sime3134.github.io.wheredidiputthat.model.repository.LocationRepository
import javax.inject.Inject

@HiltViewModel
class EditLocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _location: MutableStateFlow<Location?> = MutableStateFlow(null)
    val location: StateFlow<Location?>
        get() = _location.asStateFlow()

    private val locationId = savedStateHandle.get<Long>("locationId")

    init {
        viewModelScope.launch {
            if (locationId != null) {
                locationRepository.getLocationFlow(locationId).collect { location ->
                    _location.value = location
                }
            }
        }
    }

    fun updateLocation(onUpdate: (Location) -> Location?) {
        _location.update { oldLocation ->
            oldLocation?.let(onUpdate)
        }
    }

    fun saveLocation() {
        viewModelScope.launch {
            if(location.value?.isNew == true) {
                updateLocation { location ->
                    location.copy(
                        isNew = false
                    )
                }
            }
            location.value?.let { location ->
                locationRepository.saveLocation(location)
            }
        }
    }

    fun deleteLocationIfNew() {
        viewModelScope.launch {
            if (locationId == null) return@launch
            locationRepository.deleteLocationIfNew(locationId)
        }
    }
}