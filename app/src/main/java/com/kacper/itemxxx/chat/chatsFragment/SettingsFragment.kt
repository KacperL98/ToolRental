package com.kacper.itemxxx.chat.chatsFragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.kacper.itemxxx.R
import com.kacper.itemxxx.chat.model.Users
import com.kacper.itemxxx.databinding.FragmentSearchBinding
import com.kacper.itemxxx.databinding.FragmentSettingsBinding
import com.kacper.itemxxx.helpers.AuthenticationHelper.firebaseUser
import com.kacper.itemxxx.helpers.AuthenticationHelper.refUsers
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.*


class SettingsFragment : Fragment() {
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")



        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(pO: DatabaseError) {
            }

            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {
                    val user: Users? = pO.getValue(Users::class.java)

                    if (context != null) {
                        binding.usernameSettings.text = user!!.username
                        Picasso.get().load(user.profile).placeholder(R.drawable.profile)
                            .into(profile_image_settings1)
                        Picasso.get().load(user.cover).placeholder(R.drawable.cover)
                            .into(cover_image_settings1)
                    }
                }
            }
        })
        binding.profileImageSettings1.setOnClickListener {
            pickImage()
        }

        binding.coverImageSettings1.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        return binding.root
    }

    private fun pickImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "Uploading ...", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is uploading, please wait ...")
        progressBar.show()

        if (imageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover") {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        refUsers!!.updateChildren(mapCoverImg)
                        coverChecker = ""

                    } else {

                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        refUsers!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }

    companion object {
        private const val RequestCode = 438
    }
}