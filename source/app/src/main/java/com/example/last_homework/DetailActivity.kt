package com.example.last_homework

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.insertdialog.*

public var tid:Int = 0
public var tname:String = ""
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mytable_messages.movementMethod = ScrollingMovementMethod.getInstance()
        val dbHelper = MyDatabaseHelper(this, "Turntable.db", 1)
        val db = dbHelper.writableDatabase
        var bundle:Bundle?=intent.extras
        tid = bundle!!.getInt("table_id")
        tname = bundle!!.getString("table_name").toString()
        check()
        importMessage.setOnClickListener{//点击导入数据按钮
            InsertTableDialog.Builder(this)
                .setTitle("手动添加数据")
                .setConfirmText("确认")
                .setCancelText("取消")
                .setOnConfirmListener(object : InsertTableDialog.OnConfirmListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onClick(dialog: Dialog) {
                        var db = dbHelper.writableDatabase
                        val message_data = dialog.insert_data.text
                        val values = ContentValues().apply {
                            put("message_data",message_data.toString())
                            put("table_id",tid)
                        }
                        db.insert("message", null, values)//插入单条记录到message
                        db.close()
                        dbHelper.close()
                        Toast.makeText(getApplicationContext(), "添加成功",
                            Toast.LENGTH_SHORT).show();
                        check()
                        dialog.dismiss()
                    }
                })
                .create()
                .show()
        }
        deleteFirst.setOnClickListener{//删除第一条记录
            db.delete("message","message_id=(select message_id from message order by message_id)",null)
            check()
        }
        deleteLast.setOnClickListener{//删除最后一条记录
            db.delete("message","message_id=(select message_id from message order by message_id desc)",null)
            check()
        }
        play.setOnClickListener{//转盘
            var intent: Intent = Intent(this, TurntableActivity::class.java)
            intent.putExtra("table_id",tid)
            intent.putExtra("table_name",tname)
            if (tid>0)
            {
                this.startActivity(intent)//跳转
            }
            else
            {
                android.app.AlertDialog.Builder(this).apply {
                    setTitle("警告")
                    setMessage("未选择转盘!")
                    setCancelable(false)
                    setPositiveButton("确认") { dialog, which ->
                    }
                    show()
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun check()//打印详情
    {
        val dbHelper = MyDatabaseHelper(this, "Turntable.db", 1)
        val db = dbHelper.writableDatabase
        var bundle:Bundle?=intent.extras
        var tid: Int = bundle!!.getInt("table_id")
        mytable_messages.text = ""
        detail_name.text =""
        detail_create.text="创建时间："
        detail_creator.text="创建者："
        var cursor = db.rawQuery("select table_name,table_creator,table_create from turntable where table_id = ?",arrayOf(tid.toString()))
        if (cursor != null) {
            if(cursor.moveToFirst())
            {
                do {//转盘简介、创建者、创建时间
                    val table_name = cursor.getString(0)
                    val table_creator = cursor.getString(1)
                    val table_create = cursor.getString(2)
                    detail_name.text = table_name.toString()
                    detail_creator.text = detail_creator.text.toString()+table_creator.toString()
                    detail_create.text = detail_create.text.toString()+table_create.toString()
                }while (cursor.moveToNext())
            }
        }
        cursor = db.rawQuery("select message_data from message where table_id = ?",arrayOf(tid.toString()))
        if (cursor != null) {
            if(cursor.moveToFirst())
            {
                do {//转盘数据项
                    val message_data = cursor.getString(0)
                    mytable_messages.text = mytable_messages.text.toString()+'\n'+message_data.toString()
                }while (cursor.moveToNext())
            }
        }
        db.close()
        dbHelper.close()
    }
}