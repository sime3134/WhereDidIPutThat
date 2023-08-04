package sime3134.github.io.wheredidiputthat.model

import java.util.UUID

data class Room(
    val title: String = "",
    val id: String = UUID.randomUUID().toString(),
    val location: Int = -1,
    val description: String = "",
    val photo: String? = null
)
