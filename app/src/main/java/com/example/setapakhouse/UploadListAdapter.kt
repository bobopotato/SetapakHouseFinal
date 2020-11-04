package com.example.setapakhouse

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_upload_profile_picture.*
import java.util.ArrayList

class UploadListAdapter(val imageList : ArrayList<Uri>, val resolverList : ArrayList<ContentResolver>) : RecyclerView.Adapter<UploadListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadListAdapter.ViewHolder {
        //val view : View = LayoutInflater.from(parent.context).inflate(R.layout.uploaded_image_list,parent,false)
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.uploaded_image_list,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UploadListAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: UploadListAdapter.ViewHolder,position: Int, payloads: MutableList<Any>) {

        var bitmap = MediaStore.Images.Media.getBitmap(resolverList[position], imageList[position])
        holder.uploadedImage.setImageBitmap(bitmap)

        holder.imageName.text = (position+1).toString()

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
