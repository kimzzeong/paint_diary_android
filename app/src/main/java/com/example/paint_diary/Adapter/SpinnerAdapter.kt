package com.example.paint_diary.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.paint_diary.Data.User
import com.example.paint_diary.R

class SpinnerAdapter(context: Context, resource: Int, users: List<User?>) :
    ArrayAdapter<User?>(context, resource, users) {
    var layoutInflater: LayoutInflater
    var id: String? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = layoutInflater.inflate(R.layout.custom_spinner_adapter, null, true)
        val user = getItem(position)
        val textView = rowView.findViewById<View>(R.id.nameTextView) as TextView
        val imageView = rowView.findViewById<View>(R.id.imageIcon) as ImageView
        textView.text = user!!.name
        imageView.setImageResource(user.image)
        return rowView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) convertView =
            layoutInflater.inflate(R.layout.custom_spinner_adapter, null, true)
        val user = getItem(position)
        val textView = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
        val imageView = convertView.findViewById<View>(R.id.imageIcon) as ImageView
        textView.text = user!!.name
        imageView.setImageResource(user.image)
        id = user.name
        return convertView
    }

    init {
        layoutInflater = LayoutInflater.from(context)
    }
}