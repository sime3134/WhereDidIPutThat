package sime3134.github.io.wheredidiputthat.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
foreignKeys = [
        ForeignKey(
            entity = Location::class,
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("locationId")]
)
data class Room(
    val title: String,
    var locationId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photo: String? = null,
    var isNew : Boolean = true
) {
    override fun toString(): String = title
}
