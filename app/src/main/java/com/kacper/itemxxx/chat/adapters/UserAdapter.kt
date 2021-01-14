package com.kacper.itemxxx.chat.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private val mContext: Context,
    private val mUsers: List<Users>,
    private var isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    var lastMsg: String = ""
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.user_search_item_layout, viewGroup, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Users = mUsers[position]
        holder.userNameTxt.text = user.getUserName()

 //Picasso.get().load(lastMsg).placeholder(R.drawable.profile)
         //   .into(holder.profileImageView)

        if (isChatCheck) {
            retrieveLastMessage(user.getUID(), holder.lastMessageTxt)
        } else {
            holder.lastMessageTxt.visibility = View.GONE

        }

        if (isChatCheck) {
            if (user.getStatus() == "online") {
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            } else {
                holder.onlineImageView.visibility = View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        } else {
            holder.onlineImageView.visibility = View.GONE
            holder.offlineImageView.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )

            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
                if (position == 1) {

                    val intent = Intent(mContext, VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
            })
            builder.show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(users: Users) {
            userNameTxt.text = users.getUserName()

        }
        var userNameTxt: TextView = itemView.findViewById(R.id.username)
        var profileImageView: CircleImageView = itemView.findViewById(R.id.profile_image_2)
        var onlineImageView: CircleImageView = itemView.findViewById(R.id.image_online)
        var offlineImageView: CircleImageView = itemView.findViewById(R.id.image_offline)
        var lastMessageTxt: TextView = itemView.findViewById(R.id.message_last)

    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView) {
        lastMsg = "defaultMsg"

        val firebaseUsers = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(pO: DataSnapshot) {

                for (dataSnapshot in pO.children) {
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)

                    if (firebaseUsers != null && chat != null) {
                        if (chat.receiver == firebaseUsers.uid && chat.sender == chatUserId ||
                            chat.receiver == chatUserId && chat.sender == firebaseUsers.uid) {
                            lastMsg = chat.message
                        }
                    }
                }
                when (lastMsg) {
                    "defaultMsg" -> lastMessageTxt.text = "no Message"
                    "sent you an image." -> lastMessageTxt.text = "image sent."

                    else -> lastMessageTxt.text = lastMsg
                }

                lastMsg = "defaultMsg"
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })

    }

}