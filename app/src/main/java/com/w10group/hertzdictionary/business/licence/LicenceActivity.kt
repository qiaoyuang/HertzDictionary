package com.w10group.hertzdictionary.business.licence

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.w10group.hertzdictionary.business.licence.OSLAdapter.OSL
import com.w10group.hertzdictionary.R
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

class LicenceActivity : AppCompatActivity() {

    companion object {
        const val OPEN_SOURCE_FILE_NAME = "open_source.txt"
    }

    private val mData = ArrayList<OSL>()
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        val toolbarID = 1

        coordinatorLayout {
            appBarLayout {
                toolbar {
                    id = toolbarID
                    title = "开源许可证"
                    backgroundColorResource = R.color.blue1
                }.lparams(matchParent, actionBarSize) {
                    scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                }
            }.lparams(matchParent, wrapContent)

            mRecyclerView = recyclerView {
                layoutManager = LinearLayoutManager(this@LicenceActivity, LinearLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
            }.lparams(matchParent, wrapContent) {
                marginStart = dip(16)
                marginEnd = dip(16)
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }

        val toolbar = find<Toolbar>(toolbarID)
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
            val titles = arrayOf(
                    "Anko",
                    "CircleImageView",
                    "Glide",
                    "Gson",
                    "LitePal",
                    "OkHttp",
                    "Retrofit",
                    "RxAndroid",
                    "RxJava",
                    "RxKotlin",
                    "Subsampling Scale Image View")
            var i = 0
            val inputStream = assets.open(OPEN_SOURCE_FILE_NAME)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val contentBuilder = StringBuilder()
            var line = bufferedReader.readLine()
            while (line != null) {
                if (line == "*") {
                    val osl = OSL(titles[i], contentBuilder.toString())
                    contentBuilder.delete(0, contentBuilder.length)
                    i++
                    mData.add(osl)
                } else {
                    contentBuilder.append("\n")
                    contentBuilder.append(line)
                }
                line = bufferedReader.readLine()
            }
            it.onNext(OSLAdapter(this, mData))
            bufferedReader.close()
            inputStream.close()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribeBy { mRecyclerView.adapter = it }
    }

}