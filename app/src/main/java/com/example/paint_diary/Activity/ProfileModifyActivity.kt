package com.example.paint_diary.Activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import coil.load
import com.bumptech.glide.Glide
import com.example.paint_diary.Data.ProfileChange
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.example.paint_diary.databinding.ActivityProfileModifyBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_profile_modify.*
import kotlinx.android.synthetic.main.fragment_mypage.*
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileModifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileModifyBinding
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private val PAINT_REQUEST_CODE = 3
    lateinit var curPhotoPath : String //문자열 형태의 사진 경로 값
    //유저 인덱스값
    var user_idx : String? = null
    var user_nickname : String? = null
    var user_introduction : String? = null
    var user_profile : String? = null
    var uriPath : String? = null
    private lateinit var getResultImage : ActivityResultLauncher<Intent>
    val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/Drawing/" //사진저장 경로
    val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) //사진파일이름에 들어감
    val fileName = "${timestamp}.jpeg"
    var paint_flag : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityProfileModifyBinding.inflate(layoutInflater)
            setContentView(binding.root)

            profile_toolbar.inflateMenu(R.menu.profile_save_menu)
            profile_toolbar.setTitleTextColor(Color.BLACK)
            profile_toolbar.setTitle("프로필 수정")
            //profile_photo.setImageResource(R.drawable.basic_profile)
            //profile_nickname.setText(intent.getStringExtra("user_introduction"))

            val cameraPopup = PopupMenu(this, profile_photo)
            menuInflater?.inflate(R.menu.profile_menu, cameraPopup.menu)
            user_idx = intent.getStringExtra("user_idx")
            user_nickname = intent.getStringExtra("user_nickname")
            user_introduction = intent.getStringExtra("user_introduction")
            user_profile = intent.getStringExtra("profile_photo")

            //Log.e("onCreate", user_idx!!)

            binding.profileNickname.setText(user_nickname)
            binding.profileIntro.setText(user_introduction)
            if(user_profile != null){
                Glide.with(this) // context
                    .load(user_profile) // 이미지 url
                    .into(binding.profilePhoto) // 붙일 imageView
            }else{
                binding.profilePhoto.setImageResource(R.drawable.basic_profile)
            }

        //프로필 그림그리기로 가져옴
            getResultImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                if(result.resultCode == RESULT_OK){
                    val arr = result.data?.getByteArrayExtra("image")
                    var bitmap = BitmapFactory.decodeByteArray(arr, 0, arr!!.size)
                    profile_photo.setImageBitmap(bitmap)
                    uriPath = result.data.toString()
                        val folder = File(folderPath)
                        if(!folder.isDirectory){ //현재 해당 경로의 폴더가 존재하는지 확인
                            folder.mkdirs() // make Directory 줄임말로 해당 경로에 자동으로 디렉토리 생성
                        }
                        //실제적인 저장 처리
                        val out = FileOutputStream(folderPath + fileName)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        uriPath = folderPath+fileName
                    paint_flag = true
                    //Log.e("uriPath",uriPath.toString())
                        //Toast.makeText(this,"사진이 저장되었습니다.",Toast.LENGTH_SHORT).show()


                }

            }

            setSupportActionBar(binding.profileToolbar)

            profile_photo?.setOnClickListener {

                cameraPopup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.profile_camera -> {
                            cameraCheckPermission()

                        }

                        R.id.profile_paint -> {
//                            var intent = Intent(this, PaintActivity::class.java)
//                            intent.putExtra("paint", "paint")
//                            startActivity(intent)
                            val intent = Intent(this,PaintActivity::class.java)
                            intent.putExtra("paint","paint")
                            getResultImage.launch(intent)
                        }

                        R.id.profile_gallery -> {
                            galleryCheckPermission()
                        }
                        R.id.profile_basic -> {
                            profile_photo.setImageResource(R.drawable.basic_profile)
                            uriPath = "basic"
                        }

                    }
                    false
                }

                cameraPopup.show()

            }
    }
        private fun galleryCheckPermission() {

            Dexter.withContext(this).withPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    gallery()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@ProfileModifyActivity,
                        "You have denied the storage permission to select image",
                        Toast.LENGTH_SHORT
                    ).show()
                    showRotationalDialogForPermission()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?, p1: PermissionToken?
                ) {
                    showRotationalDialogForPermission()
                }
            }).onSameThread().check()
        }

        private fun gallery() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            //intent.putExtra("crop", true)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }


        private fun cameraCheckPermission() {

            Dexter.withContext(this)
                .withPermissions(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                ).withListener(

                    object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            report?.let {

                                if (report.areAllPermissionsGranted()) {
                                    camera()
                                }

                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            showRotationalDialogForPermission()
                        }

                    }
                ).onSameThread().check()
        }


        //카메라 촬영
        private fun camera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, CAMERA_REQUEST_CODE)
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { cameraIntent ->
                cameraIntent.resolveActivity(packageManager)?.also {
                    val photoFile : File? = try{
                        createImageFile()
                    }catch (e: IOException){
                        null
                    }
                    photoFile?.also {
                        val photoURI : Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.paint_diary.fileprovider",
                            it
                        )
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
                    }
                }
            }
        }

        //이미지 파일 생성
        @Throws(IOException::class)
        fun createImageFile(): File? { // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_$timeStamp.jpg"
            var imageFile: File?
            val storageDir = File(
                Environment.getExternalStorageDirectory().toString() + "/Pictures",
                "paint"
            )
            if (!storageDir.exists()) {
                Log.i("mCurrentPhotoPath1", storageDir.toString())
                storageDir.mkdirs()
            }
            imageFile = File(storageDir, imageFileName)
            curPhotoPath = imageFile.absolutePath
            return imageFile
        }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (resultCode == Activity.RESULT_OK) {

                when (requestCode) {

                    CAMERA_REQUEST_CODE -> {

                        //val bitmap = data?.extras?.get("data") as Bitmap
                        val bitmap: Bitmap
                        val file = File(curPhotoPath)
                        //cropImageCamera(data?.data) //이미지를 선택하면 여기가 실행됨

                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                contentResolver, Uri.fromFile(
                                    file
                                )
                            )
                            binding.profilePhoto.load(bitmap)
                        } else {
                            val decode = ImageDecoder.createSource(
                                this.contentResolver,
                                Uri.fromFile(file)
                            )
                            galleryAddPic()
                            bitmap = ImageDecoder.decodeBitmap(decode)
                            binding.profilePhoto.load(bitmap)
                        }

                        // cropImage(data?.data) //이미지를 선택하면 여기가 실행됨

                        cropImage(Uri.fromFile(file)) //이미지를 선택하면 여기가 실행됨
                        savePhoto(bitmap)

                        //we are using coroutine image loader (coil)

//                    {
//                        crossfade(true)
//                        crossfade(1000)
//                        transformations()
//                    }
                        paint_flag = false
                    }

                    GALLERY_REQUEST_CODE -> {
                        Log.e("Gallery", "GALLERY_REQUEST_CODE")
                        data?.data?.let { uri ->
                            cropImage(uri) //이미지를 선택하면 여기가 실행됨
                        }
                        //binding.profilePhoto.load(data?.data)
//                    {
//                        crossfade(true)
//                        crossfade(1000)
//                        transformations()
//                    }

                        paint_flag = false
                    }
                    CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                        Log.e("CropImage", "CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                        val result = CropImage.getActivityResult(data)
                        result.uri?.let {
                            binding.profilePhoto.setImageBitmap(result.bitmap)
                            binding.profilePhoto.setImageURI(result.uri)

                        }

                        uriPath = result.uri.toString()

                        paint_flag = false
                    }
                }

            }

        }
        private fun cropImage(uri: Uri?){
            Log.e("cropImage", "cropImage")
            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                //사각형 모양으로 자른다
                .start(this)

        }

        private fun galleryAddPic() {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(curPhotoPath)
                val contentUri: Uri = Uri.fromFile(f)
                mediaScanIntent.setData(contentUri)
                sendBroadcast(mediaScanIntent)
            }
        }

        //갤러리에 저장
        private fun savePhoto(bitmap: Bitmap) {
            val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" //사진저장 경로
            val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val fileName = "${timestamp}.jpeg"
            val folder = File(folderPath)
            if(!folder.isDirectory){ //현재 해당 경로의 폴더가 존재하는지 확인
                folder.mkdirs() // make Directory 줄임말로 해당 경로에 자동으로 디렉토리 생성
            }
            //실제적인 저장 처리
            val out = FileOutputStream(folderPath + fileName)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)

            //Toast.makeText(this,"사진이 저장되었습니다.",Toast.LENGTH_SHORT).show()
        }

        private fun showRotationalDialogForPermission() {
            AlertDialog.Builder(this)
                .setMessage("앱 설정에서 권한을 허용해 주세요")

                .setPositiveButton("설정") { _, _ ->

                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)

                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }

                .setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
        //
        private fun updateProfileImage() {
            var gson: Gson = GsonBuilder()
                .setLenient()
                .create()

            var retrofit = Retrofit.Builder()
                .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            var profilePhotoChange = retrofit.create(IRetrofit::class.java)

            //프로필 사진을 제외한 변경
            if(uriPath.equals(null)){
                profilePhotoChange.profileChange(
                    user_idx!!,
                    binding.profileNickname.text.toString(),
                    binding.profileIntro.text.toString()
                ).enqueue(object : Callback<ProfileChange> {
                    override fun onResponse(
                        call: Call<ProfileChange>,
                        response: Response<ProfileChange>
                    ) {
                        var profile = response.body()
                        if (profile != null) {
                            Toast.makeText(
                                this@ProfileModifyActivity,
                                profile.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        finish()
                    }

                    override fun onFailure(call: Call<ProfileChange>, t: Throwable) {
                        Toast.makeText(this@ProfileModifyActivity, "다시 시도해 주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }

                })

                //기본사진으로 프로필 변경
            }else if(uriPath.equals("basic")){
                profilePhotoChange.profileBasic(
                    user_idx!!,
                    binding.profileNickname.text.toString(),
                    binding.profileIntro.text.toString(),
                    uriPath!!
                ).enqueue(object : Callback<ProfileChange> {
                    override fun onResponse(
                        call: Call<ProfileChange>,
                        response: Response<ProfileChange>
                    ) {
                        var profile = response.body()
                        if (profile != null) {
                            Toast.makeText(
                                this@ProfileModifyActivity,
                                profile.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        finish()
                    }

                    override fun onFailure(call: Call<ProfileChange>, t: Throwable) {
                        Toast.makeText(this@ProfileModifyActivity, "다시 시도해 주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }

                })

            }
            //프로필 사진 변경
            else{
                var profileImage : String? = null
                //creating a file
                    if(paint_flag == false){

                        val split_uriPath = uriPath!!.split("file://")
                        profileImage = split_uriPath[1]

                    }else {
                        profileImage = uriPath
                    }
                // Log.e("curPhotoPath",curPhotoPath)
                val file = File(profileImage)	// 경로 예시 : /storage/emulated/0/Download/filename.pdf
                Log.e("uploadProfile", "2")
                var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                var user_idx_request : RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    user_idx!!
                )
                var user_nickname_request : RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    binding.profileNickname.text.toString()
                )
                var user_intro_request : RequestBody = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    binding.profileIntro.text.toString()
                )

                Log.e("uploadProfile", "3")

                var body : MultipartBody.Part = MultipartBody.Part.createFormData(
                    "profilePhoto",
                    file.name,
                    requestBody
                )



                if (user_idx != null) {
                    profilePhotoChange.profilePhoto(
                        user_idx_request,
                        user_nickname_request,
                        user_intro_request,
                        body
                    ).enqueue(object : Callback<ProfileChange> {
                        override fun onResponse(
                            call: Call<ProfileChange>,
                            response: Response<ProfileChange>
                        ) {
                            var profile = response.body()
                            Toast.makeText(
                                this@ProfileModifyActivity,
                                profile?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            //  Log.e("onResponse", profile?.user_idx!!)
                            finish()
                        }

                        override fun onFailure(call: Call<ProfileChange>, t: Throwable) {
                            Toast.makeText(
                                this@ProfileModifyActivity,
                                "다시 시도해 주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("onFailure", t.localizedMessage)
                        }

                    })
                }
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.profile_save -> {
                    updateProfileImage()
                    return super.onOptionsItemSelected(item)
                }
                else -> return super.onOptionsItemSelected(item)
            }
        }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.profile_save_menu, menu)
            return true
        }

}
