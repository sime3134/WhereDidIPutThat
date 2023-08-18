package sime3134.github.io.wheredidiputthat.view.setup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import sime3134.github.io.wheredidiputthat.databinding.ActivitySetupBinding

@AndroidEntryPoint
class SetupActivity : AppCompatActivity() {
    private var _binding: ActivitySetupBinding? = null

    private val binding
        get() = checkNotNull(_binding) {
            "ActivityWelcomeBinding was null"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        _binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}