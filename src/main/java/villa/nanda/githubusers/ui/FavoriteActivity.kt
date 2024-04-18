package villa.nanda.githubusers.ui

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import villa.nanda.githubusers.R
import villa.nanda.githubusers.data.local.User
import villa.nanda.githubusers.data.response.UserItem
import villa.nanda.githubusers.databinding.ActivityFavoriteBinding
import villa.nanda.githubusers.helper.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private var list = ArrayList<UserItem>()
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarFavorite)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.listFavorite.setHasFixedSize(true)

        binding.emptyListFavorite.visibility = View.GONE

        val factory = ViewModelFactory.getInstance(this@FavoriteActivity.application, null)
        userViewModel = ViewModelProvider(this@FavoriteActivity, factory)[UserViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressFavorite.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun showList() {
        showLoading(true)
        userViewModel.getUsers().observe(this) {
            list.clear()
            list.addAll(it)
            binding.listFavorite.layoutManager = LinearLayoutManager(this)

            val adapter = UserAdapter(list, this@FavoriteActivity, true,
                onItemLongClick = { selectedItem: UserItem ->
                    showDeleteDialog(selectedItem)
                })

            binding.listFavorite.adapter = adapter
            if (list.isEmpty()) {
                binding.emptyListFavorite.visibility = View.VISIBLE
            }

            showLoading(false)
        }
    }

    private fun isDefaultNightModeEnabled(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun showDeleteDialog(userItem: UserItem) {
        val color = if(isDefaultNightModeEnabled()) R.color.gray_smoke else R.color.black

        val dialog = AlertDialog.Builder(this)
            .setMessage("Apakah anda akan menghapus user ini?")
            .setPositiveButton("Hapus") { dialog, _ ->
                // Hapus item dari data set dan RecyclerView
                val user = User()
                user.login = userItem.login!!
                user.avatarUrl = userItem.avatar_url

                userViewModel.delete(user)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(baseContext.getColor(color))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(baseContext.getColor(color))
    }

    override fun onResume() {
        showList()
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.addCallback(this@FavoriteActivity) {
            finish()
        }

        return super.onSupportNavigateUp()
    }
}