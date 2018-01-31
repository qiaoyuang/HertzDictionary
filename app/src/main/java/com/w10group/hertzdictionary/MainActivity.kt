package com.w10group.hertzdictionary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.w10group.hertzdictionary.view.appCompatSpineer
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView

class MainActivity : AppCompatActivity() {

    private val frameLayoutID = 1
    private val toolbarID = 2
    private val iconID = 3

    val MAIN = 0

    var fragmentStatus = MAIN

    private lateinit var mToolBar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()



        coordinatorLayout {
            backgroundColorResource = R.color.deepWhite

            appBarLayout {
                translationZ = dip(4).toFloat()
                elevation = dip(4).toFloat()

                mToolBar = toolbar {
                    id = toolbarID
                    backgroundColorResource = R.color.blue1
                    setTheme(R.style.ToolBarTheme)
                }.lparams(matchParent, actionBarSize)

                relativeLayout {
                    backgroundColor = ContextCompat.getColor(this@MainActivity, android.R.color.white)

                    val array = arrayOf("英文", "中文")
                    val arrayAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, array)
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    appCompatSpineer {
                        adapter = arrayAdapter
                        setSelection(0)
                    }.lparams(wrapContent, wrapContent) {
                        alignParentStart()
                        alignParentTop()
                        marginStart = dip(8)
                    }

                    imageView {
                        id = iconID
                        setImageResource(R.drawable.ic_swap_horiz_grey600_24dp)
                    }.lparams(wrapContent, wrapContent) {
                        centerHorizontally()
                        alignParentTop()
                        topMargin = dip(12)
                    }

                    appCompatSpineer {
                        adapter = arrayAdapter
                        setSelection(1)
                    }.lparams(wrapContent, wrapContent) {
                        alignParentEnd()
                        alignParentTop()
                        marginStart = dip(8)
                    }

                    editText {
                        hint = "点击可输入文本"
                        textColor = ContextCompat.getColor(this@MainActivity, android.R.color.black)
                        textSize = sp(10).toFloat()
                        maxLines = 5
                        background = null
                        gravity = Gravity.TOP
                    }.lparams(matchParent, dip(96)) {
                        centerHorizontally()
                        below(iconID)
                        margin = dip(8)
                    }

                }.lparams(matchParent, wrapContent)

            }.lparams(matchParent, wrapContent)

            recyclerView {}.lparams(matchParent, wrapContent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }

        }



        setSupportActionBar(mToolBar)
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(frameLayoutID, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    //添加或移除返回按钮
    private fun setBackButton(ifSet: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(ifSet)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_button -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.main_menu_button)
        when (fragmentStatus) {
            MAIN -> {
                mToolBar.title = "赫兹词典"
                item.isVisible = true
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

}