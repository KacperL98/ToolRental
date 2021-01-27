package com.kacper.itemxxx.scanner.scanner_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kacper.itemxxx.R
import com.kacper.itemxxx.db.database.QrResultDataBase
import com.kacper.itemxxx.db.entities.DbHelper
import com.kacper.itemxxx.db.entities.DatabaseDao
import com.kacper.itemxxx.db.entities.QrResult
import com.kacper.itemxxx.helpers.gone
import com.kacper.itemxxx.helpers.visible
import com.kacper.itemxxx.scanner.adapter.ScannedResultListAdapter
import kotlinx.android.synthetic.main.fragment_scanned_history.view.*
import kotlinx.android.synthetic.main.layout_header_history.view.*
import java.io.Serializable

class ScannedHistoryFragment : Fragment() {
    enum class ResultListType : Serializable {
        ALL_RESULT, FAVOURITE_RESULT
    }
    private lateinit var qrView: View
    private lateinit var databaseDao: DatabaseDao
    private var resultListType: ResultListType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleArguments()
    }

    private fun handleArguments() {
        resultListType = arguments?.getSerializable(ARGUMENT_RESULT_LIST_TYPE) as ResultListType
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        qrView = inflater.inflate(R.layout.fragment_scanned_history, container, false)
        init()
        setSwipeRefresh()
        onClicks()
        showListOfResults()
        return qrView.rootView
    }

    private fun init() {
        databaseDao = DbHelper(QrResultDataBase.getAppDatabase(requireContext())!!)
        qrView.layoutHeader.tvHeaderText.text = getString(R.string.recent_scanned_results)
    }

    private fun showListOfResults() {
        when (resultListType) {
            ResultListType.ALL_RESULT -> showAllResults()
            ResultListType.FAVOURITE_RESULT -> showFavouriteResults()
        }
    }


    private fun showAllResults() {
        val listOfAllResult = databaseDao.getAllQRScannedResult()
        showResults(listOfAllResult)
        qrView.layoutHeader.tvHeaderText.text = getString(R.string.recent_scanned)
    }

    private fun showFavouriteResults() {
        val listOfFavouriteResult = databaseDao.getAllFavouriteQRScannedResult()
        showResults(listOfFavouriteResult)
        qrView.layoutHeader.tvHeaderText.text = getString(R.string.favourites_scanned_results)
    }


    private fun showResults(listOfQrResult: List<QrResult>) {
        if (listOfQrResult.isNotEmpty())
            initRecyclerView(listOfQrResult)
        else
            showEmptyState()
    }

    private fun initRecyclerView(listOfQrResult: List<QrResult>) {
        qrView.scannedHistoryRecyclerView.layoutManager = LinearLayoutManager(context)
        qrView.scannedHistoryRecyclerView.adapter =
            ScannedResultListAdapter(databaseDao, requireContext(), listOfQrResult.toMutableList())
        showRecyclerView()
    }

    private fun setSwipeRefresh() {
        qrView.swipeRefresh.setOnRefreshListener {
            qrView.swipeRefresh.isRefreshing = false
            showListOfResults()
        }
    }

    private fun onClicks() {
        qrView.layoutHeader.removeAll.setOnClickListener {
            showRemoveAllScannedResultDialog()
        }
    }

    private fun showRemoveAllScannedResultDialog() {
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog).setTitle(getString(R.string.clear_all))
            .setMessage(getString(R.string.clear_all_result))
            .setPositiveButton(getString(R.string.clear)) { _, _ ->
                clearAllRecords()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }.show()

    }
    private fun clearAllRecords() {
        when (resultListType) {
            ResultListType.ALL_RESULT -> databaseDao.deleteAllQRScannedResult()
            ResultListType.FAVOURITE_RESULT -> databaseDao.deleteAllFavouriteQRScannedResult()
        }
        qrView.scannedHistoryRecyclerView.adapter?.notifyDataSetChanged()
        showListOfResults()
    }

    private fun showRecyclerView() {
        qrView.layoutHeader.removeAll.visible()
        qrView.scannedHistoryRecyclerView.visible()
        qrView.noResultFound.gone()
    }

    private fun showEmptyState() {
        qrView.layoutHeader.removeAll.gone()
        qrView.scannedHistoryRecyclerView.gone()
        qrView.noResultFound.visible()

    }
    companion object {
        private const val ARGUMENT_RESULT_LIST_TYPE = "ArgumentResultType"

        fun newInstance(screenType: ResultListType): ScannedHistoryFragment {
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_RESULT_LIST_TYPE, screenType)
            val fragment = ScannedHistoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
