package sime3134.github.io.wheredidiputthat.view.storage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.R
import sime3134.github.io.wheredidiputthat.databinding.FragmentViewStorageBinding
import sime3134.github.io.wheredidiputthat.model.entities.Item
import sime3134.github.io.wheredidiputthat.utility.getScaledBitmap
import java.io.File

@AndroidEntryPoint
class ViewStorageFragment: Fragment() {
    private var _binding: FragmentViewStorageBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentViewStorageBinding was null"
        }

    private val viewStorageViewModel: ViewStorageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding =
            FragmentViewStorageBinding.inflate(inflater, container, false)

        binding.itemList.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddItem.setOnClickListener {
            addItem()
        }

        binding.editButton.setOnClickListener {
            val storage = viewStorageViewModel.storage.value
            if(storage != null) {
                findNavController().navigate(
                    ViewStorageFragmentDirections.showEditStorage(
                        storage.id,
                        storage.roomId
                    )
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewStorageViewModel.storage.collect { storage ->
                        if(storage != null) {
                            binding.apply {
                                fragmentTitle.text = storage.title
                                descriptionText.text = storage.description
                                updatePhoto(storage.photo)
                                viewStorageViewModel.getRoom(storage.roomId)
                                locationTreeText.text = resources.getString(
                                    R.string.storage_location_tree,
                                    viewStorageViewModel.location.value?.title,
                                    viewStorageViewModel.room.value?.title,
                                    storage?.title
                                )
                            }
                        }
                    }
                }

                launch {
                    viewStorageViewModel.room.collect { room ->
                        if(room != null) {
                            viewStorageViewModel.getLocation(room.locationId)
                            binding.locationTreeText.text = resources.getString(
                                R.string.storage_location_tree,
                                viewStorageViewModel.location.value?.title,
                                room.title,
                                viewStorageViewModel.storage.value?.title
                            )
                        }
                    }
                }

                launch {
                    viewStorageViewModel.location.collect { location ->
                        if(location != null) {
                            binding.locationTreeText.text = resources.getString(
                                R.string.storage_location_tree,
                                location?.title,
                                viewStorageViewModel.room.value?.title,
                                viewStorageViewModel.storage.value?.title
                            )
                        }
                    }
                }

                launch {
                    viewStorageViewModel.items.collect { items ->
                        if(items != null) {
                            binding.itemList.adapter = ViewStorageItemAdapter(items, onClick = { item ->
                                findNavController().navigate(
                                    ViewStorageFragmentDirections.showEditItem(item.storageId,
                                        viewStorageViewModel.room.value?.id ?: return@ViewStorageItemAdapter,
                                        item.id,)
                                )
                            }, onEllipsisClick = { view, item ->
                                showPopupMenu(view, item)
                            })
                        }
                    }
                }
            }
        }
    }

    private fun showPopupMenu(view: View, item: Item) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menu.add(Menu.NONE, item.id.toInt(), Menu.NONE, getString(R.string.delete))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == item.id.toInt()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewStorageViewModel.deleteItem(item)
                    Snackbar.make(
                        binding.root,
                        getString(R.string.storage_deleted),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun addItem() {
        viewLifecycleOwner.lifecycleScope.launch {
            val itemId = viewStorageViewModel.insertItem()
            if (itemId == null) {
                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.error_creating_item),
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.try_again) {
                    addItem()
                }
                snackbar.anchorView = binding.fabAddItem
                snackbar.show()
                return@launch
            }
            findNavController().navigate(
                ViewStorageFragmentDirections.showEditItem(
                    viewStorageViewModel.storage.value?.id ?: return@launch,
                    viewStorageViewModel.room.value?.id ?: return@launch,
                    itemId,
                )
            )
        }
    }

    private fun updatePhoto(photoFileName: String?) {
        binding.apply {
            if(photo.tag == photoFileName) {
                return
            }
            val photoFile = photoFileName?.let {
                File(requireContext().filesDir, it)
            }

            if(photoFile?.exists() == true) {
                photo.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    photo.setImageBitmap(scaledBitmap)
                    photo.tag = photoFileName
                }
            } else {
                photo.setImageResource(R.drawable.ic_image)
                photo.tag = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}