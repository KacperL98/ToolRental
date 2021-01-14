package com.kacper.itemxxx.chat.chatsActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.kacper.itemxxx.R
import com.kacper.itemxxx.chat.chatsFragment.ChatFragment
import com.kacper.itemxxx.chat.chatsFragment.SearchFragment
import com.kacper.itemxxx.chat.chatsFragment.SettingsFragment
import com.kacper.itemxxx.chat.model.Chat
import com.kacper.itemxxx.chat.model.Users
import com.kacper.itemxxx.mainPanel.PanelActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? =null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        back_btn_panelActivity.setOnClickListener {
            val intent = Intent(this@ChatActivity, PanelActivity::class.java)
                startActivity(intent)
        }
        setSupportActionBar(toolbar_main)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val ref  = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessages = 0
                for (dataSnapshot in pO.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat?.receiver.equals(firebaseUser!!.uid) && chat?.isseen == true) {
                        countUnreadMessages += 1
                    }
                }
                if (countUnreadMessages == 0){
                    viewPagerAdapter.addFragment(ChatFragment(), "Chats")
                }else{
                    viewPagerAdapter.addFragment(ChatFragment(), "($countUnreadMessages) Chats")
                }
                viewPagerAdapter.addFragment(SearchFragment(), "Search")
                viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }
            override fun onCancelled(pO: DatabaseError) {
            }
        })
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()){
                    val user: Users? = pO.getValue(Users::class.java)
                    user_name.text = user!!.getUserName()
//                    Picasso.get().load(user.getProfile()).error(R.drawable.profile).placeholder(R.drawable.profile).into(profile_image)
                }
            }override fun onCancelled(pO: DatabaseError) {
            }
        })
    }
    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager){
        private val fragments: ArrayList<Fragment> = ArrayList<Fragment>()
        private val titles: ArrayList<String> = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }
        override fun getCount(): Int {
            return fragments.size
        }
        fun addFragment(fragment: Fragment, title: String){
            fragments.add(fragment)
            titles.add(title)
        }
        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
    private fun updateStatus(status: String){
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap)
    }
    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }
    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }
}