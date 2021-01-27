package com.kacper.itemxxx.scanner.qrscanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.kacper.itemxxx.R
import com.kacper.itemxxx.db.database.QrResultDataBase
import com.kacper.itemxxx.db.entities.DbHelper
import com.kacper.itemxxx.db.entities.DatabaseDao
import com.kacper.itemxxx.scanner.dialogs.QrCodeResultDialog
import kotlinx.android.synthetic.main.fragment_qrscanner.view.*
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class QRScannerFragment : Fragment(), ZBarScannerView.ResultHandler {
    private lateinit var qrView: View
    lateinit var scannerView: ZBarScannerView
    lateinit var resultDialog: QrCodeResultDialog
    private lateinit var databaseDao: DatabaseDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        qrView = inflater.inflate(R.layout.fragment_qrscanner, container, false)
        init()
        initViews()
        onClicks()
        return qrView.rootView
    }

    private fun init() {
        databaseDao = DbHelper(QrResultDataBase.getAppDatabase(requireContext())!!)
    }

    private fun initViews() {
        initializeQRCamera()
        setResultDialog()
    }

    private fun initializeQRCamera() {
        scannerView = ZBarScannerView(context)
        scannerView.setResultHandler(this)
        scannerView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorTranslucent))
        scannerView.setBorderColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
        scannerView.setLaserColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
        scannerView.setBorderStrokeWidth(10)
        scannerView.setSquareViewFinder(true)
        scannerView.setupScanner()
        scannerView.setAutoFocus(true)
        startQRCamera()
        qrView.containerScanner.addView(scannerView)
    }

    private fun setResultDialog() {
        resultDialog = QrCodeResultDialog(requireContext())
        resultDialog.setOnDismissListener(object : QrCodeResultDialog.OnDismissListener {
            override fun onDismiss() {
                resetPreview()
            }
        })
    }
    override fun handleResult(rawResult: Result?) {
        onQrResult(rawResult?.contents)
    }

    private fun onQrResult(contents: String?) {
        if (contents.isNullOrEmpty())
            showToast("Empty Qr Result")
        else
            saveToDataBase(contents)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun saveToDataBase(contents: String) {
        val insertedResultId = databaseDao.insertQRResult(contents)
        val qrResult = databaseDao.getQRResult(insertedResultId)
        resultDialog.show(qrResult)
    }

    private fun startQRCamera() {
        scannerView.startCamera()
    }

    private fun resetPreview() {
        scannerView.stopCamera()
        scannerView.startCamera()
        scannerView.stopCameraPreview()
        scannerView.resumeCameraPreview(this)
    }

    private fun onClicks() {
        qrView.flashToggle.setOnClickListener {
            if (qrView.flashToggle.isSelected) {
                offFlashLight()
            } else {
                onFlashLight()
            }
        }
    }

    private fun onFlashLight() {
        qrView.flashToggle.isSelected = true
        scannerView.flash = true
    }

    private fun offFlashLight() {
        qrView.flashToggle.isSelected = false
        scannerView.flash = false
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView.stopCamera()
    }

    companion object {

        fun newInstance(): QRScannerFragment {
            return QRScannerFragment()
        }
    }

}
