package sime3134.github.io.wheredidiputthat.view.item

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.R
import sime3134.github.io.wheredidiputthat.databinding.FragmentEditItemBinding
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.model.entities.Storage
import sime3134.github.io.wheredidiputthat.utility.getScaledBitmap
import java.io.File
import java.util.Date

@AndroidEntryPoint
class EditItemFragment : Fragment() {
    private var _binding: FragmentEditItemBinding? = null

    private val binding
        get() = checkNotNull(_binding) { "FragmentEditItemBinding was null" }

    private val editItemViewModel: EditItemViewModel by viewModels()
    private val args: EditItemFragmentArgs by navArgs()

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
            didTakePhoto: Boolean ->
        if(didTakePhoto && photoName != null) {
            editItemViewModel.updateItem { oldItem ->
                oldItem.copy(
                    photo = photoName
                )
            }
        }
    }

    private var photoName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding =
            FragmentEditItemBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                editItemViewModel.deleteItemIfNew()
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        })

        binding.apply {
            cameraButton.setOnClickListener {
                photoName = "item_${Date()}.jpg"
                val photoFile = photoName?.let { it1 -> File(requireContext().filesDir, it1) }
                val photoUri = photoFile?.let { it1 ->
                    FileProvider.getUriForFile(
                        requireContext(),
                        "sime3134.github.io.wheredidiputthat.fileprovider",
                        it1
                    )
                }
                takePhoto.launch(photoUri)
            }

            val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraButton.isEnabled = canResolveIntent(captureImageIntent)

            (storageFieldLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { adapterView, _, i, _ ->
                val selectedItem = adapterView.getItemAtPosition(i) as? Storage
                editItemViewModel.updateItem { oldItem ->
                    selectedItem?.id?.let {
                        oldItem.copy(
                            storageId = it
                        )
                    }
                }
            }

            (roomFieldLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { adapterView, _, i, _ ->
                val selectedItem = adapterView.getItemAtPosition(i) as? Room
                storageFieldLayout.editText?.text = null
                selectedItem?.id?.let { editItemViewModel.onRoomSelected(it) }
            }

            (locationFieldLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { adapterView, _, i, _ ->
                val selectedItem = adapterView.getItemAtPosition(i) as? Location
                roomFieldLayout.editText?.text = null
                storageFieldLayout.editText?.text = null
                if (selectedItem != null) {
                    editItemViewModel.onLocationSelected(selectedItem.id)
                }
            }

            fabSave.setOnClickListener {
                if(titleField.text.toString().isEmpty()) {
                    titleField.error = getString(R.string.error_empty_title_field)
                    return@setOnClickListener
                }
                if(storageFieldLayout.editText?.text?.isBlank() == true) {
                    storageFieldLayout.error = getString(R.string.error_empty_storage_field)
                    return@setOnClickListener
                }
                if(roomFieldLayout.editText?.text?.isBlank() == true) {
                    roomFieldLayout.error = getString(R.string.error_empty_storage_field)
                    return@setOnClickListener
                }
                if(locationFieldLayout.editText?.text?.isBlank() == true) {
                    locationFieldLayout.error = getString(R.string.error_empty_location_field)
                    return@setOnClickListener
                }
                editItemViewModel.saveItem(titleField.text.toString(), descriptionField.text.toString())
                Snackbar.make(requireView(), getString(R.string.item_saved), Snackbar.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    editItemViewModel.item.collect { item ->
                        if(item != null) {
                            binding.apply {
                                if (item.isNew) {
                                    fragmentTitle.setText(R.string.add_item)
                                } else {
                                    fragmentTitle.setText(R.string.edit_item)
                                }
                                if (titleField.text.toString() != item.title) {
                                    titleField.setText(item.title)
                                }
                                if (descriptionField.text.toString() != item.description) {
                                    descriptionField.setText(item.description)
                                }
                            }
                            updatePhoto(item.photo)
                        }
                    }
                }
            }
        }
    }

    private fun setupLocationsDropdown(locationId: Long) {
        val autoCompleteTextView =
            binding.locationFieldLayout.editText as? AutoCompleteTextView
        autoCompleteTextView?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_item_location,
                editItemViewModel.locations.value
            )
        )

        val location = editItemViewModel.locations.value.find {
            it.id == locationId
        }

        autoCompleteTextView?.setText(location?.title, false)

        setupRoomsDropdown(locationId)
    }

    private fun setupRoomsDropdown(locationId: Long) {
        val autoCompleteTextView =
            binding.roomFieldLayout.editText as? AutoCompleteTextView
        autoCompleteTextView?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_item_location,
                editItemViewModel.rooms.value
            )
        )

        val room = editItemViewModel.rooms.value.find {
            it.locationId == locationId
        }

        autoCompleteTextView?.setText(room?.title, false)

        setupStoragesDropdown(room?.id)
    }

    private fun setupStoragesDropdown(id: Long?) {
        val autoCompleteTextView =
            binding.storageFieldLayout.editText as? AutoCompleteTextView
        autoCompleteTextView?.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_item_location,
                editItemViewModel.storages.value
            )
        )

        val storage = editItemViewModel.storages.value.find {
            it.roomId == id
        }

        autoCompleteTextView?.setText(storage?.title, false)
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

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        return intent.resolveActivity(packageManager) != null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        editItemViewModel.rooms.value.find { it.title == binding.roomFieldLayout.editText?.text.toString() }?.id?.let {
            outState.putLong("roomDropdownId", it)
        }
        editItemViewModel.locations.value.find { it.title == binding.locationFieldLayout.editText?.text.toString() }?.id?.let {
            outState.putLong("locationDropdownId", it)
        }
        editItemViewModel.storages.value.find { it.title == binding.storageFieldLayout.editText?.text.toString() }?.id?.let {
            outState.putLong("storageDropdownId", it)
        }
    }
}