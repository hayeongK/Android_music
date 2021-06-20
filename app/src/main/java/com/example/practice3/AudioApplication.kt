package com.example.practice3

import android.app.Application

class AudioApplication : Application() {
    var mInterface: AudioServiceInterface? = null
        private set
    init {
        mInstance = this
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        mInterface = AudioServiceInterface(applicationContext)
    }

    companion object {
        lateinit var mInstance:AudioApplication
    }

    fun getServiceInterface():AudioServiceInterface?{
        return mInterface
    }

}
