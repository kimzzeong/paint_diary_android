package com.example.paint_diary

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import coil.load
import com.example.paint_diary.databinding.ActivityProfileModifyBinding
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileModifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileModifyBinding
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    lateinit var curPhotoPath : String //문자열 형태의 사진 경로 값
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileModifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profile_toolbar.inflateMenu(R.menu.profile_save_menu)
        profile_toolbar.setTitleTextColor(Color.BLACK)
        profile_toolbar.setTitle("프로필 수정")

        profile_nickname.setText(intent.getStringExtra("user_nickname"))
        profile_photo.setImageResource(R.drawable.basic_profile)
        //profile_nickname.setText(intent.getStringExtra("user_introduction"))

        val cameraPopup = PopupMenu(this, profile_photo)
        menuInflater?.inflate(R.menu.profile_menu, cameraPopup.menu)
        profile_photo?.setOnClickListener {

            cameraPopup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.profile_camera -> {
                        cameraCheckPermission()

                    }

                    R.id.profile_gallery -> {
                        galleryCheckPermission()
                    }
                    R.id.profile_basic ->
                        profile_photo.setImageResource(R.drawable.basic_profile)
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

                    data?.data?.let { uri ->
                        cropImage(uri) //이미지를 선택하면 여기가 실행됨
                    }
                    savePhoto(bitmap)

                    //we are using coroutine image loader (coil)

//                    {
//                        crossfade(true)
//                        crossfade(1000)
//                        transformations()
//                    }
                }

                GALLERY_REQUEST_CODE -> {
                    Log.e("Gallery", "GALLERY_REQUEST_CODE")
                    data?.data?.let { uri ->
                        cropImage(uri) //이미지를 선택하면 여기가 실행됨
                    }
                    binding.profilePhoto.load(data?.data)
//                    {
//                        crossfade(true)
//                        crossfade(1000)
//                        transformations()
//                    }

                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.e("CropImage", "CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    result.uri?.let {
                        binding.profilePhoto.setImageBitmap(result.bitmap)
                        binding.profilePhoto.setImageURI(result.uri)

                    }
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
        print("dddddddddddddddddddddd")
        //val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" //사진저장 경로
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/" //사진저장 경로
        print("dddddddddddddddddddddd")
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

}