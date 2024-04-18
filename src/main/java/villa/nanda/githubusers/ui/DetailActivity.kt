package villa.nanda.githubusers.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Response
import villa.nanda.githubusers.BuildConfig
import villa.nanda.githubusers.R
import villa.nanda.githubusers.data.local.User
import villa.nanda.githubusers.data.response.DetailResponse
import villa.nanda.githubusers.data.retrofit.ApiConfig
import villa.nanda.githubusers.databinding.ActivityDetailBinding
import villa.nanda.githubusers.helper.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding
    private lateinit var userViewModel: UserViewModel

    var urlAvatar: String? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        userViewModel = obtainViewModel(this@DetailActivity)

        val username = intent.getStringExtra(USERNAME).toString()
        val pagerAdapter = PagerAdapter(this)
        val viewPager: ViewPager2 = detailBinding.viewPager

        pagerAdapter.username = username
        viewPager.adapter = pagerAdapter
        val tabs: TabLayout = detailBinding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showLoading(true)
        getDetailUser(username)

        userViewModel.searchUsername(username).observe(this) {
            if (it != null) {
                isFavorite = true
                detailBinding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                detailBinding.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
            }
        }

        detailBinding.btnFavorite.setOnClickListener {
            val user = User()
            user.login = username
            user.avatarUrl = urlAvatar

            isFavorite = if (isFavorite) {
                userViewModel.delete(user)
                detailBinding.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                false
            } else {
                userViewModel.insert(user)
                detailBinding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24)
                true
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, null)
        return ViewModelProvider(activity, factory)[UserViewModel::class.java]
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.addCallback(this@DetailActivity) {
            finish()
        }

        return super.onSupportNavigateUp()
    }

    private fun getDetailUser(username: String) {
        val client = ApiConfig.getApiService()
            .getDetailUser(BuildConfig.GITHUB_KEY, username)
        client.enqueue(object : retrofit2.Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        detailBinding.username.text =
                            getString(R.string.text_username, responseBody.login)
                        detailBinding.nama.text = responseBody.name
                        detailBinding.bio.text = responseBody.bio
                        detailBinding.totalFollowing.text =
                            getString(R.string.text_following, responseBody.following.toString())
                        detailBinding.totalFollower.text =
                            getString(R.string.text_follower, responseBody.followers.toString())

                        Glide.with(this@DetailActivity).load(responseBody.avatarUrl)
                            .placeholder(R.drawable.no_profil_pic)
                            .into(detailBinding.profilImage)

                        urlAvatar = responseBody.avatarUrl
                        detailBinding.btnFavorite.visibility = View.VISIBLE
                    }
                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                showLoading(false)
                Log.e(ContentValues.TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            detailBinding.detailLayout.visibility = View.INVISIBLE
            detailBinding.viewPager.visibility = View.INVISIBLE
            detailBinding.progressDetail.visibility = View.VISIBLE
        } else {
            detailBinding.detailLayout.visibility = View.VISIBLE
            detailBinding.viewPager.visibility = View.VISIBLE
            detailBinding.progressDetail.visibility = View.GONE
        }
    }

    companion object {
        const val USERNAME = "username"

        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.tab_text_following, R.string.tab_text_follower)
    }
}