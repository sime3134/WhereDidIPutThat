package sime3134.github.io.wheredidiputthat.view.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import sime3134.github.io.wheredidiputthat.databinding.FragmentEditLocationBinding

@AndroidEntryPoint
class EditLocationFragment : Fragment() {
    private var _binding: FragmentEditLocationBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentEditLocationBinding was null"
        }

    private val args: EditLocationFragmentArgs by navArgs()
    private val editLocationViewModel: EditLocationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentEditLocationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                editLocationViewModel.deleteLocationIfNew()
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        })

        binding.apply {
            titleField.doOnTextChanged { text, _, _, _ ->
                editLocationViewModel.updateLocation { oldLocation ->
                    oldLocation.copy(
                        title = text.toString()
                    )
                }
            }

            fabSave.setOnClickListener {
                if(titleField.text.toString().isEmpty()) {
                    titleField.error = getString(R.string.error_empty_title_field)
                    return@setOnClickListener
                }
                editLocationViewModel.saveLocation()
                Snackbar.make(requireView(), getString(R.string.location_saved), Snackbar.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editLocationViewModel.location.collect { location ->
                    binding.apply {
                        if(location?.isNew == true) {
                            fragmentTitle.setText(R.string.add_location)
                        } else {
                            fragmentTitle.setText(R.string.edit_location)
                        }
                        if(titleField.text.toString() != location?.title) {
                            titleField.setText(location?.title)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}