package com.example.zierulesguru.barcodeScanner

import android.app.Application
import com.example.zierulesguru.MyApplication
import com.example.zierulesguru.SiswaScanned


class ScannedData : MyApplication() {
  companion object {
    var siswaScannedList: ArrayList<SiswaScanned> = ArrayList()
  }
}

