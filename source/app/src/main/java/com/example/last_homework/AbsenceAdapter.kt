package com.example.last_homework

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class AbsenceAdapter(val absenceList: List<Absence>): RecyclerView.Adapter<AbsenceAdapter.ViewHolder>(),Filterable {

    //存放原数据
    private var mSourceList = mutableListOf<Absence>()
    //存放过滤后的数据
    private var mFilterList = mutableListOf<Absence>()

    init {
        mSourceList = absenceList as MutableList<Absence>
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val clasname_: TextView = view.findViewById(R.id.clasName)
        val absence_people_:TextView = view.findViewById(R.id.absence_people)
        val absence_time_:TextView = view.findViewById(R.id.history_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.absence,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val absence_ = mFilterList[position]
        holder.clasname_.text = absence_.classname
        holder.absence_people_.text = absence_.absence_people
        holder.absence_time_.text = absence_.history_time
    }

    override fun getItemCount() = mFilterList.size


    ///////////////////////////////////////////////////////////////////////

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                //存放已过滤的数据
                var theFilterList = if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mSourceList
                } else {
                    mSourceList.filter { it.classname.contains(charString) || it.absence_people.contains(charString)}
                }
                val filterResults = FilterResults()
                filterResults.values = theFilterList
                return filterResults
            }
            //把过滤后的值返回出来并进行更新
            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                mFilterList = filterResults.values as MutableList<Absence>
                notifyDataSetChanged()
            }
        }
    }
}