package villa.nanda.githubusers.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import villa.nanda.githubusers.BuildConfig
import villa.nanda.githubusers.data.response.UserItem
import villa.nanda.githubusers.data.retrofit.ApiConfig
import villa.nanda.githubusers.databinding.FragmentFollowingBinding

class FollowingFragment : Fragment() {

    private var _binding: FragmentFollowingBinding? = null

    private var list = ArrayList<UserItem>()
    private val binding get() = _binding!!

    companion object {
        const val CONST_POSITION = "position"
        const val CONST_USERNAME = "username"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var position = 1
        var username = ""
        arguments?.let {
            position = it.getInt(CONST_POSITION)
            username = it.getString(CONST_USERNAME).toString()
        }

        if (position == 0) {
            listUsers(username, true)
        } else {
            listUsers(username, false)
        }

    }

    private fun listUsers(username: String, isFollowing: Boolean) {
        showLoading(true)
        val client = if (isFollowing) {
            ApiConfig.getApiService()
                .getFollowerUsers(BuildConfig.GITHUB_KEY, username)
        } else {
            ApiConfig.getApiService()
                .getFollowingUsers(BuildConfig.GITHUB_KEY, username)
        }

        client.enqueue(object : Callback<List<UserItem>> {
            override fun onResponse(
                call: Call<List<UserItem>>,
                response: Response<List<UserItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let {
                        list = it as ArrayList<UserItem>
                    }
                } else {
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
                if (list.isEmpty()) {
                    binding.emptyListFragment.visibility = View.VISIBLE
                }
                showRecyclerList()
            }

            override fun onFailure(call: Call<List<UserItem>>, t: Throwable) {
                showLoading(false)
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
    }

    private fun showRecyclerList() {
        binding.listUsers.layoutManager = LinearLayoutManager(context)
        val adapter = context?.let { FollowerAdapter(list) }
        binding.listUsers.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressFollowing.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}