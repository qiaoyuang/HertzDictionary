package com.w10group.hertzdictionary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.w10group.hertzdictionary.view.appCompatSpineer
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.collapsingToolbarLayout
import org.jetbrains.anko.design.coordinatorLayout

class MainActivity : AppCompatActivity() {

    private val frameLayoutID = 1
    private val toolbarID = 2
    private val iconID = 3

    val MAIN = 0

    var fragmentStatus = MAIN

    private lateinit var mToolBar: Toolbar

    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mCoordinatorLayout: CoordinatorLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()


        coordinatorLayout {
            backgroundColorResource = R.color.deepWhite
            fitsSystemWindows = true
            appBarLayout {
                fitsSystemWindows = true
                translationZ = dip(4).toFloat()
                elevation = dip(4).toFloat()
                collapsingToolbarLayout {
                    fitsSystemWindows = true
                    contentScrim = ContextCompat.getDrawable(this@MainActivity, android.R.color.white)
                    setExpandedTitleTextColor(ContextCompat.getColorStateList(this@MainActivity, android.R.color.transparent)!!)
                    relativeLayout {
                        backgroundColor = ContextCompat.getColor(this@MainActivity, android.R.color.white)
                        appCompatSpineer {}.lparams(wrapContent, wrapContent) {
                            alignParentStart()
                            alignParentTop()
                        }
                        imageView { id = iconID }.lparams(wrapContent, wrapContent) {
                            centerHorizontally()
                            alignParentTop()
                        }
                        appCompatSpineer {}.lparams(wrapContent, wrapContent) {
                            alignParentEnd()
                            alignParentTop()
                        }
                        editText {
                            textSize = sp(16).toFloat()
                            textColor = ContextCompat.getColor(this@MainActivity, android.R.color.black)
                        }.lparams(matchParent, wrapContent) {
                            centerHorizontally()
                            below(iconID)
                        }
                    }.lparams(matchParent, wrapContent) { topMargin = actionBarSize * 3 }
                    toolbar {
                        id = toolbarID
                        backgroundColorResource = R.color.blue1
                    }.lparams(matchParent, actionBarSize) { collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN }
                }.lparams(matchParent, matchParent) {
                    scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL //| AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                }
            }.lparams(matchParent, dip(256))
            frameLayout { id = frameLayoutID }.lparams(matchParent, matchParent)
        }



        mToolBar = find(toolbarID)
        setSupportActionBar(mToolBar)
        find<ImageView>(iconID).setImageResource(R.drawable.ic_swap_horiz_grey600_24dp)
        setFragment(MainFragment())
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

    /*private fun setScroll(scrollable: Boolean) {
        appBarLayout.setExpanded(scrollable, true)
        coordinatorLayout.setScrollable(scrollable)
        nestedScrollView.setNestedScrollingEnabled(scrollable)
    }*/


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