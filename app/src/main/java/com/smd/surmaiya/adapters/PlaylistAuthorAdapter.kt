package com.smd.surmaiya.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smd.surmaiya.Fragments.OtherUserFragment
import com.smd.surmaiya.ManagerClasses.OtherUserManager
import com.smd.surmaiya.R
import com.smd.surmaiya.itemClasses.User
import de.hdodenhof.circleimageview.CircleImageView

class PlaylistAuthorAdapter(private val users: List<User>) :
    RecyclerView.Adapter<PlaylistAuthorAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorImage: CircleImageView = view.findViewById(R.id.playlistAuthorImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_author_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        if (user.profilePictureUrl.isEmpty()) {
            Glide.with(holder.itemView.context).load(R.mipmap.melisa).into(holder.authorImage)
        } else {
            Glide.with(holder.itemView.context).load(user.profilePictureUrl)
                .into(holder.authorImage)
        }

        holder.authorImage.setOnClickListener {

            handleAuthorImageClick(user, holder)
        }

    }

    private fun handleAuthorImageClick(user: User, holder: UserViewHolder) {
        OtherUserManager.addUser(user)
        val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current fragment with the new one
        fragmentTransaction.replace(R.id.fragment_container, OtherUserFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    override fun getItemCount() = users.size
}