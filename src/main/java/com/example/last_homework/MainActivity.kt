package com.example.last_homework

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.RadioGroup
import android.widget.SearchView
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import kotlinx.android.synthetic.main.activity_turntable.*
import kotlinx.android.synthetic.main.history.*
import kotlinx.android.synthetic.main.me.*
import kotlinx.android.synthetic.main.me.recyclerView


var Mviews = ArrayList<View>()
var search_text=""
var getActivity_Home = null
var result: Activity? = null
private val absenceList = arrayListOf<Absence>()
var texts = arrayOf<String>()
class MainActivity : AppCompatActivity() {
    private var num=0
    private  var mViews = ArrayList<View>()
    private val selectionList = ArrayList<Selection>()
    public var dbHelper:MyDatabaseHelper = MyDatabaseHelper(this, "Turntable.db", 2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.resources.getColor(R.color.pack))
        result = this
        initView() //初始化数据
        init()
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
        mViews.add(LayoutInflater.from(this).inflate(R.layout.activity_turntable,null));
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
            @SuppressLint("Range")
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        rb_home.setChecked(true)
                        rb_launch.setChecked(false)
                        rb_history.setChecked(false)
                        rb_me.setChecked(false)
                    }
                    1 -> {
                        rb_home.setChecked(false)
                        rb_launch.setChecked(true)
                        rb_history.setChecked(false)
                        rb_me.setChecked(false)
                        val db = dbHelper.writableDatabase
                        for (index in 1..10){
                            val values = ContentValues().apply {
                                put("message_data",index.toString())
                                put("table_id",1)
                            }
                            db.insert("message",null,values)
                        }
                        start_button.setOnClickListener {
                            turntable_view.start()
                        }
                    }
                    2 -> {
                        rb_home.setChecked(false)
                        rb_launch.setChecked(false)
                        rb_history.setChecked(true)
                        rb_me.setChecked(false)
                        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH)
                        searchView.isSubmitButtonEnabled = true
                        searchView.isFocusable = true
                        title_activity.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                            if (hasFocus) {
                                recyclerView_history.setBackgroundColor(this@MainActivity.resources.getColor(R.color.white))
                            } else {
                                recyclerView_history.setBackgroundColor(this@MainActivity.resources.getColor(R.color.back))
                            }
                        }
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            // 当点击搜索按钮时触发该方法
                            override fun onQueryTextSubmit(query: String): Boolean {
                                searchView.clearFocus()
                                recyclerView_history.requestFocus()
                                var adapter = AbsenceAdapter(absenceList)
                                adapter.filter.filter(search_text)
                                recyclerView_history.adapter = adapter
                                return false
                            }

                            // 当搜索内容改变时触发该方法
                            override fun onQueryTextChange(newText: String): Boolean {
                                //do something
                                //当没有输入任何内容的时候清除结果，看实际需求
                                search_text = newText
                                var adapter = AbsenceAdapter(absenceList)
                                if (TextUtils.isEmpty(newText)){
                                    adapter.filter.filter("")
                                } else{
                                    adapter.filter.filter(newText)
                                }
                                recyclerView_history.adapter = adapter
                                return false
                            }
                        })
                        SoftKeyBoardListener.setListener(this@MainActivity, object :
                            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                            override fun keyBoardShow(height: Int) {}
                            override fun keyBoardHide(height: Int) {
                                clearFource()
                            }
                        })
                        init_history()
                        val layoutManager = LinearLayoutManager(this@MainActivity)
                        recyclerView_history.layoutManager = layoutManager
                        val adapter = AbsenceAdapter(absenceList)
                        adapter.filter.filter("")
//                        recyclerView_history.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
                        recyclerView_history.adapter = adapter
                    }
                    3 -> {
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

    private fun init_history(){
        absenceList.clear()
        val dbHelper = MyDatabaseHelper(this, "last.db", 1)
        val db = dbHelper.writableDatabase
        db.close()
        dbHelper.close()
        absenceList.add(Absence("  移动智能应用开发周五上午","  20192131001 吗不                20192131004模型下\n  20192131005爱上打             20192131006爱上打\n  20192131007打算打","2021.11.24  "))
        absenceList.add(Absence("  移动智能应用开发周五上午","  20192131009 吗不                20192131002礼拜\n  20192131003杜甫                 20192131004模型下\n  20192131005爱上打             20192131006爱上打\n  20192131007打算打","2021.12.01  "))
    }

}