package com.example.setapakhouse.Adapter

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.setapakhouse.R
import java.util.ArrayList

class UploadListAdapter(val imageList : ArrayList<Uri>, val resolverList : ArrayList<ContentResolver>) : RecyclerView.Adapter<UploadListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //val view : View = LayoutInflater.from(parent.context).inflate(R.layout.uploaded_image_list,parent,false)
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.uploaded_image_list,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        var bitmap = MediaStore.Images.Media.getBitmap(resolverList[position], imageList[position])
        holder.uploadedImage.setImageBitmap(bitmap)

        holder.imageName.text = "image" + (position+1).toString() + ".jpeg"

        //super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val uploadedImage : ImageView = itemView.findViewById(R.id.uploadedImage)
        val imageName : TextView = itemView.findViewById(R.id.imageName)
        
    }

}
