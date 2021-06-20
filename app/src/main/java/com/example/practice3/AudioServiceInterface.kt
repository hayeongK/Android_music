package com.example.practice3

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class AudioServiceInterface(context: Context) {
    private var mService: AudioService? = null
    private var mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = (service as AudioService.AudioServiceBinder).service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            //mServiceConnection = null
            mService = null
        }
    }

    fun setPlayList(audioIds: ArrayList<Long>) {
        if (mService != null) {
            mService!!.setPlayList(audioIds)
        }
    }

    fun play(position: Int) {
        if (mService != null) {
            mService!!.play(position)
        }
    }

    fun play() {
        if (mService != null) {
            mService!!.play()
        }
    }

    fun pause() {
        if (mService != null) {
            mService!!.play()
        }
    }

    fun forward() {
        if (mService != null) {
            mService!!.forward()
        }
    }

    fun rewind() {
        if (mService != null) {
            mService!!.rewind()
        }
    }

    fun togglePlay(){
        if(isPlaying()){
            mService!!.pause()
        } else{
            mService!!.play()
        }
    }

    fun isPlaying(): Boolean {
        if (mService != null){
            return mService!!.isPlaying()
        }
        return false
    }

    init {
        context.bindService(
            Intent(context, AudioService::class.java)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE)
    }
}