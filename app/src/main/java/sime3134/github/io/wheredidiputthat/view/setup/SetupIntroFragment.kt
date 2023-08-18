package sime3134.github.io.wheredidiputthat.view.setup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import sime3134.github.io.wheredidiputthat.databinding.FragmentIntroSetupBinding

class SetupIntroFragment: Fragment() {
    private var _binding: FragmentIntroSetupBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentIntroSetupBinding was null"
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            getStartedButton.setOnClickListener {
                findNavController().navigate(SetupIntroFragmentDirections.showAddLocationSetup())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentIntroSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}