package sime3134.github.io.wheredidiputthat.view.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import sime3134.github.io.wheredidiputthat.R
import sime3134.github.io.wheredidiputthat.databinding.CarouselItemRoomBinding
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.utility.getScaledBitmap
import java.io.File

class CarouselItemHolder(
    private val binding: CarouselItemRoomBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(room: Room, onClick: (room: Room) -> Unit) {
        binding.carouselTextView.text = room.title
        if(room.photo != null) {
            setPhoto(room.photo, binding.carouselItemImage)
        } else {
            binding.carouselItemImage.setImageResource(R.drawable.carousel_room_placeholder)
        }

        binding.root.setOnClickListener {
            onClick(room)
        }
    }

    private fun setPhoto(photoFileName: String, carouselItemImage: ImageView) {
        val photoFile = File(context.filesDir, photoFileName)

        if(photoFile.exists()) {
            carouselItemImage.doOnLayout { measuredView ->
                val scaledBitmap = getScaledBitmap(
                    photoFile.path,
                    measuredView.width,
                    measuredView.height
                )
                carouselItemImage.setImageBitmap(scaledBitmap)
            }
        } else {
            carouselItemImage.setImageResource(R.drawable.carousel_room_placeholder)
        }
    }
}

class HomeCarouselAdapter(
    private val rooms: List<Room>,
    private val onClick: (room: Room) -> Unit
) : RecyclerView.Adapter<CarouselItemHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : CarouselItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CarouselItemRoomBinding.inflate(
            inflater,
            parent,
            false
        )
        return CarouselItemHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CarouselItemHolder, position: Int) {
        val room = rooms[position]
        holder.bind(room, onClick)
    }

    override fun getItemCount(): Int = rooms.size
}