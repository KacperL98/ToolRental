package com.kacper.itemxxx.scanner.dialogs

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.kacper.itemxxx.R
import com.kacper.itemxxx.db.database.QrResultDataBase
import com.kacper.itemxxx.db.entities.DbHelper
import com.kacper.itemxxx.db.entities.DatabaseDao
import com.kacper.itemxxx.db.entities.QrResult
import com.kacper.itemxxx.utils.toFormattedDisplay
import kotlinx.android.synthetic.main.layout_qr_result_show.*
import kotlinx.android.synthetic.main.layout_single_item_qr_result.favouriteIcon

class QrCodeResultDialog(var context: Context) {
    private lateinit var dialog: Dialog
    private lateinit var databaseDao: DatabaseDao
    private var qrResult: QrResult? = null
    private var onDismissListener: OnDismissListener? = null

    init {
        init()
        initDialog()
    }

    private fun init() {
        databaseDao = DbHelper(QrResultDataBase.getAppDatabase(context)!!)
    }

    private fun initDialog() {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.layout_qr_result_show)
        dialog.setCancelable(false)
        onClicks()
    }

    private fun onClicks() {

        dialog.favouriteIcon.setOnClickListener {
            if (it.isSelected) {
                removeFromFavourite()
            } else
                addToFavourite()
        }

        dialog.copyResult.setOnClickListener {
            copyResultToClipBoard()
        }

        dialog.shareResult.setOnClickListener {
            shareResult()
        }

        dialog.cancelDialog.setOnClickListener {
            dialog.dismiss()
            onDismissListener?.onDismiss()
        }

        dialog.button2.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().getReference("Data")
            val hashMap = HashMap<String, String>()
            hashMap["scannedText"] = dialog.scannedText.text.toString()
            hashMap["ID_User"] = dialog.identificationUser.text.toString()
            hashMap["Order_Number"] = dialog.numberUsers.text.toString()
            hashMap["Number"] = dialog.numberIte2.text.toString()

            reference.push().setValue(hashMap)
        }
    }

    private fun addToFavourite() {
        dialog.favouriteIcon.isSelected = true
        databaseDao.addToFavourite(qrResult?.id!!)
    }

    private fun removeFromFavourite() {
        dialog.favouriteIcon.isSelected = false
        databaseDao.removeFromFavourite(qrResult?.id!!)
    }

    fun show(recentQrResult: QrResult) {
        this.qrResult = recentQrResult
        dialog.scannedDate.text = qrResult?.calendar?.toFormattedDisplay()
        dialog.scannedText.text = qrResult!!.result
        dialog.favouriteIcon.isSelected = qrResult!!.favourite
        dialog.show()
    }

    fun setOnDismissListener(dismissListener: OnDismissListener) {
        this.onDismissListener = dismissListener
    }

    private fun copyResultToClipBoard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QrCodeScannedResult", dialog.scannedText.text)
        clipboard.text = clip.getItemAt(0).text.toString()
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun shareResult() {
        val txtIntent = Intent(Intent.ACTION_SEND)
        txtIntent.type = "text/plain"
        txtIntent.putExtra(
            Intent.EXTRA_TEXT,
            dialog.scannedText.text.toString()
        )
        context.startActivity(Intent.createChooser(txtIntent, "Share QR Result"))
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}




