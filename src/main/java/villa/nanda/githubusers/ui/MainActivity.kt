package villa.nanda.githubusers.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Response
import villa.nanda.githubusers.R
import villa.nanda.githubusers.data.response.SearchResponse
import villa.nanda.githubusers.data.response.UserItem
import villa.nanda.githubusers.data.retrofit.ApiConfig
import villa.nanda.githubusers.databinding.ActivityMainBinding
import villa.nanda.githubusers.helper.SettingPreferences
import villa.nanda.githubusers.helper.ViewModelFactory
import villa.nanda.githubusers.helper.dataStore

class MainActivity : AppCompatActivity() {

    private var list = ArrayList<UserItem>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var settingViewModel: SettingViewModel
    private var isDarkMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SettingPreferences.getInstance(application.dataStore)
        settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(application, pref)
        )[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(this) { isDarkMode: Boolean ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                this.isDarkMode = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        setSupportActionBar(binding.toolbar)
        binding.contentLayout.recylerView.setHasFixedSize(true)
        binding.contentLayout.emptyList.visibility = View.VISIBLE

        binding.contentLayout.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchUsername(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun searchUsername(username: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().searchUser(username)
        client.enqueue(object : retrofit2.Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        list = responseBody.items
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }

                if (list.isNotEmpty()){
                    binding.contentLayout.emptyList.visibility = View.GONE
                }

                showRecyclerList()
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showRecyclerList() {
        binding.contentLayout.recylerView.layoutManager = LinearLayoutManager(this)
        val adapter = UserAdapter(list, this@MainActivity){}

        binding.contentLayout.recylerView.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.contentLayout.progressCircular.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("InflateParams")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
                true
            }

            R.id.action_setting -> {
                showSettingDialog(this@MainActivity)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettingDialog(context: Context) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custom_setting, null)

        builder.setView(view)

        val titleTextView = view.findViewById<TextView>(R.id.text_info)
        val statusTheme = view.findViewById<TextView>(R.id.thema_status)
        val switchTheme = view.findViewById<SwitchCompat>(R.id.switch_theme)

        val spannedText: Spanned = HtmlCompat.fromHtml(
            "Submission 2nd Aplikasi Github Users <br> oleh Villa Nanda",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        var status = "Dark Mode On"
        if (!this.isDarkMode) {
            status = "Dark Mode Off"
        }

        titleTextView.text = spannedText
        statusTheme.text = status

        switchTheme.isChecked = this.isDarkMode

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
                this.isDarkMode = true
                status = "Dark Mode On"
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
                this.isDarkMode = false
                status = "Dark Mode Off"
            }

            statusTheme.text = status
            settingViewModel.saveThemeSetting(this.isDarkMode)
        }

        val dialog = builder.create()
        dialog.show()
    }
}