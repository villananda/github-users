package villa.nanda.githubusers.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import villa.nanda.githubusers.data.local.User
import villa.nanda.githubusers.data.repository.UserRepository
import villa.nanda.githubusers.data.response.UserItem

class UserViewModel(application: Application) : ViewModel() {
    private val userRepository: UserRepository = UserRepository(application)

    fun getUsers(): LiveData<List<UserItem>> = userRepository.getAllUsers()

    fun searchUsername(username: String): LiveData<User> = userRepository.search(username)

    fun insert(users: User) {
        userRepository.insert(users)
    }

    fun delete(users: User) {
        userRepository.delete(users)
    }
}