package com.example.last_homework

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

//    private val createClass = "create message class("+
//            "cid integer primary key ,"+
//            "cname text);"
//    private val createStudent = "create message student("+
//            "cid integer ,"+
//            "sname text primary key);"
//    private val createClassRecord = "create message classRecord("+
//            "identify integer primary key, "+
//            "cid integer);"
    private val createUser ="create table user("+
            "user_id integer primary key, "+
            "user_name varchar(100) unique,"+
            "user_sex integer(1),"+
            "user_phone char(11),"+
            "user_email text,"+
            "user_passwd varchar(32),"+
            "user_introduction text);"
    private val createTable ="create table turntable("+
            "table_id integer primary key, "+
            "table_name varchar(100),"+
            "table_creator varchar(100),"+
            "table_create text,"+
            "table_introduction text,"+
            "table_status integer(1),"+
            "return_reason text,"+
            "usage_count integer);"
    private val createRecord ="create table record("+
            "record_id integer primary key, "+
            "record_time text,"+
            "record_uid integer,"+
            "record_result varchar(32),"+
            "table_id integer);"
    private val createMessage ="create table message("+
            "message_id integer primary key, "+
            "message_data varchar(32),"+
            "table_id integer);"
    private val createToken ="create table token("+
            "token_id integer primary key, "+
            "table_id integer, "+
            "password char(4));"
    override fun onCreate(p0: SQLiteDatabase?) {
//        p0?.execSQL(createClass)
//        p0?.execSQL(createStudent)
        p0?.execSQL(createRecord)
        p0?.execSQL(createUser)
        p0?.execSQL(createTable)
        p0?.execSQL(createToken)
        p0?.execSQL(createMessage)
//        Toast.makeText(context,"Create message Book successful!",Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("drop table if exists user")
        p0?.execSQL("drop table if exists message")
        p0?.execSQL("drop table if exists record")
        p0?.execSQL("drop table if exists turntable")
        p0?.execSQL("drop table if exists token")
        onCreate(p0)
    }
}