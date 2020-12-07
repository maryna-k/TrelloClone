package com.mkalachova.trelloclone.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * Returns true if recycler view has a child that matcher ViewMatcher at any position
 */
class RecyclerViewHasChildMatcher private constructor(val childMatcher: Matcher<View>)
    : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
    override fun describeTo(description: Description?) {
        description?.appendText("RecyclerView has matching child")
    }

    override fun matchesSafely(item: RecyclerView?): Boolean {
        val lastIndex = item?.adapter?.itemCount?.minus(1)
        for (i in 0 until lastIndex!!) {
            val viewHolder = item.findViewHolderForAdapterPosition(i)
            if (childMatcher.matches(viewHolder?.itemView)) return true
        }
        return false
    }

    companion object {
        fun recyclerHasChild(child: Matcher<View>): RecyclerViewHasChildMatcher =
            RecyclerViewHasChildMatcher(child)
    }
}