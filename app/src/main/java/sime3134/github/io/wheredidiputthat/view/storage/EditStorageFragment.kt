package sime3134.github.io.wheredidiputthat.view.storage

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
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
import sime3134.github.io.wheredidiputthat.databinding.FragmentEditStorageBinding
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.utility.getScaledBitmap
import java.io.File
import java.util.Date


@AndroidEntryPoint
class EditStorageFragment : Fragment() {
    private var _binding: FragmentEditStorageBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentEditStorageBinding was null"
        }

    private val editStorageViewModel: EditStorageViewModel by viewModels()
    private val args: EditStorageFragmentArgs by navArgs()

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
            didTakePhoto: Boolean ->
        if(didTakePhoto && photoName != null) {
            editStorageViewModel.updateStorage { oldStorage ->
                Log.d("EditStorageFragment", "Updating storage with photo: $photoName")
                oldStorage.copy(
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
        _binding =
            FragmentEditStorageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                editStorageViewModel.deleteStorageIfNew()
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        })

        binding.apply {
            cameraButton.setOnClickListener {
                photoName = "storage_${Date()}.jpg"
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

            (roomFieldLayout.editText as? AutoCompleteTextView)?.setOnItemClickListener { adapterView, view, i, l ->
                val selectedItem = adapterView.getItemAtPosition(i) as? Room
                editStorageViewModel.updateStorage { oldStorage ->
                    selectedItem?.id?.let {
                        oldStorage.copy(
                            roomId = it
                        )
                    }
                }
            }

            locationFieldLayout.editText?.doOnTextChanged { text, _, _, _ ->
                editStorageViewModel.locations.value.find { it.title == text.toString() }?.id?.let {
                    roomFieldLayout.editText?.text = null
                    editStorageViewModel.onLocationSelected(it)
                }
            }

            fabSave.setOnClickListener {
                if(titleField.text.toString().isEmpty()) {
                    titleField.error = getString(R.string.error_empty_title_field)
                    return@setOnClickListener
                }
                if(roomFieldLayout.editText?.text?.isBlank() == true) {
                    roomFieldLayout.error = getString(R.string.error_empty_room_field)
                    return@setOnClickListener
                }
                if(locationFieldLayout.editText?.text?.isBlank() == true) {
                    locationFieldLayout.error = getString(R.string.error_empty_location_field)
                    return@setOnClickListener
                }
                editStorageViewModel.saveStorage(titleField.text.toString(), descriptionField.text.toString())
                Snackbar.make(requireView(), getString(R.string.storage_saved), Snackbar.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editStorageViewModel.locations.collect { locations ->
                    if(locations.isNotEmpty()) {
                        val autoCompleteTextView =
                            binding.locationFieldLayout.editText as? AutoCompleteTextView
                        autoCompleteTextView?.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                R.layout.spinner_item_location,
                                locations
                            )
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editStorageViewModel.room.collect { room ->
                    if(room != null) {
                        val autoCompleteTextView =
                            binding.locationFieldLayout.editText as? AutoCompleteTextView

                        if (savedInstanceState?.getLong("locationDropdownId") != null) {
                            autoCompleteTextView?.setText(
                                savedInstanceState.getLong("locationDropdownId").let { locationId ->
                                    editStorageViewModel.locations.value.find { it.id == locationId }?.title
                                }, false
                            )
                        } else {
                            autoCompleteTextView?.setText(editStorageViewModel.locations.value.find {
                                it.id == room.locationId
                            }?.title, false)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editStorageViewModel.rooms.collect { rooms ->

                    if(rooms.isNotEmpty()) {

                        val autoCompleteTextView =
                            binding.roomFieldLayout.editText as? AutoCompleteTextView
                        autoCompleteTextView?.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                R.layout.spinner_item_location,
                                rooms
                            )
                        )

                        if (savedInstanceState?.getLong("roomDropdownId") != null) {
                            autoCompleteTextView?.setText(
                                savedInstanceState.getLong("roomDropdownId").let { roomId ->
                                    rooms.find { it.id == roomId }?.title
                                }, false
                            )
                        } else {
                            autoCompleteTextView?.setText(
                                rooms.find { it.id == args.roomId }?.title,
                                false
                            )
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editStorageViewModel.storage.collect { storage ->
                    if(storage != null) {
                        binding.apply {
                            if (storage.isNew) {
                                fragmentTitle.setText(R.string.add_storage)
                            } else {
                                fragmentTitle.setText(R.string.edit_storage)
                            }
                            if (titleField.text.toString() != storage.title) {
                                titleField.setText(storage.title)
                            }
                            if (descriptionField.text.toString() != storage.description) {
                                descriptionField.setText(storage.description)
                            }
                        }
                        updatePhoto(storage.photo)
                    }
                }
            }
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
        editStorageViewModel.rooms.value.find { it.title == binding.roomFieldLayout.editText?.text.toString() }?.id?.let {
            outState.putLong("roomDropdownId", it)
        }
        editStorageViewModel.locations.value.find { it.title == binding.locationFieldLayout.editText?.text.toString() }?.id?.let {
            outState.putLong("locationDropdownId", it)
        }
    }
}