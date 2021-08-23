package com.example.paint_diary.Activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import com.bumptech.glide.Glide
import com.example.paint_diary.*
import com.example.paint_diary.Adapter.SpinnerAdapter
import com.example.paint_diary.databinding.ActivityDiaryBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_diary.*
import kotlinx.android.synthetic.main.activity_diary_info.*
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
    val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) //사진파일이름에 들어감
    val fileName = "${timestamp}.jpeg"
    val currentPath : String = folderPath+fileName
    var secret : Int = 0
    var align : Int = 0
    var diary_idx : Int ? = null
    var diary_writer : Int ? = null
    var update : String  = ""
    var retrofit_date = ""
    private val userArrayList = ArrayList<User>()
    var formatDate = SimpleDateFormat("yyyy년 MM월 dd일",Locale.KOREA)
    var formatDateSave = SimpleDateFormat("yyyy-MM-dd",Locale.KOREA)

    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.diaryToolbar)
        supportActionBar?.title = "일기 쓰기"

        val intent = intent
        update = intent.getStringExtra("update")!!
        Log.e("update",update)
        if(update.equals("update")){
            diary_idx = intent.getIntExtra("diary_idx",0)
            diary_writer = intent.getIntExtra("diary_writer",0)

            requestDiaryinfo()
        }else{
            Log.e("equals","1")
            setting()
            Log.e("equals","2")
        }

        diary_date.setOnClickListener ( View.OnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val selectDate : Calendar = Calendar.getInstance()
                selectDate.set(Calendar.YEAR,year)
                selectDate.set(Calendar.MONTH,month)
                selectDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                val date = formatDate.format(selectDate.time)
                retrofit_date = formatDateSave.format(selectDate.time)
                Toast.makeText(this,"Date:"+date,Toast.LENGTH_SHORT).show()
                diary_date.text = date

            }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH),getDate.get(Calendar.DAY_OF_MONTH))
            datePicker.show()

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

    private fun requestDiaryinfo() {
        var gson: Gson = GsonBuilder()
                .setLenient()
                .create()

        var retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        var requestDiaryInfo = retrofit.create(IRetrofit::class.java)

        requestDiaryInfo.requestDiaryInfo(diary_idx!!,diary_writer!!).enqueue(object : Callback<DiaryInfoPage> {
            override fun onResponse(call: Call<DiaryInfoPage>, response: Response<DiaryInfoPage>) {
                val diaryInfo = response.body()
                if (diaryInfo != null) {
                    var diary_painting : String = "http://3.36.52.195/diary/"


                    //일기 그림
                    diary_painting += diaryInfo.diary_painting
                    Glide.with(this@DiaryActivity)
                            .load(diary_painting)
                            .into(binding.paintImage)

                    //날짜
                    //diaryInfo_date.text = diaryInfo.diary_date

                    //제목
                    binding.diaryTitle.setText(diaryInfo.diary_title)

                    //날씨
                    spinnerSetting()
                    binding.spinnerWeather.setSelection(diaryInfo.diary_weather)

                    //내용
                    binding.diaryContent.setText(diaryInfo.diary_content)

                    //내용 정렬
                    when (diaryInfo.diary_range) {
                        0 -> binding.diaryContent.gravity = Gravity.START
                        1 -> binding.diaryContent.gravity = Gravity.CENTER_HORIZONTAL
                        2 -> binding.diaryContent.gravity = Gravity.END
                    }
                }
            }

            override fun onFailure(call: Call<DiaryInfoPage>, t: Throwable) {
                onBackPressed()
                //Toast.makeText(this@DiaryInfoActivity,"다시 시도해주세요.",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setting() {
        val arr = intent.getByteArrayExtra("image")
        bitmap = BitmapFactory.decodeByteArray(arr, 0, arr!!.size)
        binding.paintImage.setImageBitmap(bitmap)

        spinnerSetting()
    }

    private fun spinnerSetting() {
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

        val spinnerAdapter = SpinnerAdapter(
                this,
                R.layout.custom_spinner_adapter,
                userArrayList
        )
        binding.spinnerWeather.setAdapter(spinnerAdapter)

        //스피너 값 받아옴
        binding.spinnerWeather.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                id_value = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {} // 스피너 쓰기위한 어뎁터 메소드 선언부
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.diary_save -> {
                if(update.equals("update")){
                    updateDiary()
                }else{
                    if (bitmap != null) {
                       // galleryAddPic()
                        //savePhoto(bitmap!!)
                        Log.e("date : ",retrofit_date) // ->ok
                       // uploadDiary()
                    }
                }
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateDiary() {
//        var diaryUpdate = retrofit.create(IRetrofit::class.java)
//
//        diaryUpdate.requestUpdateDiary(diary_idx!!,binding.diaryTitle.text.toString(), id_value!!,align,binding.diaryContent.text.toString(),secret).enqueue(object :Callback<DiaryInfoPage>{
//            override fun onResponse(call: Call<DiaryInfoPage>, response: Response<DiaryInfoPage>) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onFailure(call: Call<DiaryInfoPage>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })

    }

    //다이어리 업로드
    private fun uploadDiary() {

        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        var diaryUpload = retrofit.create(IRetrofit::class.java)

        val file = File(uriPath)
        Log.e("uriPath", uriPath!!)

        val requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(),file)
        val user_idx_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), user_idx!!)
        val diaryTitle_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.diaryTitle.text.toString())
        val id_value_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), id_value.toString())
        val diary_range_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), align.toString())
        val diary_content_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.diaryContent.text.toString())
        val diary_secret_request : RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), secret.toString())

        val body : MultipartBody.Part = MultipartBody.Part.createFormData("diaryBitmap",file.name,requestBody)

        diaryUpload.diaryUpload(user_idx_request, diaryTitle_request, id_value_request, diary_range_request, diary_content_request, diary_secret_request, body).enqueue(object : Callback<DiaryUpload> {
            override fun onResponse(call: Call<DiaryUpload>, response: Response<DiaryUpload>) {
                var diary = response.body()
                diary_idx = diary?.diary_idx

                val intent = Intent(this@DiaryActivity, MainActivity::class.java)
                intent.putExtra("diary_idx",diary_idx)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val paintActivity: PaintActivity? = null
                paintActivity?.finish()
                startActivity(intent)
                Toast.makeText(this@DiaryActivity,"글이 정상적으로 등록되었습니다.",Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onFailure(call: Call<DiaryUpload>, t: Throwable) {
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