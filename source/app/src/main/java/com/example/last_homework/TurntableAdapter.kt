package com.example.last_homework

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.last_homework.App.Companion.context
import java.util.ArrayList

class TurntableAdapter(val TurntableList: List<Turntable>): RecyclerView.Adapter<TurntableAdapter.ViewHolder>(),Filterable {

    //存放原数据
    private var mSourceList = mutableListOf<Turntable>()
    //存放过滤后的数据
    private var mFilterList = mutableListOf<Turntable>()
    interface ItemListener { fun onItemLongClick(position: Int) }
//    private var itemListener: ItemListener? = null
//    fun setOnItemLongClickListener(itemListener: ItemListener?) {
//        this.itemListener = itemListener
//    }
    init {
        mSourceList = TurntableList as MutableList<Turntable>
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val table_name_: TextView = view.findViewById(R.id.mytable_name)
        val table_creat_:TextView = view.findViewById(R.id.mytable_create)
        val table_status_:TextView = view.findViewById(R.id.mytable_status)
        val table_creator_ :TextView = view.findViewById(R.id.mytable_creator)
        val table_introduce_ :TextView = view.findViewById(R.id.mytable_introduce)
        val table_delete_:Button = view.findViewById(R.id.mytable_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mytable_item,parent,false)
        val viewHolder = ViewHolder(view)
//        viewHolder.itemView.setOnLongClickListener(View.OnLongClickListener {
//            val position: Int = viewHolder.adapterPosition
//            itemListener?.onItemLongClick(position)
//            return@OnLongClickListener true
//        })

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Turntable_ = mFilterList[position]
        holder.table_name_.text = Turntable_.table_name
        holder.table_creat_.setText(holder.table_creat_.text.toString() + Turntable_.table_create.substring(0..9))

//        holder.table_creat_.text=Turntable_.table_create
        if (Turntable_.table_status == 0){
            holder.table_status_.text = "本地"
        }
        else if (Turntable_.table_status == 1){
            holder.table_status_.text = "上传中"
        }
        else if (Turntable_.table_status == 2){
            holder.table_status_.text = "审核中"
        }
        else if (Turntable_.table_status == 3){
            holder.table_status_.text = "上传失败"
        }
        else if (Turntable_.table_status == 4){
            holder.table_status_.text = "上传成功"
        }
        holder.table_creator_.setText(holder.table_creator_.text.toString() + Turntable_.table_creator)
        holder.table_introduce_.setText(holder.table_introduce_.text.toString() +'\n'+ Turntable_.table_introduction)
        holder.itemView.setOnClickListener{//点击控件跳转到转盘详情
            val position = holder.adapterPosition
//            Toast.makeText(holder.itemView.context,position.toString(),Toast.LENGTH_SHORT).show()
            var intent: Intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("table_id",TurntableList[position].table_id)
            intent.putExtra("table_name",TurntableList[position].table_name)
            holder.itemView.context.startActivity(intent)
        }
        holder.table_delete_.setOnClickListener {//删除按钮，删除message和turntable相关数据，并刷新
            var table_name = Turntable_.table_name
            val dbHelper = MyDatabaseHelper(holder.itemView.context, "Turntable.db", 1)
            val db = dbHelper.writableDatabase
            db.delete("message", "table_id = (select message.table_id from message inner join turntable " +
                    "on message.table_id = turntable.table_id where turntable.table_name= ?)",arrayOf(table_name))
            db.delete("turntable","table_name=?", arrayOf(table_name))
            var intent: Intent = Intent(holder.itemView.context, MainActivity::class.java)
            holder.itemView.context.startActivity(intent)//刷新（可进一步优化）
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
                    mSourceList.filter { it.table_name.contains(charString) || it.table_creator.contains(charString)}
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
                mFilterList = filterResults.values as MutableList<Turntable>
                notifyDataSetChanged()
            }
        }
    }
}