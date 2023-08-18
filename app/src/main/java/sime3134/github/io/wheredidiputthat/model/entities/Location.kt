package sime3134.github.io.wheredidiputthat.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    val title: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var isNew : Boolean = true
) {
    override fun toString(): String = title
}