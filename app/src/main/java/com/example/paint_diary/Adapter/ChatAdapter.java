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
import com.example.paint_diary.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private static final int CHAT_RIGHT = 0;
    private static final int CHAT_LEFT = 1;
    private ArrayList<Chat> mData = null ;
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

        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = mData.get(position) ;

        Log.e("onBindViewHolder","onBindViewHolder");

        if(holder instanceof ChatAdapter.ViewHolder_left){
            if(chat.getProfile_photo().equals("") || chat.getProfile_photo().isEmpty() || chat.getProfile_photo().equals("없음")){
                Glide.with(context).load(R.drawable.basic_profile).into(((ViewHolder_left) holder).chat_profile);
            }else{
                Glide.with(context).load(chat.getProfile_photo()).into(((ViewHolder_left) holder).chat_profile); //http://3.36.52.195/profile/
            }
            ((ViewHolder_left) holder).chat_nickname.setText(chat.getNickname());
            ((ViewHolder_left) holder).chatting_content.setText(chat.getContent());
            ((ViewHolder_left) holder).chatting_datetime.setText(chat.getDateTime());

        }else if(holder instanceof ChatAdapter.ViewHolder_right){
            ((ViewHolder_right) holder).chatting_content.setText(chat.getContent());
            ((ViewHolder_right) holder).chatting_datetime.setText(chat.getDateTime());

        }
    }

    @Override
    public int getItemCount() {
        Log.e("count",mData.size()+"");
        Log.e("getItemCount","getItemCount");
        return mData.size() ;
    }

    public ChatAdapter(ArrayList<Chat> list, Context context) {
        Log.e("ChatAdapter","ChatAdapter");
        mData = list ;
        this.context = context;

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        user_idx = sharedPreferences.getString("user_idx", "0");
    }


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

    public class ViewHolder_right extends RecyclerView.ViewHolder {
        TextView chatting_content,chatting_datetime ;
        public ViewHolder_right(View itemView) {
            super(itemView);
            chatting_content = itemView.findViewById(R.id.chatting_content) ;
            chatting_datetime = itemView.findViewById(R.id.chatting_datetime) ;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.e("getItemViewType","getItemViewType");
        Log.e("user_idx",""+user_idx);
        Log.e("USERID",mData.get(position).getUser_idx());
        if(user_idx.equals(mData.get(position).getUser_idx())){
            return CHAT_RIGHT;
        }else{
            return CHAT_LEFT;
        }

    }
}
