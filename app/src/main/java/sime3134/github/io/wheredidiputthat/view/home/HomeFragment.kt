package sime3134.github.io.wheredidiputthat.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.R
import sime3134.github.io.wheredidiputthat.databinding.FragmentHomeBinding
import sime3134.github.io.wheredidiputthat.model.SettingsRepository
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentHomeBinding was null"
        }

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private lateinit var locationPopup: PopupMenu

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            carouselRecyclerView.layoutManager = CarouselLayoutManager()
            recentItemsRecyclerView.layoutManager = LinearLayoutManager(context)
        }

        val toolbar = binding.topAppBar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val drawer: DrawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            activity,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        locationPopup = PopupMenu(requireContext(), binding.fragmentTitleContainer)

        locationPopup.setOnMenuItemClickListener { menuItem ->
            homeViewModel.setLocation(menuItem.itemId.toLong())
            true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.apply {
            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit_item -> {
                        findNavController().navigate(
                            HomeFragmentDirections.showManageLocations()
                        )
                        true
                    }
                    else -> false
                }
            }

            fragmentTitleContainer.setOnClickListener {
                locationPopup.show()
            }

            fabAddRoom.setOnClickListener {
                addRoom()
            }

            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.help -> {
                        showRoomsPrompt()
                        true
                    }
                    else -> false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val isFirstRun = settingsRepository.isFirstRun().first()
            if (isFirstRun) {
                launch(Dispatchers.Main) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.if_you_need_a_hand),
                        Snackbar.LENGTH_LONG
                    ).setAction(R.string.show_now) {
                        showRoomsPrompt()
                    }.show()
                }
                settingsRepository.setFirstRun(false)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.location.collect {
                        if(it != null) {
                            binding.fragmentTitle.text = it.title
                        }
                    }
                }

                launch {
                    homeViewModel.locations.collect {
                        if(it.isNotEmpty()) {
                            var found = false
                            locationPopup.menu.clear()
                            it.forEach { location ->
                                locationPopup.menu.add(
                                    Menu.NONE,
                                    location.id.toInt(),
                                    Menu.NONE,
                                    location.title
                                )
                                if(location.id == homeViewModel.locationId.value) {
                                    found = true
                                }
                            }
                            if(!found) {
                                homeViewModel.setLocation(it[0].id)
                            }
                        }
                    }
                }

                launch {
                    homeViewModel.recentItems.collect {
                        if(it.isNotEmpty()) {
                            binding.recentItemsRecyclerView.adapter = HomeRecentItemsAdapter(it)
                            binding.recentItemsRecyclerView.visibility = View.VISIBLE
                            binding.recentItemsPlaceholderContainer.visibility = View.GONE
                        } else {
                            binding.recentItemsRecyclerView.visibility = View.GONE
                            binding.recentItemsPlaceholderContainer.visibility = View.VISIBLE
                        }
                    }
                }

                launch {
                    homeViewModel.rooms.collect {
                        binding.carouselRecyclerView.adapter = HomeCarouselAdapter(it) { room ->
                            findNavController().navigate(HomeFragmentDirections.showViewRoom(room.id)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun addRoom() {
        viewLifecycleOwner.lifecycleScope.launch {
            val roomId = homeViewModel.insertRoom()
            if (roomId == null) {
                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.error_creating_room),
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.try_again) {
                    addRoom()
                }
                snackbar.anchorView = binding.fabAddRoom
                snackbar.show()
                return@launch
            }
            findNavController().navigate(
                HomeFragmentDirections.showEditRoom(roomId, homeViewModel.locationId.value?:0)
            )
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showRoomsPrompt() {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(binding.carouselRecyclerView)
            .setPrimaryText("Rooms")
            .setSecondaryText("Swipe to browse your rooms and tap to enter")
            .setFocalRadius(80f)
            .setPromptBackground(RectanglePromptBackground())
            .setPromptFocal(RectanglePromptFocal())
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED ||
                    state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    showAddRoomPrompt()
                }
            }
            .show()
    }

    private fun showSearchPrompt() {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(binding.topAppBar.findViewById(R.id.search))
            .setPrimaryText("Search")
            .setSecondaryText("Locate a specific room, storage or item")
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED ||
                    state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    showMenuPrompt()
                }
            }
            .show()
    }

    private fun showAddRoomPrompt() {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(binding.fabAddRoom)
            .setPrimaryText("Add Room")
            .setSecondaryText("Add a new room to your current location")
            .setPromptBackground(RectanglePromptBackground())
            .setPromptFocal(RectanglePromptFocal())
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED ||
                    state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                    showSearchPrompt()
                }
            }
            .show()
    }

    private fun showMenuPrompt() {
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(binding.topAppBar.getChildAt(0))
            .setPrimaryText("Menu")
            .setSecondaryText("Edit locations, change settings and more")
            .show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.app_bar_menu_home, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }
}