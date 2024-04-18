package villa.nanda.githubusers.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "login")
    var login: String = "",

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String? = null
) : Parcelable