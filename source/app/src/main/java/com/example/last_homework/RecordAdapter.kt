package com.example.last_homework

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordAdapter(val RecordList: List<Record>): RecyclerView.Adapter<RecordAdapter.ViewHolder>(),Filterable {
    //存放原数据
    private var mSourceList = mutableListOf<Record>()
    //存放过滤后的数据
    private var mFilterList = mutableListOf<Record>()
    init {
        mSourceList = RecordList as MutableList<Record>
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){//获取控件
        val Record_tableName_: TextView = view.findViewById(R.id.record_tableName)
        val Record_data_:TextView = view.findViewById(R.id.record_data)
        val Record_delete_:Button = view.findViewById(R.id.record_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Record_ = mFilterList[position]
        holder.Record_delete_.setOnClickListener {//点击删除按钮，删除record数据表并刷新
            var table_name = Record_.table_name
            val dbHelper = MyDatabaseHelper(holder.itemView.context, "Turntable.db", 1)
            val db = dbHelper.writableDatabase
            db.delete("record","table_id = (select record.table_id from record inner join turntable " +
                    "on record.table_id = turntable.table_id where turntable.table_name=?)", arrayOf(table_name))
            var intent: Intent = Intent(holder.itemView.context, MainActivity::class.java)
            holder.itemView.context.startActivity(intent)//刷新（可进一步优化）
        }
        holder.Record_data_.text = String.format("%-16s","数据")+"时间"
        for(index in 0..Record_.record_result.size-1)
        {
            val item = Record_.record_result.get(index)
            holder.Record_tableName_.text = Record_.table_name //转盘名
            //TextView打印出转盘记录和世界
            holder.Record_data_.text = holder.Record_data_.text.toString()+'\n'+String.format("%-16s",item) + Record_.record_time.get(index)
        }
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
                    mSourceList.filter { it.table_name.contains(charString)}
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
                mFilterList = filterResults.values as MutableList<Record>
                notifyDataSetChanged()
            }
        }
    }
}