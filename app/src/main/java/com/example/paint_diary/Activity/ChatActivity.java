package com.example.paint_diary.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paint_diary.Adapter.ChatAdapter;
import com.example.paint_diary.Data.Chat2;
import com.example.paint_diary.IRetrofit;
import com.example.paint_diary.MyService;
import com.example.paint_diary.R;
import com.example.paint_diary.ServiceThread;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.OnListItemSelectedInterface {

    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    //private String ip = "192.168.56.1"; //??????
    private String ip = "3.36.52.195"; //aws ip ??????
    private int port = 8888; //????????????


    private final int CAMERA_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE = 2;
    private final int PAINT_REQUEST_CODE = 3;
    String curPhotoPath; //????????? ????????? ?????? ?????? ???
    File photoFile;

    String UserID = "", user_nickname;
    ImageView chatbutton; // ?????? ?????????
    ImageView imagebutton; // ????????? ????????? ?????? ??? ?????? ?????????
    EditText message;
    String sendmsg;
    String read;
    ArrayList<Chat2> list = new ArrayList<>(); // ?????? ?????????
    RecyclerView recyclerView;
    ChatAdapter adapter;

//    public String getRoom_idx() {
//        return room_idx;
//    }
//
//    public void setRoom_idx(String room_idx) {
//        this.room_idx = room_idx;
//    }

    public String room_idx, profile_photo = "";
    Date date_now;
    SimpleDateFormat fourteen_format; //?????? ??????
    String date;
    String[] photo; //?????? ??????????????? ????????? ?????? ??????
    TextView chat_room_name;
    public static Context context;
    ArrayList<String>room = new ArrayList<>();


    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    private static final String SHARED_PREF_NAME = "user";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHandler = new Handler();
        message = (EditText) findViewById(R.id.message);
        context = this;


        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        UserID = sharedPreferences.getString("user_idx", String.valueOf(Context.MODE_PRIVATE));
        user_nickname = sharedPreferences.getString("user_nickname", String.valueOf(Context.MODE_PRIVATE));
        chatbutton = findViewById(R.id.chatbutton);
        imagebutton = findViewById(R.id.imagebutton);
        chat_room_name = findViewById(R.id.chat_room_name);


        //????????? ?????? ?????? ??? ?????????, ????????? ??????????????? ?????????
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_photo_Dialog();
            }
        });




        new Thread() {
            public void run() {
                try {

                    //ServiceThread serviceThread = new ServiceThread(mHandler,UserID,room);
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while(true){

                        read = input.readLine();
                        String[] msg = new String[10];
                        msg = read.split(">>");
                        Log.e("msg size",msg.length+"");
                        Log.e("msg profile",msg[4]);
                        System.out.println("TTTTTTTT"+read);
                        if(read!=null){
                            mHandler.post(new msgUpdate(msg[0],msg[1],msg[2],msg[3],msg[4],Integer.parseInt(msg[5]),msg[6]));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();


        //?????? ????????? ?????????
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();
                if(sendmsg.isEmpty()){
                    Toast.makeText(ChatActivity.this,"???????????? ????????? ?????????.",Toast.LENGTH_SHORT).show();
                }else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {

                                date_now = new Date(System.currentTimeMillis()); // ??????????????? ????????? Date????????? ????????????
                                // ?????????????????? 14?????? ??????
                                fourteen_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                date = fourteen_format.format(date_now);

                                Log.e("msg profile",profile_photo);
                                Log.e("msg UserID",UserID);
                                Log.e("msg user_nickname",user_nickname);
                                Log.e("msg room_idx",room_idx);
                                Log.e("msg sendmsg",sendmsg);
                                Log.e("msg date",date);
                                Log.e("total",UserID + ">>" +user_nickname +">>" + room_idx + ">>" + sendmsg + ">>" + profile_photo + ">>" + 0 + ">>" + date);
                                sendWriter.println(UserID + ">>" +user_nickname +">>" + room_idx + ">>" + sendmsg + ">>" + profile_photo + ">>" + 0 + ">>" + date);
                                sendWriter.flush();
                                message.setText("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
    }

    public class msgUpdate implements Runnable{
        private final String msg;
        private final String user_idx;
        private final String message_room_idx;
        private final String nickname;
        private final String photo;
        private final String date;
        private int type;

        public msgUpdate(String user_idx, String nickname, String room_idx, String str, String photo, int type, String date) {
            this.msg=str;
            this.user_idx = user_idx;
            this.message_room_idx = room_idx;
            this.nickname = nickname;
            this.photo = photo;
            this.type = type;
            this.date = date;
        }

        @Override
        public void run() {

            if(room_idx.equals(message_room_idx)){
                Chat2 chat = new Chat2(msg,user_idx,date,photo,nickname,room_idx,type);
                list.add(chat);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1); // ?????? ?????? ???????????? ????????? ??????
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        room_idx = String.valueOf(intent.getIntExtra("room_idx",0));
        chat_room_name.setText(intent.getStringExtra("room_name"));
        room = intent.getStringArrayListExtra("room_list");

        recyclerView = findViewById(R.id.chatView) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;


        adapter = new ChatAdapter(list,this,this) ;

        recyclerView.setAdapter(adapter) ;


        Log.e("profile_photo",profile_photo);
        profile_photo = sharedPreferences.getString("profile_photo","??????");
        Log.e("room_idx",room_idx+"");

        requestChat(room_idx,this);

    }

    //?????? ????????? ????????????
    public void requestChat(String room_idx, Context context){


        IRetrofit api = retrofit.create(IRetrofit.class);

        Call<ArrayList<Chat2>> call = api.requestChat(room_idx);

        call.enqueue(new Callback<ArrayList<Chat2>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Chat2>> call, @NotNull Response<ArrayList<Chat2>> response) {
                list = response.body();

                adapter = new ChatAdapter(list,ChatActivity.this,ChatActivity.this) ;
                recyclerView.setAdapter(adapter) ;

                adapter.notifyDataSetChanged();

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        recyclerView.scrollToPosition(list.size()-1);
//                    }
//                }, 50); //sometime not working, need some delay

               // recyclerView.scrollToPosition(list.size()-1); // ?????? ?????? ???????????? ????????? ??????

                //Log.e("?????? ????????? size",""+list.get(0).getType());
            }

            @Override
            public void onFailure(Call<ArrayList<Chat2>> call, Throwable t) {
                Log.e("?????? ????????? ???????????? ??????",t.getLocalizedMessage());


            }
        });
    }

    //????????? ????????? ????????? ?????? ?????????/????????? ?????? ??????????????? ?????????
    public void show_photo_Dialog() {
        photo = getResources().getStringArray(R.array.photoArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.photoChoice)
                .setItems(R.array.photoArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(photo[which].equals("?????????")){
                            cameraCheckPermission();
                        }else{
                            galleryCheckPermission();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void galleryCheckPermission() {

        Dexter.withContext(this) .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE) .withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                gallery();
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(ChatActivity.this,"You have denied the storage permission to select image",Toast.LENGTH_SHORT).show();
                showRotationalDialogForPermission();
            }
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                showRotationalDialogForPermission();
            }
        }).check();

    }

    private void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }


    private void cameraCheckPermission() {


        Dexter.withContext(this) .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA) .withListener(new MultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                camera();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                showRotationalDialogForPermission();
            }
        }).onSameThread().check();
    }


    //????????? ??????
    private void camera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, CAMERA_REQUEST_CODE)
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            photoFile = createImageFile();
        }catch (IOException e){
            Toast.makeText(this,"?????? ??????????????????.",Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }

        if(photoFile != null){
            Uri photoURI = FileProvider.getUriForFile(this,"com.example.paint_diary.fileprovider",photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
            startActivityForResult(intent,CAMERA_REQUEST_CODE);
        }
    }

    //????????? ?????? ??????
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PaintDiary_JPEG_"+timeStamp+"_";
        File storageDir = new File( Environment.getExternalStorageDirectory().toString() + "/Pictures", "paint");

        if(!storageDir.exists()){
            storageDir.mkdirs();
        }

        File imageFile = File.createTempFile(imageFileName,".jpg",storageDir);
        curPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                //?????????
                case CAMERA_REQUEST_CODE :
                    Bitmap bitmap = null;
                    File file = new File(curPhotoPath);
                    if(Build.VERSION.SDK_INT < 28){
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),Uri.fromFile(file));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }else{
                        ImageDecoder.Source decode = ImageDecoder.createSource(this.getContentResolver(),Uri.fromFile(file));
                        galleryAddPic();
                        try {
                            bitmap = ImageDecoder.decodeBitmap(decode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    cropImage(Uri.fromFile(file)); //???????????? ???????????? ?????? ??????
                    try {
                        savePhoto(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                //?????????
                case GALLERY_REQUEST_CODE :
                    Uri uri = data.getData();

                   cropImage(uri);
                    break;

                //????????? ??????
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE :

                    Log.e("CropImage", "CROP_IMAGE_ACTIVITY_REQUEST_CODE");
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if(resultCode == RESULT_OK){
                        Uri resultUri = result.getUri();
                        System.out.println("crop resultUri : "+resultUri);

                        date_now = new Date(System.currentTimeMillis()); // ??????????????? ????????? Date????????? ????????????
                        // ?????????????????? 14?????? ??????
                        fourteen_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        date = fourteen_format.format(date_now);

                        mHandler.post(new msgUpdate(UserID,user_nickname,room_idx,resultUri.toString(),profile_photo,1,date));
                        uploadImage(resultUri);

                    }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                        Exception error = result.getError();
                        System.out.println("crop error : "+error);
                    }
                    break;

            }
        }
    }

    //????????? ?????? ?????????
    private void cropImage(Uri uri){

        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE) //????????? ???????????? ?????????
                .start(this);

    }

    //?????? ????????? ???????????? ????????? ????????? ??????
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(curPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    //???????????? ??????
    private void savePhoto(Bitmap bitmap) throws FileNotFoundException {
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/"; //???????????? ??????
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "${timestamp}.jpeg";
        File folder = new File(folderPath);
        if(!folder.exists()){ //?????? ?????? ????????? ????????? ??????????????? ??????
            folder.mkdirs(); // make Directory ???????????? ?????? ????????? ???????????? ???????????? ??????
        }
        //???????????? ?????? ??????
        FileOutputStream out = new FileOutputStream(folderPath + fileName);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        //Toast.makeText(this,"????????? ?????????????????????.",Toast.LENGTH_SHORT).show()
    }

    private void showRotationalDialogForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("??? ???????????? ????????? ????????? ?????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getPackageName(),null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });

        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    //?????? ????????? ?????????
    private void uploadImage(Uri uri){
        Log.e("uploadImage",uri.toString());

        String[] split_uriPath = uri.toString().split("file://");
        String image = split_uriPath[1]; // ?????? ?????? : /storage/emulated/0/Download/filename.pdf <- ?????? ???????????? ????????? ????????? ????????? ????????? ???????????? ??????

        File file = new File(image);
        RequestBody requestfile = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("chat_image",file.getName(),requestfile);

        IRetrofit api = retrofit.create(IRetrofit.class);

        Call<String> call = api.uploadChatImage(body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String img_name = response.body();
                Log.e("img_name",img_name);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {

                            date_now = new Date(System.currentTimeMillis()); // ??????????????? ????????? Date????????? ????????????
                            // ?????????????????? 14?????? ??????
                            fourteen_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            date = fourteen_format.format(date_now);

                            sendWriter.println(UserID + ">>" +user_nickname +">>" + room_idx + ">>" + img_name + ">>" + profile_photo + ">>" + 1 + ">>" + date);
                            sendWriter.flush();
                            message.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("img_name","??????");
                t.printStackTrace();

            }
        });

    }
    @Override
    public void onItemSelected(View v, int chat_type, String url) {
        //????????? ?????? ?????????
        Log.e("chat_type",chat_type+"");
        Log.e("url",url);
        Intent intent = new Intent(ChatActivity.this,ViewPagerActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);

    }

}