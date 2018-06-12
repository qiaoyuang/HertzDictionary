package com.w10group.hertzdictionary.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import com.w10group.hertzdictionary.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.*
import org.jetbrains.anko.support.v4.drawerLayout

class MainActivity : AppCompatActivity() {

    private val frameLayoutID = 1

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mCollapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var mToolBar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        mDrawerLayout = drawerLayout {

            coordinatorLayout {
                fitsSystemWindows = true
                backgroundColorResource = R.color.deepWhite

                appBarLayout {
                    translationZ = dip(4).toFloat()
                    elevation = dip(4).toFloat()
                    fitsSystemWindows = true
                    setTheme(R.style.AppTheme_AppBarOverlay)

                    mCollapsingToolbarLayout = collapsingToolbarLayout {
                        fitsSystemWindows = true
                        contentScrim = ContextCompat.getDrawable(this@MainActivity, R.color.blue1)
                        title = "赫兹词典"
                        setExpandedTitleTextColor(ContextCompat.getColorStateList(this@MainActivity, android.R.color.transparent)!!)

                        editText {
                            hint = "点击可输入文本"
                            hintTextColor = ContextCompat.getColor(this@MainActivity, R.color.deepWhite)
                            textColor = ContextCompat.getColor(this@MainActivity, android.R.color.white)
                            textSize = 20f
                            maxLines = 5
                            background = null
                        }.lparams(matchParent, dip(96)) {
                            topMargin = actionBarSize
                            marginStart = dip(32)
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
                        }

                        mToolBar = toolbar { }.lparams(matchParent, actionBarSize) {
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
                        }

                    }.lparams(matchParent, matchParent) {
                        scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    }

                }.lparams(matchParent, dip(256))

                coordinatorLayout {
                    id = frameLayoutID
                    backgroundColorResource = R.color.deepWhite
                }.lparams(matchParent, matchParent)

            }.lparams(matchParent, matchParent)

            navigationView {
                inflateMenu(R.menu.menu_main)
                backgroundColorResource = android.R.color.white
                itemTextColor = ContextCompat.getColorStateList(this@MainActivity, android.R.color.tertiary_text_dark)
                itemIconTintList = null
                addHeaderView(createHeaderView())
            }.lparams(dip(256), matchParent) { gravity = Gravity.START }

        }

        setSupportActionBar(mToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }

    }

    private fun createHeaderView() =
            AnkoContext.create(this).apply {
                verticalLayout {
                    imageView { backgroundColorResource = R.color.blue1 }.lparams(matchParent, dip(184))
                }
            }.view

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { mDrawerLayout.openDrawer(GravityCompat.START) }
        }
        return super.onOptionsItemSelected(item)
    }


}