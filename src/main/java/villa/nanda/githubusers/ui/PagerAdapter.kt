package villa.nanda.githubusers.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var username: String = ""
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment= FollowingFragment()
        fragment.arguments = Bundle().apply {
            putInt(FollowingFragment.CONST_POSITION, position)
            putString(FollowingFragment.CONST_USERNAME, username)
        }

        return fragment
    }
}