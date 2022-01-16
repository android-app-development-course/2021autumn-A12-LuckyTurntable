package com.example.last_homework

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import kotlinx.android.synthetic.main.activity_turntable.*
import java.text.SimpleDateFormat
import java.util.*

class TurntableActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turntable)
        var bundle:Bundle?=intent.extras
        var tid = bundle!!.getInt("table_id")
        var tname = bundle!!.getString("table_name")
        turntable_record.movementMethod = ScrollingMovementMethod.getInstance();
        tv_title.text = tname.toString()
        turntable_record.text = "记录如下："+'\n'
        val dbHelper = MyDatabaseHelper(this, "Turntable.db", 1)
        var db = dbHelper.writableDatabase
        turntable_view.setValueListener {//转动转盘
            if(it != "")
            {
                AlertDialog.Builder(this).apply {
                    setTitle("转盘结果")
                    setMessage("本次抽中："+it)
                    setCancelable(false)
                    setPositiveButton("确认") { dialog, which ->//插入数据到record表
                        val formatter  = SimpleDateFormat   ("yyyy-MM-dd HH:mm:ss");
                        val curDate =  Date(System.currentTimeMillis())
                        val values = ContentValues().apply {
                            put("record_time",formatter.format(curDate).toString())
                            put("record_result",it)
                            put("table_id",tid)
                            put("record_uid",1)
                        }
                        db.insert("record", null, values)
                    }
                        create()
                        show()
                }
                turntable_record.text = turntable_record.text.toString()+'\t'+'\t'+it
            }
        }
        start_button.setOnClickListener {
            turntable_view.start()
        }
    }
}