package sime3134.github.io.wheredidiputthat.view.storage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import sime3134.github.io.wheredidiputthat.R
import sime3134.github.io.wheredidiputthat.databinding.ListCardBinding
import sime3134.github.io.wheredidiputthat.model.entities.Item
import sime3134.github.io.wheredidiputthat.utility.getScaledBitmap
import java.io.File

class CardHolder(
    val binding: ListCardBinding,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
            item: Item,
            onClick: (Item) -> Unit,
            onEllipsisClick: (view: View, item: Item) -> Unit,
        ) {
        binding.apply {
            title.text = item.title
            description.text = item.description
            if(item.photo != null) {
                setPhoto(item.photo, binding.photo)
            } else {
                binding.photo.setImageResource(R.drawable.ic_image)
            }

            root.setOnClickListener {
                onClick(item)
            }

            binding.ellipsisIcon.setOnClickListener {
                onEllipsisClick(it, item)
            }
        }
    }

    private fun setPhoto(photoFileName: String, photoView: ImageView) {
        val photoFile = File(context.filesDir, photoFileName)

        if(photoFile.exists()) {
            photoView.doOnLayout { measuredView ->
                val scaledBitmap = getScaledBitmap(
                    photoFile.path,
                    measuredView.width,
                    measuredView.height
                )
                photoView.setImageBitmap(scaledBitmap)
            }
        } else {
            photoView.setImageResource(R.drawable.ic_image)
        }
    }
}

class ViewStorageItemAdapter(
    private val items: List<Item>,
    private val onClick: (Item) -> Unit,
    private val onEllipsisClick: (view: View, item: Item) -> Unit,
) : RecyclerView.Adapter<CardHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : CardHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListCardBinding.inflate(
            inflater,
            parent,
            false
        )
        return CardHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onClick, onEllipsisClick)
    }

    override fun getItemCount(): Int = items.size
}