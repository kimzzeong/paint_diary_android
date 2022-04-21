package com.example.paint_diary.Activity

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.MyService
import com.example.paint_diary.Paint.PaintView
import com.example.paint_diary.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_paint.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class PaintActivity : AppCompatActivity(){
   // private lateinit var binding: ActivityPaintBinding
    var defaultColor : Int? = null

    private var paintView: PaintView? = null
    var metrics = DisplayMetrics()
    var switch_brush_option : Boolean = false

    var user_idx : String? = null
    companion object {
        var paintactivity: Activity? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //    binding = ActivityPaintBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_paint)
        setSupportActionBar(paint_toolbar)
        supportActionBar?.title = "그림그리기"
        paintactivity = this@PaintActivity

        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        user_idx = sharedPreferences?.getString("user_idx", "")
        Log.e("oncreate", user_idx!!)

        getWindowManager().getDefaultDisplay().getMetrics(metrics)
        paintView = findViewById(R.id.paint_view)
        if (paintView != null) {
            paintView!!.init(metrics)
        }
        val canvasPopup = PopupMenu(this, btnEraser)
        menuInflater.inflate(R.menu.canvas_menu, canvasPopup.menu)
        defaultColor = ContextCompat.getColor(this, R.color.black)
        //color picker
        btnColor.setOnClickListener {

            paintView?.currentColor = defaultColor
            openColorPicker()
        }

//        //노티테스트
//        test_btn.setOnClickListener {
//            sendNotification()
//        }
//        test2_btn.setOnClickListener {
//            //Toast.makeText(getApplicationContext(),"Service 시작",Toast.LENGTH_SHORT).show()
//        }
//        test3_btn.setOnClickListener {
//            //Toast.makeText(getApplicationContext(),"Service 끝",Toast.LENGTH_SHORT).show()
//            var intent = Intent(this, MyService::class.java)
//            stopService(intent)
//
//        }

        //지우개
        btnEraser.setOnClickListener {
            canvasPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.canvas_eraser -> {
                        paintView?.eraser()
                    }

                    R.id.canvas_clear -> {
                        // binding.signatureView.clearCanvas()
                        paintView?.clear()
                        paintView?.currentColor = defaultColor
                    }

                }
                false
            }
            canvasPopup.show()
        }

        //브러쉬 굵기
        penSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                paintView?.BRUSH_SIZE = progress
                txtPenSize.setText(progress.toString() + "dp").toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        //브러쉬 창 open/close
        btnBrush.setOnClickListener {
            if(switch_brush_option){
                brushOption.visibility = View.INVISIBLE
                switch_brush_option = false
            }else{
                brushOption.visibility = View.VISIBLE
                switch_brush_option = true
            }
        }
        //기본 브러쉬
        default_brush.setOnClickListener {
            paintView?.setBrushType(0, 1F)
            brushOption.visibility = View.INVISIBLE
        }
        //네온 브러쉬
        neon_brush.setOnClickListener {
            paintView?.setBrushType(1, 28F)
            brushOption.visibility = View.INVISIBLE
        }
        //투명도 브러쉬
        inner_brush.setOnClickListener {
            paintView?.setBrushType(2, 100F)
            brushOption.visibility = View.INVISIBLE
        }
        //블러 브러쉬
        blur_brush.setOnClickListener {
            paintView?.setBrushType(3, 10F)
            brushOption.visibility = View.INVISIBLE
        }

        undo.setOnClickListener {
            paintView?.onClickUndo()
        }
        redo.setOnClickListener {
            paintView?.onClickRedo()
        }
    }

    private fun openColorPicker() {

        val colorPicker = AmbilWarnaDialog(this, defaultColor!!, object : OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {

                paintView?.currentColor = defaultColor
            }

            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                defaultColor = color
                paintView?.currentColor = defaultColor
                btnColor.setColorFilter(defaultColor!!)
                // binding.signatureView.penColor = color
            }
        })
        colorPicker.show()
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")
        if(user_idx != ""){
        }else{//로그인 상태가 아니면 다이얼로그를 이용해 로그인 페이지로 이동

            //Log.e("TAG", "쉐어드에 저장된 아이디 = " + user_idx)
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("로그인을 하셔야 이용 가능한 서비스입니다.\n지금 바로 로그인하시겠습니까?")
            dialog.setCancelable(false)
            dialog.setPositiveButton("네"){ dialog, id ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            dialog.setNegativeButton("아니오"){ dialog, id ->
                onBackPressed()
            }
            dialog.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.paint_save -> {
                var profile = intent.getStringExtra("paint")
                if (profile.equals("paint")) {
                    val stream = ByteArrayOutputStream()
                    paintView!!.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    val in1 = Intent(applicationContext, ProfileModifyActivity::class.java)
                    in1.putExtra("image", byteArray)
                    setResult(RESULT_OK, in1)
                    finish()
                    return true
                } else {
                    val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                    var user_idx: Int? =
                            Integer.parseInt(sharedPreferences?.getString("user_idx", ""))
                    val stream = ByteArrayOutputStream()
                    paintView!!.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    val in1 = Intent(applicationContext, DiaryActivity::class.java)
                    in1.putExtra("image", byteArray)
                    in1.putExtra("update", "")
                    in1.putExtra("diary_writer", user_idx)
                    startActivity(in1)
                    return true
                }
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.paint_save_menu, menu)
        return true
    }


}