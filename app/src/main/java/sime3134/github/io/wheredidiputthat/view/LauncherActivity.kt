package sime3134.github.io.wheredidiputthat.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sime3134.github.io.wheredidiputthat.model.SettingsRepository
import sime3134.github.io.wheredidiputthat.view.setup.SetupActivity
import javax.inject.Inject

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            //TODO: Remove this when the app is ready
            //settingsRepository.resetPreferences()
            //applicationContext.deleteDatabase("database")
            val isFirstRunValue = settingsRepository.isFirstRun().first()
            val intent: Intent = if (isFirstRunValue) {
                Intent(this@LauncherActivity, SetupActivity::class.java)
            } else {
                Intent(this@LauncherActivity, MainActivity::class.java)
            }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
        }
    }
}