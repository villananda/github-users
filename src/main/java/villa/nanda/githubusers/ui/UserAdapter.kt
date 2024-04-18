package villa.nanda.githubusers.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import villa.nanda.githubusers.R
import villa.nanda.githubusers.data.response.UserItem
import villa.nanda.githubusers.databinding.ItemUserBinding
import villa.nanda.githubusers.ui.UserAdapter.ItemHolder

class UserAdapter(
    private val listUsers: ArrayList<UserItem>,
    private val context: Context,
    private val isDelete: Boolean = false,
    private val onItemLongClick: ((UserItem) -> Unit)?
) : RecyclerView.Adapter<ItemHolder>() {

    class ItemHolder(var binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemUserBinding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return ItemHolder(itemUserBinding)
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val user = listUsers[position]
        holder.binding.tvItemUsername.text = user.login
        Glide.with(holder.itemView.context).load(user.avatar_url)
            .placeholder(R.drawable.no_profil_pic)
            .into(holder.binding.imgItemUsername)

        holder.binding.itemLayout.setOnClickListener {
            val detail = Intent(context, DetailActivity::class.java)
            detail.putExtra(DetailActivity.USERNAME, user.login)

            context.startActivity(detail)
        }

        if (isDelete) {
            holder.binding.itemLayout.setOnLongClickListener {
                onItemLongClick?.invoke(user)
                return@setOnLongClickListener true
            }
        }
    }
}