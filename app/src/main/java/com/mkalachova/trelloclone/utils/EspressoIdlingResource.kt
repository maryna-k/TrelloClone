package com.mkalachova.trelloclone.utils

import android.util.Log
import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"
    private var count = 0

    @JvmField val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment(){
        countingIdlingResource.increment()
        count++
        Log.i(this.javaClass.simpleName, "Method called : increment, $count")
    }

    fun decrement(){
        countingIdlingResource.decrement()
        count--
        Log.i(this.javaClass.simpleName, "Method called : decrement, $count")
    }
}