package com.kacper.itemxxx.chat.viewholders

import android.content.Intent
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.kacper.itemxxx.R
import com.kacper.itemxxx.chat.chatsActivity.ViewFullImageActivity
import com.kacper.itemxxx.chat.model.Chat
import com.kacper.itemxxx.databinding.MessageItemRightBinding
import com.squareup.picasso.Picasso

class ChatRightViewHolder(private val binding: MessageItemRightBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(chat: Chat, firebaseUser: FirebaseUser, imgUrl: String) {
        with(binding) {
            Picasso.get().load(imgUrl).into(profileImage)
            showTextMessage.text = chat.message
            showMessageInfo(chat)
            if (firebaseUser.uid == chat.sender) {
                showDialogMessage(chat)
            }

            if (chat.message == "sent you an image." && chat.url != "") {

                if (chat.sender == firebaseUser.uid) {
                    showTextMessage.visibility = GONE
                    rightImageView.visibility = VISIBLE
                    Picasso.get().load(chat.url).into(rightImageView)

                    rightImageView.setOnClickListener {
                        val options = arrayOf<CharSequence>(
                            "View Full Image",
                            "Delete Image",
                            "Cancel"
                        )
                        val builder: AlertDialog.Builder = AlertDialog.Builder(binding.root.context)
                        builder.setTitle("what do you want?")
                        builder.setItems(options) { _, which ->
                            if (which == 0) {
                                val intent =
                                    Intent(binding.root.context, ViewFullImageActivity::class.java)
                                intent.putExtra("url", chat.url)
                                binding.root.context.startActivity(intent)

                            } else if (which == 1) {
                                deleteSentMessage(chat)
                            }
                        }
                        builder.show()
                    }
                }

            }
        }
    }

    private fun showMessageInfo(chat: Chat) {
        with(binding) {
            if (chat.isseen) {
                textSeen.text = "Seen"
                if (chat.message == "sent you an image." && chat.url != "") {
                    val lp: RelativeLayout.LayoutParams? =
                        textSeen.layoutParams as RelativeLayout.LayoutParams?
                    lp?.setMargins(0, 245, 10, 0)
                    textSeen.layoutParams = lp
                }
            } else {
                textSeen.text = "Sent"
                if (chat.message == "sent you an image." && chat.url != "") {
                    val lp: RelativeLayout.LayoutParams? =
                        textSeen.layoutParams as RelativeLayout.LayoutParams?
                    lp?.setMargins(0, 245, 10, 0)
                    textSeen.layoutParams = lp
                }
            }
        }
    }


    private fun showDialogMessage(chat: Chat) {
        binding.showTextMessage.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Delete Message",
                "Cancel"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(binding.root.context)
            builder.setTitle("what do you want?")
            builder.setItems(options) { _, _ ->
                deleteSentMessage(chat)
            }
            builder.show()
        }
    }

    private fun deleteSentMessage(chat: Chat) {
        FirebaseDatabase.getInstance().reference.child("Chats")
            .child(chat.messageId)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context, "Deleted.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(binding.root.context, "Failed, Not Deleted.", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }


    companion object {
        fun create(parent: ViewGroup): ChatRightViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.message_item_right, parent, false)
            val binding = MessageItemRightBinding.bind(view)
            return ChatRightViewHolder(binding)
        }
    }
}