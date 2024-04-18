package villa.nanda.githubusers.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import villa.nanda.githubusers.data.response.UserItem

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: User)

    @Delete
    fun delete(item: User)

    @Query("SELECT * from User")
    fun getAllUsers(): LiveData<List<UserItem>>

    @Query("SELECT * FROM User WHERE login = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<User>
}