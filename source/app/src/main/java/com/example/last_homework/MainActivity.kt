package com.example.last_homework

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.RadioGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import kotlinx.android.synthetic.main.activity_turntable.*
import kotlinx.android.synthetic.main.createdialog.*
import kotlinx.android.synthetic.main.history.*
import kotlinx.android.synthetic.main.history.title_activity
import kotlinx.android.synthetic.main.me.*
import kotlinx.android.synthetic.main.me.recyclerView
import kotlinx.android.synthetic.main.mytable.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


var Mviews = ArrayList<View>()
var search_text=""
var getActivity_Home = null
var result: Activity? = null
private val absenceList = arrayListOf<Absence>()
private val recordList = arrayListOf<Record>()
private val MytableList = arrayListOf<Turntable>()
var texts = arrayOf<String>()
class MainActivity : AppCompatActivity() {
    private var num=0
    private  var mViews = ArrayList<View>()
    private val selectionList = ArrayList<Selection>()
    public var dbHelper:MyDatabaseHelper = MyDatabaseHelper(this, "Turntable.db", 1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.resources.getColor(R.color.pack))
        result = this
        initView() //初始化数据
        init()
        val db = dbHelper.writableDatabase
        for (index in 1..10){
            val values = ContentValues().apply {
                put("user_name", "user$index")
                put("user_sex",index%2)
                put("user_passwd",123456)
            }
            db.insert("user",null,values)
        }
        //对单选按钮进行监听，选中、未选中
        rg_tab.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_home -> viewpager.setCurrentItem(0)
                R.id.rb_launch-> viewpager.setCurrentItem(1)
                R.id.rb_history -> viewpager.setCurrentItem(2)
                R.id.rb_me -> viewpager.setCurrentItem(3)
            }
        })

    }

    fun set()
    {

    }

    private fun init(){
        selectionList.add(Selection("检查更新",R.drawable.versions))
        selectionList.add(Selection("设置",R.drawable.set))
    }

    private fun initView() {
        //初始化控件
        mViews.clear()
        mViews.add(LayoutInflater.from(this).inflate(R.layout.home,null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.mytable,null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.history,null));
        mViews.add(LayoutInflater.from(this).inflate(R.layout.me,null));
        Mviews.clear()
        Mviews = mViews
        viewpager.adapter = MyViewPagerAdapter() //设置一个适配器
        viewpager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            //让viewpager滑动的时候，下面的图标跟着变动
            @RequiresApi(Build.VERSION_CODES.N)
            @SuppressLint("Range")
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {//第一页（待完善）
                        rb_home.setChecked(true)
                        rb_launch.setChecked(false)
                        rb_history.setChecked(false)
                        rb_me.setChecked(false)
                    }
                    1 -> {//（第二页（我的转盘）
                        rb_home.setChecked(false)
                        rb_launch.setChecked(true)
                        rb_history.setChecked(false)
                        rb_me.setChecked(false)
                        init_mytable()
                        val layoutManager = LinearLayoutManager(this@MainActivity)//获取适配器
                        mytable1_listView.layoutManager = layoutManager
                        var adapter = TurntableAdapter(MytableList)
                        adapter.filter.filter("")
                        recyclerView_history.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
                        mytable1_listView.adapter = adapter
                        mytable1_title_activity.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->//列表框颜色
                            if (hasFocus) {
                                mytable1_listView.setBackgroundColor(this@MainActivity.resources.getColor(R.color.white))
                            } else {
                                mytable1_listView.setBackgroundColor(this@MainActivity.resources.getColor(R.color.back))
                            }
                        }
                        mytable1_searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {//搜索
                            // 当点击搜索按钮时触发该方法
                            override fun onQueryTextSubmit(query: String): Boolean {
                                mytable1_searchView.clearFocus()
                                mytable1_listView.requestFocus()
                                var adapter = TurntableAdapter(MytableList)
                                adapter.filter.filter(search_text)
                                mytable1_listView.adapter = adapter
                                return false
                            }

                            // 当搜索内容改变时触发该方法
                            override fun onQueryTextChange(newText: String): Boolean {
                                //do something
                                //当没有输入任何内容的时候清除结果，看实际需求
                                search_text = newText
                                var adapter = TurntableAdapter(MytableList)
                                if (TextUtils.isEmpty(newText)){
                                    adapter.filter.filter("")
                                } else{
                                    adapter.filter.filter(newText)
                                }
                                mytable1_listView.adapter = adapter
                                return false
                            }
                        })
                        SoftKeyBoardListener.setListener(this@MainActivity, object ://软键盘
                            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                            override fun keyBoardShow(height: Int) {}
                            override fun keyBoardHide(height: Int) {
                                mytable1_clearFource()
                            }
                        })
                        mytable1_add.setOnClickListener{//添加转盘按钮
                             CreateTableDialog.Builder(this@MainActivity)
                                .setTitle("新建转盘")
                                .setConfirmText("确认")
                                .setCancelText("取消")
                                .setOnConfirmListener(object : CreateTableDialog.OnConfirmListener {
                                    @SuppressLint("SimpleDateFormat")
                                    @RequiresApi(Build.VERSION_CODES.O)
                                    override fun onClick(dialog: Dialog) {
                                        var db = dbHelper.writableDatabase
                                        val turntable_name = dialog.tv_name.text
                                        val turntable_introduction = dialog.tv_intro.text
                                        val formatter  = SimpleDateFormat   ("yyyy-MM-dd HH:mm:ss");
                                        val curDate =  Date(System.currentTimeMillis())
                                        val values = ContentValues().apply {
                                            put("table_name",turntable_name.toString())
                                            put("table_creator", "user")//创建者
                                            put("table_create", formatter.format(curDate).toString())//创建时间
                                            put("table_introduction", turntable_introduction.toString())//简介
                                            put("table_status", 0)
                                            put("usage_count", 0)
                                        }
                                        db.insert("turntable", null, values)
                                        db.close()
                                        dbHelper.close()
                                        Toast.makeText(getApplicationContext(), "创建成功",
                                            Toast.LENGTH_SHORT).show();
                                        init_mytable()
                                        val layoutManager = LinearLayoutManager(this@MainActivity)
                                        mytable1_listView.layoutManager = layoutManager
                                        var adapter = TurntableAdapter(MytableList)
                                        adapter.filter.filter("")
                                        mytable1_listView.adapter = adapter
                                        dialog.dismiss()
                                    }
                                })
                                .setOnCancelListener(object : CreateTableDialog.OnCancelListener {
                                    override fun onClick(dialog: Dialog) {
                                        dialog.dismiss()
                                    }
                                })
                                .create()
                                .show()

                        }
                    }
                    2 -> {//转盘历史
                        init_history()
                        val layoutManager1 = LinearLayoutManager(this@MainActivity)//适配器
                        recyclerView_history.layoutManager = layoutManager1
                        var adapter = RecordAdapter(recordList)
                        adapter.filter.filter("")
                        recyclerView_history.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
                        recyclerView_history.adapter = adapter
                        rb_home.setChecked(false)
                        rb_launch.setChecked(false)
                        rb_history.setChecked(true)
                        rb_me.setChecked(false)
                        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH)
                        searchView.isSubmitButtonEnabled = true
                        searchView.isFocusable = true
                        title_activity.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->//列表框颜色
                            if (hasFocus) {
                                recyclerView_history.setBackgroundColor(this@MainActivity.resources.getColor(R.color.white))
                            } else {
                                recyclerView_history.setBackgroundColor(this@MainActivity.resources.getColor(R.color.back))
                            }
                        }
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {//搜索
                            // 当点击搜索按钮时触发该方法
                            override fun onQueryTextSubmit(query: String): Boolean {
                                searchView.clearFocus()
                                recyclerView_history.requestFocus()
                                var adapter = RecordAdapter(recordList)
                                adapter.filter.filter(search_text)
                                recyclerView_history.adapter = adapter
                                return false
                            }

                            // 当搜索内容改变时触发该方法
                            override fun onQueryTextChange(newText: String): Boolean {
                                //do something
                                //当没有输入任何内容的时候清除结果，看实际需求
                                search_text = newText
                                var adapter = RecordAdapter(recordList)
                                if (TextUtils.isEmpty(newText)){
                                    adapter.filter.filter("")
                                } else{
                                    adapter.filter.filter(newText)
                                }
                                recyclerView_history.adapter = adapter
                                return false
                            }
                        })
                        SoftKeyBoardListener.setListener(this@MainActivity, object ://软键盘
                            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                            override fun keyBoardShow(height: Int) {}
                            override fun keyBoardHide(height: Int) {
                                clearFource()
                            }
                        })
                    }
                    3 -> {//用户设置
                        num += 1
                        rb_home.setChecked(false)
                        rb_launch.setChecked(false)
                        rb_history.setChecked(false)
                        rb_me.setChecked(true)
                        val layoutManager = LinearLayoutManager(this@MainActivity)
                        recyclerView.layoutManager = layoutManager
                        val adapter = SelectionAdapter(selectionList)
//                        if(num==1){
//                            recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
//                        }
//                        recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
                        recyclerView.adapter = adapter
                        person_image.setOnClickListener {
                            var intent = Intent(this@MainActivity,peron_information::class.java)
                            startActivity(intent)
                        }
                        login_text.setOnClickListener {
                            var intent = Intent(this@MainActivity,peron_information::class.java)
                            startActivity(intent)
                        }
                        account_text.setOnClickListener {
                            var intent = Intent(this@MainActivity,peron_information::class.java)
                            startActivity(intent)
                        }
                        account_direction_right.setOnClickListener {
                            var intent = Intent(this@MainActivity,peron_information::class.java)
                            startActivity(intent)
                        }
                        account_swap.setOnClickListener {
                            var intent = Intent(this@MainActivity,peron_information::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })



    }


    //ViewPager适配器
    private class MyViewPagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return Mviews.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(Mviews.get(position))
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(Mviews.get(position))
            return Mviews.get(position)
        }
    }

    private fun clearFource() {
        if (searchView != null) {
            searchView.clearFocus()
        }
        title_activity.setFocusable(true)
        title_activity.setFocusableInTouchMode(true)
        title_activity.requestFocus()
    }
    private fun mytable1_clearFource() {
        if (mytable1_searchView != null) {
            mytable1_searchView.clearFocus()
        }
        mytable1_title_activity.setFocusable(true)
        mytable1_title_activity.setFocusableInTouchMode(true)
        mytable1_title_activity.requestFocus()
    }
    private fun init_mytable(){//获取转盘
        MytableList.clear()
        val dbHelper = MyDatabaseHelper(this, "Turntable.db", 1)
        val db = dbHelper.writableDatabase
        var cursor = db.rawQuery("select * from turntable",null)
        if (cursor != null) {
            if(cursor.moveToFirst())
            {
                do {
                    val table_id = cursor.getInt(0)
                    val table_name = cursor.getString(1)
                    val table_creator = cursor.getString(2)
                    val table_create = cursor.getString(3)
                    val table_introduction = cursor.getString(4)
                    val table_status = cursor.getInt(5)
                    MytableList.add(Turntable(table_id,table_name,table_creator,table_create,table_introduction,table_status))
                }while (cursor.moveToNext())
            }
        }
        db.close()
        dbHelper.close()
     }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun init_history() {//获取转盘历史
        absenceList.clear()
        recordList.clear()
        val dbHelper = MyDatabaseHelper(this, "Turntable.db", 1)
        val db = dbHelper.writableDatabase
        val table_result = mutableMapOf<String,ArrayList<ArrayList<String>>>()
        var cursor = db.rawQuery("select record_id,record_time,record_uid,record_result,record.table_id,table_name from record inner join turntable on record.table_id = turntable.table_id where record_uid=1;",null)

        if (cursor != null) {
            if(cursor.moveToFirst())
            {
                do {
                    val single_result = ArrayList<String>()
                    val record_id = cursor.getInt(0)
                    val record_time = cursor.getString(1)
                    val record_uid = cursor.getString(2)
                    val record_result = cursor.getString(3)
                    val table_id = cursor.getInt(4)
                    val table_name = cursor.getString(5)
                    val result = table_result.getOrDefault(table_name,ArrayList<ArrayList<String>>())
                    single_result.add(record_result)
                    single_result.add(record_time)
                    result.add(single_result)
                    table_result.put(table_name,result)
                }while (cursor.moveToNext())
            }
        }
        for ((key,value) in table_result){//获取记录
            var datas = ArrayList<String>()
            var times = ArrayList<String>()
            for (arr in value)
            {
               datas.add(arr.get(0))
                times.add(arr.get(1))
            }
            recordList.add(Record(key,datas,times))
        }
        db.close()
        dbHelper.close()
    }

}