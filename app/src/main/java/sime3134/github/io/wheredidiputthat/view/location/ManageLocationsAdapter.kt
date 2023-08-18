package sime3134.github.io.wheredidiputthat.view.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sime3134.github.io.wheredidiputthat.R
import sime3134.github.io.wheredidiputthat.databinding.ListLocationItemBinding
import sime3134.github.io.wheredidiputthat.model.entities.Location

class LocationHolder(
    val binding: ListLocationItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        location: Location,
        onClick: (location: Location) -> Unit,
        onEllipsisClick: (view: View, location: Location, itemCount: Int) -> Unit,
        itemCount: Int
    ) {
        if (location.title.isNotEmpty()) {
            binding.circleLetter.text = location.title[0].toString()
            binding.titleText.text = location.title
        } else {
            binding.circleLetter.text = "?"
            binding.titleText.text = R.string.unknown.toString()
        }

        binding.root.setOnClickListener {
            onClick(location)
        }

        binding.ellipsisIcon.setOnClickListener {
            onEllipsisClick(binding.ellipsisIcon, location, itemCount)
        }
    }
}

class ManageLocationsAdapter(
    private val locations: List<Location>,
    private val onClick: (location: Location) -> Unit,
    private val onEllipsisClick: (view: View, location: Location, itemCount: Int) -> Unit
) : RecyclerView.Adapter<LocationHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : LocationHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListLocationItemBinding.inflate(
            inflater,
            parent,
            false
        )
        return LocationHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        val item = locations[position]
        holder.bind(item, onClick, onEllipsisClick, locations.size)
    }

    override fun getItemCount(): Int = locations.size
}