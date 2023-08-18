package sime3134.github.io.wheredidiputthat.view.room

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
import sime3134.github.io.wheredidiputthat.databinding.FragmentViewRoomBinding
import sime3134.github.io.wheredidiputthat.model.entities.Storage
import sime3134.github.io.wheredidiputthat.utility.getScaledBitmap
import java.io.File

@AndroidEntryPoint
class ViewRoomFragment: Fragment() {
    private var _binding: FragmentViewRoomBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentViewRoomBinding was null"
        }

    private val viewRoomViewModel: ViewRoomViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding =
            FragmentViewRoomBinding.inflate(inflater, container, false)

        binding.storageList.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddStorage.setOnClickListener {
            addStorage()
        }

        binding.editButton.setOnClickListener {
            val room = viewRoomViewModel.room.value
            if(room != null) {
                findNavController().navigate(
                    ViewRoomFragmentDirections.showEditRoom(
                        room.id,
                        room.locationId
                    )
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewRoomViewModel.room.collect { room ->
                        binding.apply {
                            fragmentTitle.text = room?.title
                            updatePhoto(room?.photo)
                            if(room != null) {
                                viewRoomViewModel.getLocation(room.locationId)
                            }
                            locationTreeText.text = resources.getString(R.string.room_location_tree,
                                viewRoomViewModel.location.value?.title, room?.title)
                        }
                    }
                }

                launch {
                    viewRoomViewModel.location.collect { location ->
                        binding.locationTreeText.text = resources.getString(R.string.room_location_tree,
                            location?.title, viewRoomViewModel.room.value?.title)
                    }
                }

                launch {
                    viewRoomViewModel.storage.collect {
                        binding.storageList.adapter = ViewRoomStorageAdapter(
                            it,
                            onClick = { storage ->
                                findNavController().navigate(ViewRoomFragmentDirections.showViewStorage(storage.id))
                            },
                            onEllipsisClick = { view, storage ->
                                showPopupMenu(view, storage)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun showPopupMenu(view: View, storage: Storage) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menu.add(Menu.NONE, storage.id.toInt(), Menu.NONE, getString(R.string.delete))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == storage.id.toInt()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewRoomViewModel.deleteStorage(storage)
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

    private fun addStorage() {
        viewLifecycleOwner.lifecycleScope.launch {
            val storageId = viewRoomViewModel.insertStorage()
            if (storageId == null) {
                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.error_creating_storage),
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.try_again) {
                    addStorage()
                }
                snackbar.anchorView = binding.fabAddStorage
                snackbar.show()
                return@launch
            }
            findNavController().navigate(
                ViewRoomFragmentDirections.showEditStorage(
                    storageId,
                    viewRoomViewModel.room.value?.id ?: return@launch,
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