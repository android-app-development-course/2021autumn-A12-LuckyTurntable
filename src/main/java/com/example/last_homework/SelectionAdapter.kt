package com.example.last_homework

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.lang.IllegalArgumentException

class SelectionAdapter(val selectionList: List<Selection>): RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) :RecyclerView.ViewHolder(view){
        val selectionName: TextView = view.findViewById(R.id.selectionName)
        val selectionImage: ImageView = view.findViewById(R.id.selectionImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selection_item,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            Log.d("app11234","position:"+position.toString())
            if(position==0){
                Toast.makeText(parent.context ,"最新的版本是: 1.0.0\n当前已是最新版本",Toast.LENGTH_SHORT).show()
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selection = selectionList[position]
        holder.selectionImage.setImageResource(selection.imageId)
        holder.selectionName.text = selection.name
    }

    override fun getItemCount() = selectionList.size
}