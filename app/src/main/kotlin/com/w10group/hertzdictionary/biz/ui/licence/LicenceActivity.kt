package com.w10group.hertzdictionary.biz.ui.licence

import android.os.Bundle
import android.view.MenuItem
import com.w10group.hertzdictionary.core.architecture.CoroutineScopeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 * Created by Administrator on 2018/6/26.
 * 开源许可证Activity
 */

class LicenceActivity : CoroutineScopeActivity<LicenceActivity>() {

    private companion object {
        const val OPEN_SOURCE_FILE_NAME = "open_source.txt"
    }

    override val uiComponent = LicenceActivityUIComponent(this)
    override val implementer = this

    private val mData = LinkedList<OSL>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadDataByCoroutines()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadDataByCoroutines() = launch(Dispatchers.IO) {
        BufferedReader(InputStreamReader(assets.open(OPEN_SOURCE_FILE_NAME), "UTF-8")).use {
            val builder = StringBuilder()
            var line = it.readLine()
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
                line = it.readLine()
            }
        }
        withContext(Dispatchers.Main) {
            uiComponent.setAdapter(OSLAdapter(this@LicenceActivity, mData))
        }
    }

}