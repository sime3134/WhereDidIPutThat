package sime3134.github.io.wheredidiputthat.view.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sime3134.github.io.wheredidiputthat.R
import sime3134.github.io.wheredidiputthat.databinding.FragmentAddLocationSetupBinding

class SetupAddLocationFragment: Fragment() {
    private var _binding: FragmentAddLocationSetupBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentAddLocationSetupBinding was null"
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            continueButton.setOnClickListener { navigateOrShowError() }
            titleField.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    navigateOrShowError()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun navigateOrShowError() {
        val title = binding.titleField.text.toString()
        if (title.isNotEmpty()) {
            findNavController().navigate(
                SetupAddLocationFragmentDirections.showAddRoomSetup(title)
            )
        } else {
            binding.titleField.error = getString(R.string.error_empty_title_field)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentAddLocationSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}