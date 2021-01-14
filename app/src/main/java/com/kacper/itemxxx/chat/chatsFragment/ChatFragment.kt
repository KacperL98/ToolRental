package com.kacper.itemxxx.chat.chatsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.kacper.itemxxx.R
import com.kacper.itemxxx.chat.adapters.UserAdapter
import com.kacper.itemxxx.chat.model.ChatList
import com.kacper.itemxxx.chat.model.Users
import com.kacper.itemxxx.chat.notifications.Token

class ChatFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recyclerViewChatList: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerViewChatList = view.findViewById(R.id.recycler_view_chatlist)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()
        val ref  = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for (dataSnaphot in pO.children){
                    val chatlist = dataSnaphot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }
            override fun onCancelled(pO: DatabaseError) {
            }
        })
        updateToken(FirebaseInstanceId.getInstance().token)
        return view
    }
    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)

    }

    private fun retrieveChatList(){
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {

                (mUsers as ArrayList).clear()
                for (dataSnapshot in pO.children){
                    val user = dataSnapshot.getValue(Users::class.java)
                    for (eachChatList in usersChatList!!){
                        if (user!!.getUID().equals(eachChatList.getId())){
                            (mUsers as ArrayList).add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                recyclerViewChatList.adapter = userAdapter
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })
    }

}