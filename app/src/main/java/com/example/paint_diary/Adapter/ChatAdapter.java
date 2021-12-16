package com.example.paint_diary.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.paint_diary.Data.Chat;
import com.example.paint_diary.Data.Chat2;
import com.example.paint_diary.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private static final int CHAT_RIGHT = 0;
    private static final int CHAT_LEFT = 1;
    private static final int CHAT_IMAGE_RIGHT = 2;
    private static final int CHAT_IMAGE_LEFT = 3;
    private ArrayList<Chat2> mData = null ;
    private String user_idx;
    Context context;

    private static final String SHARED_PREF_NAME = "user";

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("onCreateViewHolder","onCreateViewHolder");
        //context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;


        if(viewType == CHAT_LEFT){
            View view = inflater.inflate(R.layout.chat_left_item, parent, false) ;

            return new ChatAdapter.ViewHolder_left(view) ;

        }else if(viewType == CHAT_RIGHT){
            View view = inflater.inflate(R.layout.chat_right_item, parent, false) ;
            return new ChatAdapter.ViewHolder_right(view) ;

        }else if(viewType == CHAT_IMAGE_LEFT){
            View view = inflater.inflate(R.layout.chat_image_left_item, parent, false) ;
            return new ChatAdapter.ViewHolder_image_left(view) ;

        }else if(viewType == CHAT_IMAGE_RIGHT){
            View view = inflater.inflate(R.layout.chat_image_right_item, parent, false) ;
            return new ChatAdapter.ViewHolder_image_right(view) ;

        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat2 chat = mData.get(position) ;

        Log.e("onBindViewHolder","onBindViewHolder");

        if(holder instanceof ChatAdapter.ViewHolder_left){ // 일반채팅 - 상대방

            if(chat.getChat_profile_photo() == null || chat.getChat_profile_photo().equals("없음") || chat.getChat_profile_photo().equals("http://3.36.52.195/profile/")){
                Glide.with(context).load(R.drawable.basic_profile).into(((ViewHolder_left) holder).chat_profile);
            }else{
                Glide.with(context).load(chat.getChat_profile_photo()).into(((ViewHolder_left) holder).chat_profile);
            }
            ((ViewHolder_left) holder).chat_nickname.setText(chat.getChat_nickname());
            ((ViewHolder_left) holder).chatting_content.setText(chat.getChat_content());
            ((ViewHolder_left) holder).chatting_datetime.setText(chat.getChat_dateTime());

        }else if(holder instanceof ChatAdapter.ViewHolder_right){ //일반채팅 - 나

            ((ViewHolder_right) holder).chatting_content.setText(chat.getChat_content());
            ((ViewHolder_right) holder).chatting_datetime.setText(chat.getChat_dateTime());

        }else if(holder instanceof  ChatAdapter.ViewHolder_image_left){ //이미지채팅 - 상대방

            if(chat.getChat_profile_photo() == null || chat.getChat_profile_photo().equals("없음") || chat.getChat_profile_photo().equals("http://3.36.52.195/profile/")){
                Glide.with(context).load(R.drawable.basic_profile).into(((ViewHolder_image_left) holder).chat_profile);
            }else{
                Glide.with(context).load(chat.getChat_profile_photo()).into(((ViewHolder_image_left) holder).chat_profile);
            }
            ((ViewHolder_image_left) holder).chat_nickname.setText(chat.getChat_nickname());
            Glide.with(context).load(chat.getChat_content()).into(((ViewHolder_image_left) holder).chatting_content);
            ((ViewHolder_image_left) holder).chatting_datetime.setText(chat.getChat_dateTime());

        }else if(holder instanceof  ChatAdapter.ViewHolder_image_right){ //이미지채팅 - 나

            Glide.with(context).load(chat.getChat_content()).into(((ViewHolder_image_right) holder).chatting_content);
            ((ViewHolder_image_right) holder).chatting_datetime.setText(chat.getChat_dateTime());

        }
    }

    @Override
    public int getItemCount() {
        Log.e("count",mData.size()+"");
        Log.e("getItemCount","getItemCount");
        return mData.size() ;
    }

    public ChatAdapter(ArrayList<Chat2> list, Context context) {
        Log.e("ChatAdapter","ChatAdapter");
        mData = list ;
        this.context = context;

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        user_idx = sharedPreferences.getString("user_idx", "0");
    }


    //일반채팅 - 상대방
    public class ViewHolder_left extends RecyclerView.ViewHolder {
        ImageView chat_profile;
        TextView chat_nickname, chatting_content, chatting_datetime;
        public ViewHolder_left(View itemView) {
            super(itemView);
            chat_nickname = itemView.findViewById(R.id.chat_nickname) ;
            chatting_content = itemView.findViewById(R.id.chatting_content) ;
            chatting_datetime = itemView.findViewById(R.id.chatting_datetime) ;
            chat_profile = itemView.findViewById(R.id.chat_profile) ;
        }
    }

    //일반채팅 - 나
    public class ViewHolder_right extends RecyclerView.ViewHolder {
        TextView chatting_content,chatting_datetime ;
        public ViewHolder_right(View itemView) {
            super(itemView);
            chatting_content = itemView.findViewById(R.id.chatting_content) ;
            chatting_datetime = itemView.findViewById(R.id.chatting_datetime) ;
        }
    }

    //이미지채팅 - 상대방
    public class ViewHolder_image_left extends RecyclerView.ViewHolder {
        TextView chatting_datetime, chat_nickname ;
        ImageView chatting_content,chat_profile;
        public ViewHolder_image_left(View itemView) {
            super(itemView);
            chatting_content = itemView.findViewById(R.id.chatting_content) ;
            chatting_datetime = itemView.findViewById(R.id.chatting_datetime) ;
            chat_nickname = itemView.findViewById(R.id.chat_nickname) ;
            chat_profile = itemView.findViewById(R.id.chat_profile) ;
        }
    }

    //이미지채팅 - 나
    public class ViewHolder_image_right extends RecyclerView.ViewHolder {
        TextView chatting_datetime ;
        ImageView chatting_content;
        public ViewHolder_image_right(View itemView) {
            super(itemView);
            chatting_content = itemView.findViewById(R.id.chatting_content) ;
            chatting_datetime = itemView.findViewById(R.id.chatting_datetime) ;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.e("getItemViewType","getItemViewType");
        Log.e("user_idx",""+user_idx);
        Log.e("USERID",mData.get(position).getChat_user());
        if(mData.get(position).getChat_type() == 0){ // 텍스트 채팅

            if(user_idx.equals(mData.get(position).getChat_user())){
                return CHAT_RIGHT;
            }else{
                return CHAT_LEFT;
            }
        }else{ // 이미지 채팅

            if(user_idx.equals(mData.get(position).getChat_user())){
                return CHAT_IMAGE_RIGHT;
            }else{
                return CHAT_IMAGE_LEFT;
            }
        }

    }
}
