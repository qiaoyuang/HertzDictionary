package com.w10group.hertzdictionary.business.licence

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
import com.w10group.hertzdictionary.business.licence.OSLAdapter.OSL
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.core.ActionBarSize
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

    private companion object {
        const val OPEN_SOURCE_FILE_NAME = "open_source.txt"
        const val ANKO = "Anko"
        const val CIRCLE_IMAGE_VIEW = "CircleImageView"
        const val GLIDE = "Glide"
        const val GSON = "Gson"
        const val LITE_PAL = "LitePal"
        const val OK_HTTP = "OkHttp"
        const val RETROFIT = "Retrofit"
        const val RX_ANDROID = "RxAndroid"
        const val RX_JAVA = "RxJava"
        const val RX_KOTLIN = "RxKotlin"
        const val SUBSAMPLING = "Subsampling Scale Image View"
    }

    private val mData = ArrayList<OSL>()
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var toolbar: Toolbar

        coordinatorLayout {
            appBarLayout {
                toolbar = toolbar {
                    title = "开源许可证"
                    backgroundColorResource = R.color.blue1
                }.lparams(matchParent, ActionBarSize.get(this@LicenceActivity)) {
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
            val titles = arrayOf(
                    ANKO, CIRCLE_IMAGE_VIEW, GLIDE, GSON, LITE_PAL, OK_HTTP,
                    RETROFIT, RX_ANDROID, RX_JAVA, RX_KOTLIN, SUBSAMPLING)
            var i = 0
            val inputStream = assets.open(OPEN_SOURCE_FILE_NAME)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val builder = StringBuilder()
            var line = bufferedReader.readLine()
            var isFirst = true
            while (line != null) {
                if (line == "*") {
                    val osl = OSL(titles[i], builder.toString())
                    builder.delete(0, builder.length)
                    i++
                    mData.add(osl)
                    isFirst = true
                } else {
                    if (isFirst) isFirst = false
                    else builder.append("\n")
                    builder.append(line)
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