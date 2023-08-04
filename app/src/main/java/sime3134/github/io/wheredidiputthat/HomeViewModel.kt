package sime3134.github.io.wheredidiputthat

import androidx.lifecycle.ViewModel
import sime3134.github.io.wheredidiputthat.model.Room

class HomeViewModel : ViewModel() {

    val placeholders = mutableListOf<Room>()

    init {
        for (i in 0..10) {
            placeholders.add(Room("Room $i"))
        }
    }
}