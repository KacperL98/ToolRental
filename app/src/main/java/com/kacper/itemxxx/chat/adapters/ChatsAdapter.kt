package com.kacper.itemxxx.chat.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kacper.itemxxx.chat.model.Chat
import com.kacper.itemxxx.chat.viewholders.ChatLeftViewHolder
import com.kacper.itemxxx.chat.viewholders.ChatRightViewHolder
import com.kacper.itemxxx.helpers.AuthenticationHelper.auth

private const val MSG_TYPE_LEFT = 0
private const val MSG_TYPE_RIGHT = 1

class ChatsAdapter(private val image_url: String) :
    ListAdapter<Chat, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            ChatRightViewHolder.create(parent)
        } else {
            ChatLeftViewHolder.create(parent)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ChatLeftViewHolder -> holder.bind(item, firebaseUser, image_url)
            is ChatRightViewHolder -> holder.bind(item, firebaseUser, image_url)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).sender == auth?.currentUser?.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}
private val DIFF_CALLBACK: DiffUtil.ItemCallback<Chat> = object : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean =
        oldItem == newItem
}