package sime3134.github.io.wheredidiputthat.model

import java.util.UUID

data class Storage(
    val title: String = "",
    val id: String = UUID.randomUUID().toString(),
    val room: Int = -1,
    val description: String = "",
    val photo: String? = null
)
