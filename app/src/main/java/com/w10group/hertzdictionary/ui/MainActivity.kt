package com.w10group.hertzdictionary.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.*
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.model.Word
import com.w10group.hertzdictionary.util.InquireWordService
import com.w10group.hertzdictionary.util.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.nestedScrollView

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mCollapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var mToolBar: Toolbar
    private lateinit var mNestedScrollView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTVResult: TextView
    private lateinit var mBackgroundImageView: ImageView

    private val mData by lazy { ArrayList<Word>() }
    private val mAdapter by lazy { WordListAdapter(this, mData) }


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

                    verticalLayout {
                        fitsSystemWindows = true
                        backgroundColorResource = android.R.color.white

                        mToolBar = toolbar {
                            title = "赫兹词典"
                            backgroundColorResource = R.color.blue1
                        }.lparams(matchParent, actionBarSize)

                        editText {
                            hint = "点击可输入单词"
                            hintTextColor = ContextCompat.getColor(this@MainActivity, R.color.gray600)
                            textColor = ContextCompat.getColor(this@MainActivity, android.R.color.black)
                            textSize = 20f
                            maxLines = 1
                            imeOptions = EditorInfo.IME_ACTION_DONE
                            setOnEditorActionListener { _, actionId, event ->
                                if (actionId == EditorInfo.IME_ACTION_SEND
                                        || actionId == EditorInfo.IME_ACTION_DONE
                                        || (event != null && KeyEvent.KEYCODE_ENTER == event.keyCode
                                                && KeyEvent.ACTION_DOWN == event.action)) {
                                    val text = text.toString().trim()
                                    Log.d("结果", "执行")
                                    inquire(text)
                                }
                                false
                            }
                        }.lparams(matchParent, dip(96)) {
                            topMargin = actionBarSize
                            marginStart = dip(32)
                            //collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
                        }

                    }.lparams(matchParent, dip(256)) {
                        scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    }

                    /*mCollapsingToolbarLayout = collapsingToolbarLayout {
                        fitsSystemWindows = true
                        contentScrim = ContextCompat.getDrawable(this@MainActivity, R.color.blue1)
                        title = "赫兹词典"
                        setExpandedTitleTextColor(ContextCompat.getColorStateList(this@MainActivity, android.R.color.transparent)!!)

                        editText {
                            hint = "点击可输入单词"
                            hintTextColor = ContextCompat.getColor(this@MainActivity, android.R.color.white)
                            textColor = ContextCompat.getColor(this@MainActivity, android.R.color.white)
                            textSize = 20f
                            maxLines = 1
                            background = null
                            imeOptions = EditorInfo.IME_ACTION_DONE
                            setOnEditorActionListener { _, actionId, event ->
                                if (actionId == EditorInfo.IME_ACTION_SEND
                                                || actionId == EditorInfo.IME_ACTION_DONE
                                                || (event != null && KeyEvent.KEYCODE_ENTER == event.keyCode
                                                && KeyEvent.ACTION_DOWN == event.action)) {
                                    val text = text.toString().trim()
                                    Log.d("结果", "执行")
                                    inquire(text)
                                }
                                false
                            }
                        }.lparams(matchParent, dip(96)) {
                            topMargin = actionBarSize
                            marginStart = dip(32)
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
                        }

                        mToolBar = toolbar {}.lparams(matchParent, actionBarSize) {
                            collapseMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
                        }

                    }.lparams(matchParent, matchParent) {
                        scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    }*/

                }.lparams(matchParent, dip(256)) {
                    elevation = dip(8).toFloat()
                    translationZ = dip(8).toFloat()
                }

                mNestedScrollView = nestedScrollView {
                    visibility = View.GONE
                    cardView {
                        backgroundResource = R.color.blue1
                        val sourceLanguageId = 1
                        relativeLayout {
                            textView {
                                id = sourceLanguageId
                                textColorResource = android.R.color.white
                                textSize = 16f
                                text = "简体中文"
                            }.lparams(wrapContent, wrapContent) {
                                alignParentTop()
                                alignParentStart()
                                topMargin = dip(8)
                                marginStart = dip(8)
                            }
                            mTVResult = textView {
                                textColorResource = android.R.color.white
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent) {
                                below(sourceLanguageId)
                                alignParentStart()
                                topMargin = dip(8)
                                marginStart = dip(8)
                            }
                        }.lparams(matchParent, dip(128))
                    }.lparams(matchParent, wrapContent) {
                        margin = dip(8)
                        elevation = dip(8).toFloat()
                        translationZ = dip(8).toFloat()
                        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    }
                }.lparams(matchParent, matchParent) {
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }

                mRecyclerView = recyclerView {
                    layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = mAdapter
                }.lparams(matchParent, matchParent) {
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }

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

        Glide.with(this)
                .load("https://cn.bing.com/az/hprichbg/rb/HenningsvaerFootball_ROW8312618022_1920x1080.jpg")
                .into(mBackgroundImageView)
    }

    private fun createHeaderView() =
            AnkoContext.create(this).apply {
                verticalLayout {
                    mBackgroundImageView = imageView {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        backgroundColorResource = R.color.blue1
                    }
                            .lparams(matchParent, dip(184))
                }
            }.view

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { mDrawerLayout.openDrawer(GravityCompat.START) }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun inquire(word: String) {
        if (!NetworkUtil.checkNetwork(this)) {
            snackbar(mRecyclerView, "当前无网络连接")
            return
        }
        NetworkUtil.create<InquireWordService>()
                .inquire(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            mRecyclerView.visibility = View.GONE
                            mNestedScrollView.visibility = View.VISIBLE
                            it.words?.let {
                                mTVResult.text = it[0].ch
                                Log.d("结果", it[0].ch)
                            }
                        },
                        onError = { it.printStackTrace() }
                )
    }

}