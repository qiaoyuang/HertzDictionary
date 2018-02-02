package com.w10group.hertzdictionary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.collapsingToolbarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.navigationView
import org.jetbrains.anko.support.v4.drawerLayout

class MainActivity : AppCompatActivity() {

    val MAIN = 0

    val frameLayoutID = 1

    var fragmentStatus = MAIN

    private lateinit var mToolBar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        drawerLayout {

            coordinatorLayout {
                backgroundColorResource = R.color.deepWhite
                fitsSystemWindows = true

                appBarLayout {
                    translationZ = dip(4).toFloat()
                    elevation = dip(4).toFloat()
                    fitsSystemWindows = true

                    collapsingToolbarLayout {
                        fitsSystemWindows = true
                        contentScrim = ContextCompat.getDrawable(this@MainActivity, R.color.blue2)
                        setExpandedTitleTextColor(ContextCompat.getColorStateList(this@MainActivity, android.R.color.transparent)!!)

                        editText {
                            hint = "点击可输入文本"
                            hintTextColor = ContextCompat.getColor(this@MainActivity, R.color.deepWhite)
                            textColor = ContextCompat.getColor(this@MainActivity, android.R.color.white)
                            textSize = sp(10).toFloat()
                            maxLines = 5
                            background = null
                        }.lparams(matchParent, dip(96)) {
                            topMargin = actionBarSize
                            marginStart = dip(32)
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
                        }

                        mToolBar = toolbar {
                            backgroundColorResource = R.color.blue2
                            setTheme(R.style.ToolBarTheme)
                        }.lparams(matchParent, actionBarSize) {
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
                        }

                    }.lparams(matchParent, matchParent) {
                        scrollFlags
                    }

                }.lparams(matchParent, dip(256))

            }.lparams(matchParent, matchParent)

            navigationView {
                inflateMenu(R.menu.menu_main)
            }.lparams(dip(256), matchParent) {
                gravity = Gravity.START
            }
        }

        setSupportActionBar(mToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
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

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
    }*/

}