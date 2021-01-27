package com.kacper.itemxxx.chat.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kacper.itemxxx.R
import com.kacper.itemxxx.chat.chatsActivity.MessageChatActivity
import com.kacper.itemxxx.chat.chatsActivity.VisitUserProfileActivity
import com.kacper.itemxxx.chat.model.Chat
import com.kacper.itemxxx.chat.model.Users
import kotlinx.android.synthetic.main.user_search_item_layout.view.*

class UserAdapter(
    var context: Context,
    private val adapterUserViewHolder: List<Users>,
    private var isChatCheck: Boolean = false,
    private var imageUrl: String = ""
) : RecyclerView.Adapter<UserAdapter.AdapterResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterResultViewHolder {
        return AdapterResultViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_search_item_layout, parent, false)
        )
    }
    override fun getItemCount(): Int {
        return adapterUserViewHolder.size
    }
    override fun onBindViewHolder(holder: AdapterResultViewHolder, position: Int) {
        holder.bind(adapterUserViewHolder[position])
        val users: Users = adapterUserViewHolder[position]

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("What do you want?")
            builder.setItems(options) { _, position ->

                if (position == 0) {
                    val intent = Intent(context, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", users.uid)
                    context.startActivity(intent)
                }
                if (position == 1) {
                    val intent = Intent(context, VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id", users.uid)
                    context.startActivity(intent)
                }
            }
            builder.show()
        }
    }
    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView) {
        imageUrl = "defaultMsg"

        val firebaseUsers = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(pO: DataSnapshot) {

                for (dataSnapshot in pO.children) {
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)

                    if (firebaseUsers != null && chat != null) {
                        if (chat.receiver == firebaseUsers.uid && chat.sender == chatUserId ||
                            chat.receiver == chatUserId && chat.sender == firebaseUsers.uid
                        ) {
                            imageUrl = chat.message
                        }
                    }
                }
                when (imageUrl) {
                    "defaultMsg" -> lastMessageTxt.text = "no Message"
                    "sent you an image." -> lastMessageTxt.text = "image sent."

                    else -> lastMessageTxt.text = imageUrl
                }
            }
            override fun onCancelled(pO: DatabaseError) {
            }
        })
    }
    inner class AdapterResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(users: Users) {
            with(itemView) {
                usernameTxt.text = users.username
                message_last.text = users.userNameTxt

                Glide.with(this).load(imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.profile).into(profile_image_2)

                if (isChatCheck) {
                    retrieveLastMessage(users.uid, message_last)

                } else {
                    message_last.visibility = View.GONE
                }
                if (isChatCheck) {
                    if (users.status == "online") {
                        image_online.visibility = View.VISIBLE
                        image_offline.visibility = View.GONE
                    } else {
                        image_online.visibility = View.GONE
                        image_offline.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}


