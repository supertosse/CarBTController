package com.supertosse.carbtcontroller

import android.app.Application
import java.io.InputStream
import java.io.OutputStream

class MyApplication : Application() {
    var outputStream: OutputStream? = null
    var inputStream: InputStream? = null
}