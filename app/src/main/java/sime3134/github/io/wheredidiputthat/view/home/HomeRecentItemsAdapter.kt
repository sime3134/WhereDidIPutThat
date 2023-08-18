package sime3134.github.io.wheredidiputthat.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sime3134.github.io.wheredidiputthat.databinding.ListRecentItemsItemBinding
import sime3134.github.io.wheredidiputthat.model.entities.Item

class RecentItemHolder(
    val binding: ListRecentItemsItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Item) {
        binding.itemTitle.text = item.title
        //TODO: Add location to item
        binding.itemLocation.text = "Location"
    }
}

class HomeRecentItemsAdapter(
    private val rooms: List<Item>
) : RecyclerView.Adapter<RecentItemHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : RecentItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRecentItemsItemBinding.inflate(
            inflater,
            parent,
            false
        )
        return RecentItemHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentItemHolder, position: Int) {
        val item = rooms[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = rooms.size
}