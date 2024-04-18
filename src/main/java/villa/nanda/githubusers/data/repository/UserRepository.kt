package villa.nanda.githubusers.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import villa.nanda.githubusers.data.local.User
import villa.nanda.githubusers.data.local.UserDao
import villa.nanda.githubusers.data.local.UserDatabase
import villa.nanda.githubusers.data.response.UserItem
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val userDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = UserDatabase.getDatabase(application)
        userDao = db.userDao()
    }

    fun getAllUsers(): LiveData<List<UserItem>> = userDao.getAllUsers()

    fun search(username: String): LiveData<User> = userDao.getFavoriteUserByUsername(username)

    fun insert(users: User) {
        executorService.execute { userDao.insert(users) }
    }

    fun delete(users: User) {
        executorService.execute { userDao.delete(users) }
    }
}