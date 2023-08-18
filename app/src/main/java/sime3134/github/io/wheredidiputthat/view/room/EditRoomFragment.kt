package sime3134.github.io.wheredidiputthat.view.room

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
import sime3134.github.io.wheredidiputthat.databinding.FragmentEditRoomBinding
import sime3134.github.io.wheredidiputthat.utility.getScaledBitmap
import java.io.File
import java.util.Date

@AndroidEntryPoint
class EditRoomFragment: Fragment() {
    private var _binding: FragmentEditRoomBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentEditRoomBinding was null"
        }

    private val args: EditRoomFragmentArgs by navArgs()
    private val editRoomViewModel: EditRoomViewModel by viewModels()

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        didTakePhoto: Boolean ->
        if(didTakePhoto && photoName != null) {
            editRoomViewModel.updateRoom { oldRoom ->
                oldRoom.copy(
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
            FragmentEditRoomBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                editRoomViewModel.deleteRoomIfNew()
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        })

        binding.apply {
            cameraButton.setOnClickListener {
                photoName = "room_${Date()}.jpg"
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

            titleField.doOnTextChanged { text, _, _, _ ->
                editRoomViewModel.updateRoom { oldRoom ->
                    oldRoom.copy(
                        title = text.toString()
                    )
                }
            }

            locationFieldLayout.editText?.doOnTextChanged { text, _, _, _ ->
                editRoomViewModel.updateRoom { oldRoom ->
                    editRoomViewModel.locations.value.find { it.title == text.toString() }?.id?.let {
                        if(oldRoom.locationId == it) {
                            return@let oldRoom
                        }
                        oldRoom.copy(
                            locationId = it
                        )
                    }
                }
            }

            fabSave.setOnClickListener {
                if(titleField.text.toString().isEmpty()) {
                    titleField.error = getString(R.string.error_empty_title_field)
                    return@setOnClickListener
                }
                if(locationFieldLayout.editText?.text == null) {
                    locationFieldLayout.error = getString(R.string.error_empty_location_field)
                    return@setOnClickListener
                }
                editRoomViewModel.saveRoom()
                Snackbar.make(requireView(), getString(R.string.room_saved), Snackbar.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    editRoomViewModel.locations.collect { locations ->
                        if(locations.isEmpty()) {
                            return@collect
                        }

                        val autoCompleteTextView =
                            binding.locationFieldLayout.editText as? AutoCompleteTextView
                        autoCompleteTextView?.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                R.layout.spinner_item_location,
                                locations
                            )
                        )

                        if(savedInstanceState?.getLong("locationDropdownId") != null) {
                            autoCompleteTextView?.setText(
                                savedInstanceState.getLong("locationDropdownId").let { locationId ->
                                    locations.find { it.id == locationId }?.title
                            }, false)
                        } else {
                            autoCompleteTextView?.setText(locations.find { it.id == args.locationId }?.title, false)
                        }

                    }
                }

                launch {
                    editRoomViewModel.room.collect { room ->
                        binding.apply {
                            if(room?.isNew == true) {
                                fragmentTitle.setText(R.string.add_room)
                            } else {
                                fragmentTitle.setText(R.string.edit_room)
                            }
                            if(titleField.text.toString() != room?.title) {
                                titleField.setText(room?.title)
                            }
                            updatePhoto(room?.photo)
                        }
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
        editRoomViewModel.locations.value.find { it.title == binding.locationFieldLayout.editText?.text.toString() }?.id?.let {
            outState.putLong("locationDropdownId", it)
        }
    }
}