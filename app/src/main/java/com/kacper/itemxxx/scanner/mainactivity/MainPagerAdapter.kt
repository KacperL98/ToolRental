package com.kacper.itemxxx.scanner.mainactivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kacper.itemxxx.scanner.qrscanner.QRScannerFragment
import com.kacper.itemxxx.scanner.scanner_history.ScannedHistoryFragment

class MainPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {


    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                QRScannerFragment.newInstance()
            }
            1 -> {
                ScannedHistoryFragment.newInstance(ScannedHistoryFragment.ResultListType.ALL_RESULT)
            }

            2 -> {
                ScannedHistoryFragment.newInstance(ScannedHistoryFragment.ResultListType.FAVOURITE_RESULT)
            }

            else -> {
                QRScannerFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }
}