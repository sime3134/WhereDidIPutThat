package sime3134.github.io.wheredidiputthat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.carousel.CarouselLayoutManager
import sime3134.github.io.wheredidiputthat.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentHomeBinding was null"
        }

    private val homeViewModel: HomeViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            carouselRecyclerView.layoutManager = CarouselLayoutManager()

            val rooms = homeViewModel.placeholders
            carouselRecyclerView.adapter = HomeCarouselAdapter(rooms)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}