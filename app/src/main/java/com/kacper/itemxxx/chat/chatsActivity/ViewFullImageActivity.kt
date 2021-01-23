package com.kacper.itemxxx.chat.chatsActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.kacper.itemxxx.R
import com.kacper.itemxxx.databinding.ActivityViewFullImageBinding
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {
    private var imageUrl: String =""
    private lateinit var binding: ActivityViewFullImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageUrl = intent.getStringExtra("url")!!
        Picasso.get().load(imageUrl).into(binding.imageViewer)

    }
}
