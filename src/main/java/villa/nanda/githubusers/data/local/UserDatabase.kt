package villa.nanda.githubusers.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase(){
    abstract fun userDao(): UserDao
    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): UserDatabase {
            if (INSTANCE == null) {
                synchronized(UserDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        UserDatabase::class.java, "users_db")
                        .build()
                }
            }
            return INSTANCE as UserDatabase
        }
    }
}