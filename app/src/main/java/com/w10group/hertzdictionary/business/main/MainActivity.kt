package com.w10group.hertzdictionary.business.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.*
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.business.bean.Word
import com.w10group.hertzdictionary.business.manager.BackgroundImageManager
import com.w10group.hertzdictionary.business.network.InquireWordService
import com.w10group.hertzdictionary.core.NetworkUtil
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
    private lateinit var mToolBar: Toolbar
    private lateinit var mNestedScrollView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mBackgroundImageView: ImageView

    private lateinit var mTVSrcPronunciation: TextView
    private lateinit var mTVResult: TextView
    private lateinit var mTVPronunciation: TextView
    private lateinit var mTVOtherTranslation: TextView
    private lateinit var mTVRelatedWords: TextView

    private val mData by lazy { ArrayList<Word>() }
    private val mAdapter by lazy { WordListAdapter(this, mData) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        mDrawerLayout = drawerLayout {
            coordinatorLayout {
                backgroundColorResource = R.color.deepWhite
                fitsSystemWindows = true

                appBarLayout {
                    translationZ = dip(4).toFloat()
                    elevation = dip(4).toFloat()
                    backgroundColorResource = android.R.color.white

                    mToolBar = toolbar {
                        title = "赫兹词典"
                        backgroundColorResource = R.color.blue1
                        setTheme(R.style.ThemeOverlay_AppCompat_Light)
                        popupTheme = R.style.ThemeOverlay_AppCompat_Light
                    }.lparams(matchParent, actionBarSize) {
                        scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                    }

                    editText {
                        hint = "点击可输入单词"
                        hintTextColor = ContextCompat.getColor(this@MainActivity, R.color.gray600)
                        textColor = ContextCompat.getColor(this@MainActivity, android.R.color.black)
                        background = null
                        textSize = 22f
                        singleLine = true
                        imeOptions = EditorInfo.IME_ACTION_SEARCH
                        setOnEditorActionListener { _, actionId, _ ->
                            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                val text = text.toString().trim()
                                inquire(text)
                            }
                            false
                        }
                    }.lparams(matchParent, wrapContent) {
                        topMargin = dip(8)
                        marginStart = dip(16)
                        marginEnd = dip(16)
                        scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                    }

                    mTVSrcPronunciation = textView {
                        visibility = View.GONE
                        textColor = ContextCompat.getColor(this@MainActivity, android.R.color.black)
                        textSize = 14f
                    }.lparams(wrapContent, wrapContent) {
                        marginStart = dip(16)
                        bottomMargin = dip(16)
                        scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                    }

                }.lparams(matchParent, wrapContent) {
                    elevation = dip(8).toFloat()
                    translationZ = dip(8).toFloat()
                }

                mNestedScrollView = nestedScrollView {
                    visibility = View.GONE
                    cardView {
                        backgroundResource = R.color.blue1
                        val sourceLanguageId = 1
                        val resultId = 2
                        val pronunciationId = 3
                        val otherTranslationsId = 4
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
                                id = resultId
                                textColorResource = android.R.color.white
                                textSize = 22f
                            }.lparams(wrapContent, wrapContent) {
                                below(sourceLanguageId)
                                alignParentStart()
                                topMargin = dip(16)
                                marginStart = dip(16)
                            }

                            mTVPronunciation = textView {
                                id = pronunciationId
                                textColorResource = android.R.color.white
                                textSize = 14f
                            }.lparams(wrapContent, wrapContent) {
                                below(resultId)
                                alignParentStart()
                                topMargin = dip(4)
                                marginStart = dip(16)
                            }

                            mTVOtherTranslation = textView {
                                id = otherTranslationsId
                                textColorResource = android.R.color.white
                                textSize = 14f
                            }.lparams(matchParent, wrapContent) {
                                below(pronunciationId)
                                alignParentStart()
                                topMargin = dip(16)
                                marginStart = dip(16)
                                marginEnd = dip(16)
                            }

                            mTVRelatedWords = textView {
                                textColorResource = android.R.color.white
                                textSize = 14f
                            }.lparams(matchParent, wrapContent) {
                                below(otherTranslationsId)
                                alignParentStart()
                                margin = dip(16)
                            }


                        }.lparams(matchParent, wrapContent)
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
                fitsSystemWindows = true
                isClickable = true
                backgroundColorResource = android.R.color.white
                itemTextColor = ContextCompat.getColorStateList(this@MainActivity, android.R.color.tertiary_text_dark)
                addHeaderView(createHeaderView())
                setNavigationItemSelectedListener {
                    false
                }
            }.lparams(dip(256), matchParent) { gravity = Gravity.START }
        }

        setSupportActionBar(mToolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }

        BackgroundImageManager.show(this, mBackgroundImageView)
    }

    private fun createHeaderView() =
            AnkoContext.create(this).apply {
                verticalLayout {
                    mBackgroundImageView = imageView {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        backgroundColorResource = R.color.blue1
                    }.lparams(matchParent, dip(184))
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
                .doOnNext {
                    mRecyclerView.visibility = View.GONE
                    mNestedScrollView.visibility = View.VISIBLE
                    mTVSrcPronunciation.visibility = View.VISIBLE
                    it.word?.let {
                        mTVResult.text = it[0].ch
                        val srcPronunciationText = "读音：${it[1].srcPronunciation}"
                        mTVSrcPronunciation.text = srcPronunciationText
                        mTVPronunciation.text = it[1].pronunciation
                    }
                }
                .observeOn(Schedulers.computation())
                .map {
                    val builder1 = StringBuilder()
                    it.alternativeTranslations?.let {
                        it[0].words?.let {
                            val last = it.size - 1
                            it.forEachIndexed { index, alternative ->
                                if (index != 0) {
                                    if (index == last) {
                                        builder1.append(alternative.word)
                                    } else {
                                        builder1.append("${alternative.word}，")
                                    }
                                }
                            }
                        }
                    }

                    val builder2 = StringBuilder()
                    it.relatedWords?.let {
                        it.words?.let {
                            val last = it.size - 1
                            it.forEachIndexed { index, word ->
                                if (index != 0) {
                                    if (index == last) {
                                        builder2.append(word)
                                    } else {
                                        builder2.append("$word,")
                                    }
                                }
                            }
                        }
                    }
                    arrayOf(builder1.toString(), builder2.toString())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            val otherTranslationText = "其它翻译：\n${it[0]}"
                            mTVOtherTranslation.text = otherTranslationText
                            val relatedWordsText = "相关词组：\n${it[1]}"
                            mTVRelatedWords.text = relatedWordsText
                        },
                        onError = { it.printStackTrace() }
                )
    }

}