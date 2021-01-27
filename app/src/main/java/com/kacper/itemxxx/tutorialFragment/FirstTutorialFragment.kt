package com.kacper.itemxxx.tutorialFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.kacper.itemxxx.R
import com.kacper.itemxxx.databinding.FragmentChatBinding
import com.kacper.itemxxx.databinding.FragmentFirstBinding
import com.kacper.itemxxx.helpers.toastFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_first.view.*

class FirstTutorialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_first, container, false)
        view.imageViewLottie.setOnClickListener {
            viewPager?.currentItem = 1
        }
        toastFragment("")
        return view
    }

}