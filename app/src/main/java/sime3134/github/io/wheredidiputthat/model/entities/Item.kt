package sime3134.github.io.wheredidiputthat.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Storage::class,
            parentColumns = ["id"],
            childColumns = ["storageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("storageId")])
data class Item(
    val title: String,
    val storageId: Long,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String = "",
    val photo: String? = null,
    val lastViewed: Long? = null,
    val isNew: Boolean = true
)
