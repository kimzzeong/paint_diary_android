package com.example.paint_diary.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.paint_diary.Adapter.ViewPagerAdapter
import com.example.paint_diary.R
import kotlinx.android.synthetic.main.activity_view_pager.*
import kotlinx.android.synthetic.main.chat_image_viewpager_item.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ViewPagerActivity : AppCompatActivity() {
    var permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    var dir = File(Environment.getExternalStorageDirectory().toString() + "/Pictures", "paint")
    var image : String? = null
    var bitmap : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        var imageList = arrayListOf<String>()
        var viewPagerAdapter = ViewPagerAdapter(imageList, this)

        var intent = intent
        image = intent.getStringExtra("url")

        if(intent != null){
            Log.e("url", image!!)
            imageList.add(intent.getStringExtra("url")!!)

        }

        chat_viewpager.adapter = viewPagerAdapter
        chat_viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        viewPagerAdapter.setItemClickListener(object : ViewPagerAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {
                if (ContextCompat.checkSelfPermission(
                        this@ViewPagerActivity,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    download()

                } else {
                    askPermission()
                }


            }

        })

    }

    //이미지 다운로드 메소드
    private fun download() {
        if(!dir.exists()){ // 이 경로의 디렉토리가 없을 땐 디렉토리 생성
            dir.mkdir()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "PaintDiary_JPEG_" + timeStamp + "_"
        val imageFile = File.createTempFile(imageFileName, ".jpg", dir)
        var fileOutputStream = FileOutputStream(imageFile)
//        Log.e("download","1")

        Log.e("image",image!!)
        Glide.with(this)
            .asBitmap()
            .load(image)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if(resource != null){
                        Log.e("resource","not null")
                    }else{

                        Log.e("resource","null")
                    }
                    bitmap = resource

                    bitmap?.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream)
                    Toast.makeText(this@ViewPagerActivity,"다운로드가 완료되었습니다.",Toast.LENGTH_SHORT).show()


                    fileOutputStream.flush()
                    fileOutputStream.close()



                    //갤러리에 이미지 저장
                    var intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    intent.setData(Uri.fromFile(imageFile))
                    sendBroadcast(intent)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }
            })
        if(bitmap != null){
            Log.e("bitmap","not null")
        }else{

            Log.e("bitmap","null")
        }


    }

    //권한 체크 메소드
    private fun askPermission() {

        ActivityCompat.requestPermissions(this, permission, 100);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(100 == requestCode){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                download()
            }else{
                Toast.makeText(this, "권한 체크", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}