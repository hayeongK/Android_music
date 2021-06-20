package com.example.practice3

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore

class AudioService : Service() {
    private val mBinder: IBinder = AudioServiceBinder()
    private val mAudioIds: ArrayList<Long> = ArrayList()
    private var mMediaPlayer: MediaPlayer? = null
    private var isPrepared = false
    private var mCurrentPosition = 0
    private var mAudioItem = Music("","","","",0) //빈 Music

    inner class AudioServiceBinder : Binder() {
        val service: AudioService
            get() = this@AudioService
    }

    override fun onCreate() {
        super.onCreate()
        mMediaPlayer = MediaPlayer()
        mMediaPlayer!!.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK)//ApplicationProvider.getApplicationContext()
        mMediaPlayer!!.setOnPreparedListener { mp ->
            isPrepared = true
            mp.start()
        }
        mMediaPlayer!!.setOnCompletionListener { isPrepared = false }
        mMediaPlayer!!.setOnErrorListener { mp, what, extra ->
            isPrepared = false
            false
        }
        mMediaPlayer!!.setOnSeekCompleteListener { }
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    private fun queryAudioItem(position: Int) {
        mCurrentPosition = position
        val audioId = mAudioIds[position]
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
        val selection = MediaStore.Audio.Media._ID + " = ?"
        val selectionArgs = arrayOf(audioId.toString())
        val cursor= contentResolver.query(uri, projection, selection, selectionArgs, null)
        //val cursor: Cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null)
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst()

                //mAudioItem = AudioAdapter.AudioItem.bindCursor(cursor)
                val id = cursor!!.getString(0)
                val title = cursor!!.getString(1)
                val artist = cursor!!.getString(2)
                val albumId = cursor!!.getString(3)
                val duration = cursor!!.getLong(4)

                val music = Music(id, title, artist, albumId, duration)
                mAudioItem = music
            }
            cursor.close()
        }
    }

    private fun prepare() {
        try {
            mMediaPlayer?.setDataSource(mAudioItem.getMusicUri().toString())
            //mMediaPlayer.setDataSource(mAudioItem.mDataPath)
            //mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer!!.setAudioAttributes(//윗줄 대체
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mMediaPlayer!!.prepareAsync()
            /*mMediaPlayer?.apply {
                setOnPreparedListener(AudioService)
                prepareAsync()
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stop() {
        mMediaPlayer!!.stop()
        mMediaPlayer!!.reset()
    }

    fun setPlayList(audioIds: ArrayList<Long>) {//ArrayList<Long?>? 바꿈
        if (mAudioIds.size != audioIds.size){
            if (!mAudioIds.equals(audioIds)) {
                mAudioIds.clear()
                mAudioIds.addAll(audioIds)
            }
        }
    }

    fun play(position: Int) {
        queryAudioItem(position)
        stop()
        prepare()
        //mMediaPlayer!!.start()
    }

    fun play() {
        if (isPrepared) {
            mMediaPlayer!!.start()
        }
    }

    fun pause() {
        if (isPrepared) {
            mMediaPlayer!!.pause()
        }
    }

    fun forward() {
        if (mAudioIds.size - 1 > mCurrentPosition) {
            mCurrentPosition++ // 다음 포지션으로 이동.
        } else {
            mCurrentPosition = 0 // 처음 포지션으로 이동.
        }
        play(mCurrentPosition)
    }

    fun rewind() {
        if (mCurrentPosition > 0) {
            mCurrentPosition-- // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mAudioIds.size - 1 // 마지막 포지션으로 이동.
        }
        play(mCurrentPosition)
    }

    fun getAudioItem(): Music {
        return mAudioItem
    }

    fun isPlaying(): Boolean{
        return mMediaPlayer!!.isPlaying()
    }
}
