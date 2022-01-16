package com.example.last_homework

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.animation.ValueAnimator
import androidx.core.animation.doOnEnd

@SuppressLint("SoonBlockedPrivateApi")
class TurntableView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 16.toSp() }
    private val arcRectF = RectF()
    private val textRectF = RectF()
    private val textPath = Path()
    private val ringWidth = 20.toDp()
    private val arcColors = arrayOf(0xfff481eb.toInt(), 0xffffdeb1.toInt(),0xff00ced1.toInt(),0xffffa500.toInt())
    private var center = 0f
    private var startAngle = 0f
    private var valueListener: ((value: String) -> Unit)? = null
    private var txts = arrayOf<String>()
    private var  count = txts.size
    private var sweepAngle = 360f / count
    init {
        var dbHelper: MyDatabaseHelper? = result?.let { MyDatabaseHelper(context, "Turntable.db", 1) }
        val db = dbHelper?.writableDatabase
        //获取message数据
        var cursor = db?.rawQuery("select message_data from message where table_id=?", arrayOf(tid.toString()))
        if (cursor != null) {
            if(cursor.moveToFirst())
            {
                do {
                    val data = cursor.getString(0)
                    txts = txts.plus(data)
                }while (cursor.moveToNext())
            }
        }
        count = txts.size
        sweepAngle = 360f / count
        ValueAnimator::class.java.getDeclaredField("sDurationScale").run {
            isAccessible = true
            if (getFloat(null) != 1f) {
                setFloat(null, 1f)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {//计算每一条数据的角度
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val size = Math.min(w, h)
        center = size / 2f
        arcRectF.set(ringWidth, ringWidth, size - ringWidth, size - ringWidth)
        textRectF.set(ringWidth * 3, ringWidth * 3, size - ringWidth * 3, size - ringWidth * 3)
        setMeasuredDimension(size, size)
    }

    @SuppressLint("Range")
    override fun onDraw(canvas: Canvas) {//绘制转盘颜色
        paint.color = 0xffff4321.toInt()
        canvas.drawCircle(center, center, center, paint)
        startAngle = 0f
        repeat(count) {
            paint.color = arcColors[it % 4]
            canvas.drawArc(arcRectF, startAngle, sweepAngle, true, paint)
            var label:String = txts[it]
            drawText(canvas, label)
            startAngle += sweepAngle
        }
    }
    fun start() {
        val degree = 360 * Math.random().toFloat()
        ObjectAnimator.ofFloat(this, "rotation", 0f, 1080 + degree).run {
            duration = 2000
            interpolator = DecelerateInterpolator()
            doOnEnd {
                var rotateAngle = 270 - degree
                if (rotateAngle < 0) rotateAngle += 360
                val position = (rotateAngle / sweepAngle).toInt()
                var label:String = txts.get(position)
                valueListener?.invoke(label)
//                Toast.makeText(result,texts.get(position),Toast.LENGTH_SHORT).show()

            }
            start()
        }
    }

    fun setValueListener(listener: (value: String) -> Unit) {
        valueListener = listener
    }
    fun setTableId(int: Int)
    {
        tid = int
    }
    private fun drawText(canvas: Canvas, text:String) {//绘制转盘标签
        textPath.reset()
        textPath.addArc(textRectF, startAngle, sweepAngle)
        paint.color = Color.BLACK
        val textWidth = paint.measureText(text)
        val hOffset = sweepAngle * Math.PI.toFloat() * (center - ringWidth * 3) / 180 / 2 - textWidth / 2
        canvas.drawTextOnPath(text, textPath, hOffset, 0f, paint)
    }
}