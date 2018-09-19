package com.w10group.hertzdictionary.biz.licence

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.w10group.hertzdictionary.biz.licence.OSLAdapter.OSL
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.core.getActionBarSize
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 * Created by Administrator on 2018/6/26.
 * 开源许可证Activity
 */

class LicenceActivity : AppCompatActivity() {

    private companion object {
        const val OPEN_SOURCE_FILE_NAME = "open_source.txt"
    }

    private val mData = LinkedList<OSL>()
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var toolbar: Toolbar

        coordinatorLayout {
            appBarLayout {
                toolbar = toolbar {
                    title = "开源许可证"
                    backgroundColorResource = R.color.blue1
                }.lparams(matchParent, getActionBarSize(this@LicenceActivity)) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                }
            }.lparams(matchParent, wrapContent)

            mRecyclerView = recyclerView {
                layoutManager = LinearLayoutManager(this@LicenceActivity, LinearLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
            }.lparams(matchParent, wrapContent) {
                marginStart = dip(16)
                marginEnd = dip(16)
                behavior = ScrollingViewBehavior()
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { onBackPressed() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData() {
        Observable.create<OSLAdapter> {
            val inputStream = assets.open(OPEN_SOURCE_FILE_NAME)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val builder = StringBuilder()
            var line = bufferedReader.readLine()
            var isFirst = true
            while (line != null) {
                when (line) {
                    "&" -> {
                        val osl = OSL(builder.toString())
                        builder.delete(0, builder.length)
                        mData.add(osl)
                        isFirst = true
                    }
                    "*" -> {
                        mData.last.content = builder.toString()
                        builder.delete(0, builder.length)
                        isFirst = true
                    }
                    else -> {
                        if (isFirst) isFirst = false
                        else builder.append("\n")
                        builder.append(line)
                    }
                }
                line = bufferedReader.readLine()
            }
            it.onNext(OSLAdapter(this, mData))
            it.onComplete()
            bufferedReader.close()
            inputStream.close()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribeBy { mRecyclerView.adapter = it }
    }

}