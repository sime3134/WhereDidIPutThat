package sime3134.github.io.wheredidiputthat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sime3134.github.io.wheredidiputthat.databinding.CarouselItemRoomBinding
import sime3134.github.io.wheredidiputthat.model.Room

class CarouselItemHolder(
    val binding: CarouselItemRoomBinding
) : RecyclerView.ViewHolder(binding.root) {

}

class HomeCarouselAdapter(
    private val rooms: List<Room>
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
        return CarouselItemHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselItemHolder, position: Int) {
        val item = rooms[position]
        holder.binding.carouselTextView.text = item.title
    }

    override fun getItemCount(): Int = rooms.size
}