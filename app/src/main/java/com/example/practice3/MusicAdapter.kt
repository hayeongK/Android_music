package com.example.practice3

import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class MusicAdapter : RecyclerView.Adapter<MusicAdapter.Holder>() {
    val musicList = mutableListOf<Music>()
    var mediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val music = musicList[position]
        holder.setMusic(music, position)
    }

    fun getMusicIds(): ArrayList<Long> {
        val count = getItemCount()
        val audioIds: ArrayList<Long> = ArrayList()
        for (i in 0 until count) {
            audioIds.add(getItemId(i))
        }
        return audioIds
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var musicUri: Uri? = null
        var mPosition: Int = 0

        init {
            itemView.setOnClickListener{
                //원래 누르면 재생되도록
                if(mediaPlayer != null) {
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                mediaPlayer = MediaPlayer.create(itemView.context, musicUri)
                mediaPlayer?.start()

                //수정해야하는
                //AudioApplication.mInstance.getServiceInterface()?.setPlayList(getMusicIds())// 재생목록등록
                //AudioApplication.mInstance.getServiceInterface()?.play(mPosition)// 선택한 오디오재생
                //AudioServiceInterface(AudioApplication.mInstance.applicationContext).setPlayList(getMusicIds())
                //AudioServiceInterface(AudioApplication.mInstance.applicationContext).play(mPosition)
            }
        }

        fun setMusic(music:Music, position: Int){ //아이템에 글자 입력되도록
            musicUri = music.getMusicUri()
            mPosition = position

            itemView.findViewById<ImageView>(R.id.imageAlbum).setImageURI(music.getAlbumUri())
            itemView.findViewById<TextView>(R.id.textArtist).text = music.artist
            itemView.findViewById<TextView>(R.id.textTitle).text = music.title
            val sdf = SimpleDateFormat("mm:ss")
            itemView.findViewById<TextView>(R.id.textDuration).text = sdf.format(music.duration)
        }
    }
}
