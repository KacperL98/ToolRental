package com.kacper.itemxxx.scanner.mainactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.kacper.itemxxx.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViewPager()
        setBottomViewListener()
        setViewPagerListener()
    }
    private fun setViewPager() {
        viewPager.adapter =
            MainPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 2
    }

    private fun setBottomViewListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.scanMenuid -> {
                    viewPager.currentItem = 0
                }
                R.id.recentScannedMenuId -> {
                    viewPager.currentItem = 1

                }
                R.id.favouritesMenuId -> {
                    viewPager.currentItem = 2
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun setViewPagerListener() {
        viewPager.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        bottomNavigationView.selectedItemId = R.id.scanMenuid
                    }
                    1 -> {
                        bottomNavigationView.selectedItemId = R.id.recentScannedMenuId
                    }
                    2 -> {
                        bottomNavigationView.selectedItemId = R.id.favouritesMenuId
                    }
                }
            }
        })
    }
}