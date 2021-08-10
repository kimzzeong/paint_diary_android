package com.example.paint_diary

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.example.paint_diary.databinding.ActivityPaintBinding
import kotlinx.android.synthetic.main.activity_paint.*
import kotlinx.android.synthetic.main.activity_profile_modify.*
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.io.ByteArrayOutputStream

class PaintActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPaintBinding
    var defaultColor : Int? = null


    private var paintView: PaintView? = null
    var metrics = DisplayMetrics()

//그림 저장은 나중에 상세 페이지 보여줄 때 할 것
   // lateinit var PaintPath : String //문자열 형태의 그림 경로 값


//    val path = File(
//        Environment.getExternalStorageDirectory().toString() + "/Pictures",
//        "paint"
//    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        paint_toolbar.inflateMenu(R.menu.paint_save_menu)
//        paint_toolbar.setTitleTextColor(Color.BLACK)
//        paint_toolbar.title = "그림그리기"
        setSupportActionBar(binding.paintToolbar)
        supportActionBar?.title = "그림그리기"
//

        getWindowManager().getDefaultDisplay().getMetrics(metrics)
        paintView = findViewById(R.id.paint_view)
        if (paintView != null) {
            paintView!!.init(metrics)
        }
        val canvasPopup = PopupMenu(this, binding.btnEraser)
        menuInflater.inflate(R.menu.canvas_menu, canvasPopup.menu)
        defaultColor = ContextCompat.getColor(this, R.color.black)
        binding.btnColor.setOnClickListener {

            paintView?.currentColor = defaultColor
            openColorPicker()
        }
        binding.penSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.txtPenSize.text = progress.toString() + "dp"
                //binding.signatureView.penSize = progress.toFloat()
                paintView?.BRUSH_SIZE = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        binding.btnEraser.setOnClickListener {
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

        binding.undo.setOnClickListener {
            paintView?.onClickUndo()
        }
        binding.redo.setOnClickListener {
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
                val stream = ByteArrayOutputStream()
                paintView!!.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                val in1 = Intent(applicationContext, DiaryActivity::class.java)
                in1.putExtra("image", byteArray)
                startActivity(in1)
                return true
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