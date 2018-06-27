package com.w10group.hertzdictionary.business.licence

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.w10group.hertzdictionary.business.licence.OSLAdapter.OSL
import com.w10group.hertzdictionary.R
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.toolbar
import org.jetbrains.anko.verticalLayout
import java.io.BufferedReader
import java.io.InputStreamReader

class LicenceActivity : AppCompatActivity() {

    private val mData = ArrayList<OSL>()
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        verticalLayout {
            toolbar {
                title = "开源许可证"
                titleColor = ContextCompat.getColor(this@LicenceActivity, android.R.color.white)
                backgroundColorResource = R.color.blue1
                setTheme(R.style.ThemeOverlay_AppCompat_Light)
                popupTheme = R.style.ThemeOverlay_AppCompat_Light
            }.lparams(matchParent, actionBarSize)

            mRecyclerView = recyclerView {
                layoutManager = LinearLayoutManager(this@LicenceActivity, LinearLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
            }
        }

        loadData()
    }

    private fun loadData() {
        Observable.create<OSLAdapter> {
            val titles = arrayOf(
                    "Anko",
                    "CircleImageView",
                    "Glide",
                    "Gson",
                    "OkHttp",
                    "Retrofit",
                    "RxAndroid",
                    "RxJava",
                    "RxKotlin",
                    "Subsampling Scale Image View")
            var i = 0
            val inputStream = assets.open("open_source.txt")
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
