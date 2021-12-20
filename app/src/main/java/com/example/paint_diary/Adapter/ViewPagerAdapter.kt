package com.example.paint_diary.Adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.paint_diary.R

class ViewPagerAdapter(imageList : ArrayList<String>, activity: Activity) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    var item = imageList
    var context : Context? = activity

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }


    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var chat_image : ImageView = itemView.findViewById(R.id.chat_image)
        var viewPager_close : ImageView = itemView.findViewById(R.id.viewPager_close)
        var viewPager_download : ImageView = itemView.findViewById(R.id.viewPager_download)


        fun bind(item: String){

            Glide.with(context!!).load(item).into(chat_image)


            //download 이미지 클릭 시 이미지 파일 다운받기
            viewPager_download.setOnClickListener {
                Log.e("download","download")
                var position = bindingAdapterPosition
                itemClickListner.onClick(it, position)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        //context = parent.context
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_image_viewpager_item, parent, false)
        Log.e("onCreateViewHolder","onCreateViewHolder")
        return ViewHolder(v)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item.get(position))

        //close 이미지 클릭 시 액티비티 종료
        holder.viewPager_close.setOnClickListener {
            //recyclerview에서 액티비티 종료하는 방법
            (context as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }
}