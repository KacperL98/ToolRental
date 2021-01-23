package com.kacper.itemxxx.chat.chatsFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kacper.itemxxx.R
import com.kacper.itemxxx.chat.adapters.UserAdapter
import com.kacper.itemxxx.chat.model.Users

class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
     lateinit var users: List<Users>
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.searchUsersEt)

        users = ArrayList()
        retrieveAllUsers()
        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().toLowerCase())

            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        return view
    }
    private fun retrieveAllUsers() {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                (users as ArrayList<Users>).clear()
                if (searchEditText!!.text.toString() == ""){

                    for (snapshot in pO.children ){

                        val user: Users? = snapshot.getValue(Users::class.java)
                        if (!(user!!.uid).equals(firebaseUserID)){
                            (users as ArrayList<Users>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, users as ArrayList<Users>, false)
                    recyclerView!!.adapter = userAdapter
                }
            }
            override fun onCancelled(pO: DatabaseError) {
            }
        })
    }
    private fun searchForUsers (str: String){
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                (users as ArrayList<Users>).clear()

                for (snapshot in pO.children ){
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (!(user!!.uid).equals(firebaseUserID)){
                        (users as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, users as ArrayList<Users>, false)
                recyclerView!!.adapter = userAdapter
            }
            override fun onCancelled(pO: DatabaseError) {
            }
        })
    }
}