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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.business.about.AboutDeveloperActivity
import com.w10group.hertzdictionary.business.bean.Word
import com.w10group.hertzdictionary.business.features.FeaturesActivity
import com.w10group.hertzdictionary.business.licence.LicenceActivity
import com.w10group.hertzdictionary.business.manager.BackgroundImageManager
import com.w10group.hertzdictionary.business.manager.NetworkService
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
    private lateinit var mOtherMeanLayout: LinearLayout
    private lateinit var mIVClose: ImageView
    private lateinit var mETInput: EditText

    private lateinit var mTVSrcPronunciation: TextView
    private lateinit var mTVResult: TextView
    private lateinit var mTVPronunciation: TextView
    private lateinit var mTVOtherTranslation: TextView
    private lateinit var mTVRelatedWords: TextView

    private val mData by lazy { ArrayList<Word>() }
    private val mAdapter by lazy { WordListAdapter(this, mData) }

    private val gray600 by lazy { ContextCompat.getColor(this, R.color.gray600) }
    private val deepWhite by lazy { ContextCompat.getColor(this, R.color.deepWhite) }
    private val black by lazy { ContextCompat.getColor(this, android.R.color.black) }
    private val white by lazy { ContextCompat.getColor(this, android.R.color.white) }
    private val blue1 by lazy { ContextCompat.getColor(this, R.color.blue1) }

    private companion object {
        const val STATUS_INQUIRED_NOT = 0
        const val STATUS_INQUIRED = 1
    }

    private var status = STATUS_INQUIRED_NOT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        mDrawerLayout = drawerLayout {

            navigationView {
                inflateMenu(R.menu.menu_main)
                fitsSystemWindows = true
                isClickable = true
                backgroundColor = deepWhite
                itemTextColor = ContextCompat.getColorStateList(this@MainActivity, R.color.gray600)
                addHeaderView(createHeaderView())
                setNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.main_menu_more_features -> startActivity<FeaturesActivity>()
                        R.id.main_menu_mine -> startActivity<AboutDeveloperActivity>()
                        R.id.main_menu_licence -> startActivity<LicenceActivity>()
                    }
                    true
                }
            }.lparams(matchParent, matchParent) {
                gravity = Gravity.START
                elevation = dip(8).toFloat()
                translationZ = dip(8).toFloat()
            }

            coordinatorLayout {
                backgroundColor = deepWhite
                fitsSystemWindows = true

                appBarLayout {
                    backgroundColor = white

                    mToolBar = toolbar {
                        title = "赫兹词典"
                        backgroundColor = blue1
                        setTheme(R.style.ThemeOverlay_AppCompat_Light)
                        popupTheme = R.style.ThemeOverlay_AppCompat_Light
                    }.lparams(matchParent, actionBarSize) {
                        scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                    }

                    mIVClose = imageView {
                        visibility = View.GONE
                        setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_clear_black_24dp))
                        setOnClickListener {
                            if (status == STATUS_INQUIRED) {
                                restore()
                            }
                        }
                    }.lparams(wrapContent, wrapContent) {
                        gravity = Gravity.END or Gravity.TOP
                        topMargin = dip(8)
                        marginEnd = dip(8)
                    }

                    mETInput = editText {
                        hint = "点击可输入单词"
                        hintTextColor = gray600
                        textColor = black
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
                        addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                            override fun afterTextChanged(s: Editable?) {
                                if (status == STATUS_INQUIRED) {
                                    restore()
                                }
                            }
                        })
                    }.lparams(matchParent, wrapContent) {
                        topMargin = dip(8)
                        marginStart = dip(16)
                        marginEnd = dip(16)
                        scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
                    }

                    mTVSrcPronunciation = textView {
                        visibility = View.GONE
                        textColor = black
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
                    verticalLayout {
                        cardView {
                            backgroundColor = blue1
                            val sourceLanguageId = 1
                            val resultId = 2
                            val pronunciationId = 3
                            val otherTranslationsId = 4
                            relativeLayout {
                                textView {
                                    id = sourceLanguageId
                                    textColor = white
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
                                    textColor = white
                                    textSize = 22f
                                }.lparams(wrapContent, wrapContent) {
                                    below(sourceLanguageId)
                                    alignParentStart()
                                    topMargin = dip(16)
                                    marginStart = dip(16)
                                }

                                mTVPronunciation = textView {
                                    id = pronunciationId
                                    textColor = white
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    below(resultId)
                                    alignParentStart()
                                    topMargin = dip(4)
                                    marginStart = dip(16)
                                }

                                mTVOtherTranslation = textView {
                                    id = otherTranslationsId
                                    textColor = white
                                    textSize = 14f
                                }.lparams(matchParent, wrapContent) {
                                    below(pronunciationId)
                                    alignParentStart()
                                    topMargin = dip(16)
                                    marginStart = dip(16)
                                    marginEnd = dip(16)
                                }

                                mTVRelatedWords = textView {
                                    textColor = white
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

                        cardView {
                            verticalLayout {
                                textView {
                                    textColor = black
                                    textSize = 16f
                                    text = "词汇扩展"
                                }.lparams(wrapContent, wrapContent) {
                                    topMargin = dip(8)
                                    bottomMargin = dip(16)
                                    marginStart = dip(8)
                                }
                                mOtherMeanLayout = verticalLayout {}
                            }
                        }.lparams(matchParent, wrapContent) {
                            marginStart = dip(8)
                            marginEnd = dip(8)
                            bottomMargin = dip(16)
                            elevation = dip(8).toFloat()
                            translationZ = dip(8).toFloat()
                            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                        }
                    }.lparams(matchParent, wrapContent)
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
                        backgroundColor = blue1
                    }.lparams(matchParent, dip(184))
                }
            }.view

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { mDrawerLayout.openDrawer(GravityCompat.START) }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (status == STATUS_INQUIRED) {
                restore()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun restore() {
        status = STATUS_INQUIRED_NOT
        mRecyclerView.visibility = View.VISIBLE
        mNestedScrollView.visibility = View.GONE
        mTVSrcPronunciation.visibility = View.GONE
        mIVClose.visibility = View.GONE
        mETInput.setText("")
    }

    private fun inquire(word: String) {
        if (!NetworkUtil.checkNetwork(this)) {
            snackbar(mRecyclerView, "当前无网络连接")
            return
        }
        NetworkUtil.create<NetworkService>()
                .inquireWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (status == STATUS_INQUIRED_NOT) {
                        mRecyclerView.visibility = View.GONE
                        mNestedScrollView.visibility = View.VISIBLE
                        mTVSrcPronunciation.visibility = View.VISIBLE
                        mIVClose.visibility = View.VISIBLE
                        status = STATUS_INQUIRED
                    }
                    it.word?.let {
                        mTVResult.text = it[0].ch
                        val srcPronunciationText = "读音：${it[1].srcPronunciation}"
                        mTVSrcPronunciation.text = srcPronunciationText
                        mTVPronunciation.text = it[1].pronunciation
                    }

                    mOtherMeanLayout.removeAllViews()
                    it.dict?.forEach {
                        val layoutParams1 = LinearLayout.LayoutParams(matchParent, wrapContent)
                        layoutParams1.marginStart = dip(16)
                        val headView = TextView(this)
                        headView.textSize = 16f
                        headView.textColor = gray600
                        headView.text = it.posType
                        headView.layoutParams = layoutParams1

                        val layoutParams2 = LinearLayout.LayoutParams(matchParent, wrapContent)
                        layoutParams2.marginStart = dip(32)
                        layoutParams2.topMargin = dip(16)
                        layoutParams2.bottomMargin = dip(24)
                        val recyclerView = RecyclerView(this)
                        recyclerView.layoutManager = object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
                            override fun canScrollVertically(): Boolean {
                                return false
                            }
                        }
                        it.dictInfo?.let {
                            recyclerView.adapter = OtherMeanAdapter(this, it)
                        }
                        recyclerView.layoutParams = layoutParams2

                        mOtherMeanLayout.addView(headView)
                        mOtherMeanLayout.addView(recyclerView)
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
                                if (index == last) {
                                    builder2.append(word)
                                } else {
                                    builder2.append("$word, ")
                                }
                            }
                        }
                    }
                    arrayOf(builder1.toString(), builder2.toString())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            if (it[0].isBlank()) {
                                mTVOtherTranslation.visibility = View.GONE
                            } else {
                                mTVOtherTranslation.visibility = View.VISIBLE
                                val otherTranslationText = "其它义项：\n${it[0]}"
                                mTVOtherTranslation.text = otherTranslationText
                            }
                            val relatedWordsText = "相关词组：\n${it[1]}"
                            mTVRelatedWords.text = relatedWordsText
                        },
                        onError = { it.printStackTrace() }
                )
    }

}