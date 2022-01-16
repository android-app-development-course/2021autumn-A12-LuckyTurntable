package com.example.last_homework

import android.util.TypedValue
import com.example.last_homework.App

fun Int.toDp() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toFloat(), App.context.resources.displayMetrics)

fun Int.toSp() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, toFloat(), App.context.resources.displayMetrics)