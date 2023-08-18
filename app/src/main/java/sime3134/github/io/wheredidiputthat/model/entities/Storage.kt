package sime3134.github.io.wheredidiputthat.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Room::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["roomId"])]
)
data class Storage(
    val title: String,
    val roomId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String = "",
    val photo: String? = null,
    val isNew: Boolean = true
) {
    override fun toString(): String {
        return title
    }
}
