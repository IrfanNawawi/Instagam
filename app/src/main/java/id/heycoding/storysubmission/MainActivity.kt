package id.heycoding.storysubmission

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import id.heycoding.storysubmission.utils.Preferences

class MainActivity : AppCompatActivity() {

    lateinit var userLoginPref: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userLoginPref = Preferences(this)
        supportActionBar?.hide()
        checkSession()
        setupView()
    }

    private fun checkSession() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController
        if (userLoginPref.getLoginData().isLogin) {
            navController.navigateUp()
            navController.navigate(R.id.action_welcomeFragment_to_homeFragment)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mapsStory -> {
                findNavController(R.id.container).navigate(R.id.action_homeFragment_to_mapsFragment)
                true
            }
            R.id.settingLanguage -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logoutMenu -> {
                doLogout()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun doLogout() {
        findNavController(R.id.container).navigate(R.id.action_homeFragment_to_welcomeFragment)
        userLoginPref.logout()
    }
}