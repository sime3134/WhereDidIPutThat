package sime3134.github.io.wheredidiputthat.view.setup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.databinding.FragmentAddRoomSetupBinding
import sime3134.github.io.wheredidiputthat.model.SettingsRepository
import sime3134.github.io.wheredidiputthat.model.entities.Location
import sime3134.github.io.wheredidiputthat.model.entities.Room
import sime3134.github.io.wheredidiputthat.view.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class SetupAddRoomFragment: Fragment() {

    @Inject lateinit var settingsRepository: SettingsRepository

    private var _binding: FragmentAddRoomSetupBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentAddRoomSetupBinding was null"
        }

    private val args: SetupAddRoomFragmentArgs by navArgs()

    private val viewModel: SetupAddRoomViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            finishButton.setOnClickListener {
                navigateOrShowError()
            }

            titleField.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    navigateOrShowError()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.status.collect { result ->
                result?.fold(
                    onSuccess = {
                        launchMainActivity()
                    },
                    onFailure = { error ->
                        error.printStackTrace()
                        Snackbar.make(
                            binding.root,
                            "An unknown error occurred",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
    }

    private fun navigateOrShowError() {
        val location = Location(args.locationTitle, isNew = false)
        val room = Room(binding.titleField.text.toString(), -1, isNew = false)
        if (room.title.isNotEmpty()) {
            viewModel.addLocationAndRoom(location, room)
        } else {
            binding.titleField.error = "Please enter a title"
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentAddRoomSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}