package com.example.practice3

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
    val REQ_READ = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isPermitted()){
            startProcess()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQ_READ)
        }

        var mImgAlbumArt = findViewById<ImageView>(R.id.img_albumart)
        var mTxtTitle = findViewById<TextView>(R.id.txt_title)
        var mBtnPlayPause = findViewById<ImageButton>(R.id.btn_play_pause)
        findViewById<LinearLayout>(R.id.lin_miniplayer).setOnClickListener {

        }
        findViewById<ImageButton>(R.id.btn_rewind).setOnClickListener {
            AudioApplication.mInstance.getServiceInterface()?.rewind()
        }
        mBtnPlayPause.setOnClickListener {
            AudioApplication.mInstance.getServiceInterface()?.togglePlay()
        }
        findViewById<ImageButton>(R.id.btn_forward).setOnClickListener {
            AudioApplication.mInstance.getServiceInterface()?.forward()
        }

    }

    fun startProcess(){
        val adapter = MusicAdapter()
        adapter.musicList.addAll(getMusicList())

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun getMusicList(): List<Music> {
        //컨텐트 리졸버로 음원 목록 가져오기
        //1.데이터 테이블 주소
        val musicListUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        //2. 가져올 데이터 컬럼 정의
        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )
        //3.컨텐트 리졸버에 해당 데이터 요청
        val cursor = contentResolver.query(musicListUri, proj, null, null, null)
        //4커서로 전달받은 데이터를 꺼내서 저장
        val musicList = mutableListOf<Music>()
        while(cursor?.moveToNext() ?: false){
            val id = cursor!!.getString(0)
            val title = cursor!!.getString(1)
            val artist = cursor!!.getString(2)
            val albumId = cursor!!.getString(3)
            val duration = cursor!!.getLong(4)

            val music = Music(id, title, artist, albumId, duration)
            musicList.add(music)
        }
        return musicList
    }

    fun isPermitted(): Boolean{
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQ_READ){
            startProcess()
        }else{
            Toast.makeText(this, "권한 요청을 승인해야지만 앱을 실행할 수 있습니다.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}