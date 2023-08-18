package sime3134.github.io.wheredidiputthat.view.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import sime3134.github.io.wheredidiputthat.databinding.FragmentManageLocationsBinding
import sime3134.github.io.wheredidiputthat.model.entities.Location

@AndroidEntryPoint
class ManageLocationsFragment: Fragment() {
    private var _binding: FragmentManageLocationsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentManageLocationsBinding was null"
        }

    private val manageLocationsViewModel: ManageLocationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding =
            FragmentManageLocationsBinding.inflate(inflater, container, false)

        binding.locationsRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddLocation.setOnClickListener {
            addLocation()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                manageLocationsViewModel.locations.collect {
                    binding.locationsRecyclerView.adapter = ManageLocationsAdapter(
                        it,
                        onClick = { location ->
                            findNavController().navigate(
                                ManageLocationsFragmentDirections.showEditLocation(location.id)
                            )
                        },
                        onEllipsisClick = { view, location, itemCount ->
                            showPopupMenu(view, location, itemCount)
                        }
                    )
                }
            }
        }
    }

    private fun showPopupMenu(view: View, location: Location, numberOfLocations: Int) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menu.add(Menu.NONE, location.id.toInt(), Menu.NONE, getString(R.string.delete))
        popupMenu.setOnMenuItemClickListener { menuItem ->
            if(numberOfLocations > 1) {
                if (menuItem.itemId == location.id.toInt()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        manageLocationsViewModel.deleteLocation(location)
                        Snackbar.make(
                            binding.root,
                            getString(R.string.location_deleted),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
                true
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.cannot_delete_last_location),
                    Snackbar.LENGTH_LONG
                ).show()
                false
            }
        }
        popupMenu.show()
    }

    private fun addLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            val locationId = manageLocationsViewModel.insertLocation()
            findNavController().navigate(
                ManageLocationsFragmentDirections.showEditLocation(
                    locationId
                )
            )
        }
    }
}