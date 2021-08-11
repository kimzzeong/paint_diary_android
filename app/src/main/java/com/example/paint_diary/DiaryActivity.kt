package com.example.paint_diary

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.paint_diary.databinding.ActivityDiaryBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class DiaryActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDiaryBinding
    var bitmap : Bitmap? = null
    var id_value : Int? = null
    var uriPath : String? = null
    val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/Drawing/" //사진저장 경로
    val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val fileName = "${timestamp}.jpeg"
    val currentPath : String = folderPath+fileName
    var secret : Int = 0
    var align : Int = 0
    private val userArrayList = ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.diaryToolbar)
        supportActionBar?.title = "일기 쓰기"

        val intent = intent
        val arr = intent.getByteArrayExtra("image")
        bitmap = BitmapFactory.decodeByteArray(arr, 0, arr!!.size)


        binding.image.setImageBitmap(bitmap)

        val user1 = User("0", "해")
        userArrayList.add(user1)
        val user2 = User("1", "구름")
        userArrayList.add(user2)
        val user3 = User("2", "달")
        userArrayList.add(user3)
        val user4 = User("3", "비")
        userArrayList.add(user4)
        val user5 = User("4", "눈")
        userArrayList.add(user5)

        val spinnerAdapter = SpinnerAdapter(this, R.layout.custom_spinner_adapter, userArrayList)
        binding.spinnerWeather.setAdapter(spinnerAdapter)

        //스피너 값 받아옴
        binding.spinnerWeather.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                id_value = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {} // 스피너 쓰기위한 어뎁터 메소드 선언부
        })

        //왼쪽정렬
        binding.alignLeft.setOnClickListener {
            binding.diaryContent.gravity = Gravity.LEFT
            align = 0
        }
        //가운데 정렬
        binding.alignCenter.setOnClickListener {
            binding.diaryContent.gravity = Gravity.CENTER_HORIZONTAL
            align = 1
        }
        //오른쪽 정렬
        binding.alignRight.setOnClickListener {
            binding.diaryContent.gravity = Gravity.RIGHT
            align = 2
        }

        //비밀글
        binding.diarySecret.setOnClickListener {
            if(secret == 0){
                binding.diarySecret.setImageResource(R.drawable.ic_baseline_lock_24)
                secret = 1
            }else{
                binding.diarySecret.setImageResource(R.drawable.ic_baseline_lock_open_24)
                secret = 0
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.diary_save -> {
                if (bitmap != null) {
                    galleryAddPic()
                    savePhoto(bitmap!!)

                    uploadDiary()

                    val intent = Intent(this, DiaryInfoActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //다이어리 업로드
    private fun uploadDiary() {

        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        var gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        var retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        var diaryUpload = retrofit.create(IRetrofit::class.java)

        val file = File(uriPath)
        Log.e("uriPath", uriPath!!)

        var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(),file)
        var user_idx_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), user_idx!!)
        var diaryTitle_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.diaryTitle.text.toString())
        var id_value_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), id_value.toString())
        var diary_range_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), align.toString())
        var diary_content_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.diaryContent.text.toString())
        var diary_secret_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), secret.toString())

        var body : MultipartBody.Part = MultipartBody.Part.createFormData("diaryBitmap",file.name,requestBody)

        diaryUpload.diaryUpload(user_idx_request, diaryTitle_request, id_value_request, diary_range_request, diary_content_request, diary_secret_request, body).enqueue(object : Callback<DiaryInfo> {
            override fun onResponse(call: Call<DiaryInfo>, response: Response<DiaryInfo>) {
                var diary = response.body()
                Toast.makeText(this@DiaryActivity, diary?.message, Toast.LENGTH_SHORT).show()
                //  Log.e("onResponse", profile?.user_idx!!)
               // finish()
            }

            override fun onFailure(call: Call<DiaryInfo>, t: Throwable) {
                Toast.makeText(this@DiaryActivity, "다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                Log.e("onFailure", t.localizedMessage)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.diary_save_menu, menu)
        return true
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPath)
            val contentUri: Uri = Uri.fromFile(f)
            mediaScanIntent.setData(contentUri)
            sendBroadcast(mediaScanIntent)
        }
        Log.e("currentPath", currentPath)
    }

    //비트맵 이미지 파일로 저장
    private fun savePhoto(bitmap: Bitmap) {
        val folder = File(folderPath)
        if(!folder.isDirectory){ //현재 해당 경로의 폴더가 존재하는지 확인
            folder.mkdirs() // make Directory 줄임말로 해당 경로에 자동으로 디렉토리 생성
        }
        //실제적인 저장 처리
        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        uriPath = folderPath+fileName
        //Toast.makeText(this,"사진이 저장되었습니다.",Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}