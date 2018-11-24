package com.w10group.hertzdictionary.biz.main

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX
import android.support.design.widget.CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.*
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.biz.about.AboutDeveloperActivity
import com.w10group.hertzdictionary.biz.bean.InquireResult
import com.w10group.hertzdictionary.biz.features.FeaturesActivity
import com.w10group.hertzdictionary.biz.licence.LicenceActivity
import com.w10group.hertzdictionary.biz.manager.ImageManagerService
import com.w10group.hertzdictionary.biz.manager.WordDisplayView
import com.w10group.hertzdictionary.biz.manager.WordManagerServiceV3
import com.w10group.hertzdictionary.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.design.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * Created by Administrator on 2018/6/15.
 * 主界面Activity
 */

class MainActivity : CoroutineScopeActivity(), WordDisplayView {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mCollapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var mToolBar: Toolbar
    private lateinit var mNestedScrollView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mBackgroundImageView: ImageView
    private lateinit var mOtherMeanCard: CardView
    private lateinit var mOtherMeanLayout: LinearLayout
    private lateinit var mETInput: EditText

    private lateinit var mTVSrcPronunciation: TextView
    private lateinit var mTVResult: TextView
    private lateinit var mTVPronunciation: TextView
    private lateinit var mTVOtherTranslation: TextView
    private lateinit var mTVRelatedWords: TextView

    private val gray600 by lazy { ContextCompat.getColor(this, R.color.gray600) }
    private val deepWhite by lazy { ContextCompat.getColor(this, R.color.deepWhite) }
    private val blue1 by lazy { ContextCompat.getColor(this, R.color.blue1) }
    private val blue2 by lazy { ContextCompat.getColor(this, R.color.blue2) }
    private val mTitleText by lazy { getString(R.string.app_name) }

    private companion object {
        const val STATUS_INQUIRED_NOT = 0
        const val STATUS_INQUIRED = 1
    }

    private var status = STATUS_INQUIRED_NOT
    private val mWordManagerService by lazy { WordManagerServiceV3(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDrawerLayout = drawerLayout {
            layoutTransition = LayoutTransition()

            coordinatorLayout {
                backgroundColor = deepWhite
                fitsSystemWindows = true

                mAppBarLayout = appBarLayout {
                    fitsSystemWindows = true
                    backgroundColor = Color.WHITE
                    isFocusableInTouchMode = true
                    translationZ = dip(8).toFloat()

                    mCollapsingToolbarLayout = collapsingToolbarLayout {
                        fitsSystemWindows = true
                        title = mTitleText
                        setExpandedTitleColor(Color.TRANSPARENT)

                        view {
                            fitsSystemWindows = true
                            backgroundColor = blue2
                        }.lparams(matchParent, getStatusBarSize(this@MainActivity)) {
                            collapseMode = COLLAPSE_MODE_PIN
                        }

                        verticalLayout {
                            mETInput = editText {
                                hint = "点击可输入单词"
                                hintTextColor = gray600
                                textColor = Color.BLACK
                                background = null
                                textSize = 22f
                                singleLine = true
                                inputType = InputType.TYPE_TEXT_VARIATION_URI
                                imeOptions = EditorInfo.IME_ACTION_SEARCH
                                setOnEditorActionListener { _, actionId, _ ->
                                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                        val text = text.toString().trim()
                                        mWordManagerService.inquire(text)
                                    }
                                    false
                                }
                                addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                    override fun afterTextChanged(s: Editable?) {
                                        if (status == STATUS_INQUIRED) restore()
                                    }
                                })
                            }.lparams(matchParent, wrapContent)

                            mTVSrcPronunciation = textView {
                                visibility = View.GONE
                                textColor = Color.BLACK
                                textSize = 14f
                            }.lparams(wrapContent, wrapContent)

                        }.lparams(matchParent, wrapContent) {
                            topMargin = dip(100)
                            marginStart = dip(16)
                            marginEnd = dip(16)
                            collapseMode = COLLAPSE_MODE_PARALLAX
                        }

                        mToolBar = toolbar {
                            backgroundColor = blue1
                        }.lparams(matchParent, getActionBarSize(this@MainActivity)) {
                            collapseMode = COLLAPSE_MODE_PIN
                        }

                    }.lparams(matchParent, wrapContent) {
                        scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                    }

                }.lparams(matchParent, wrapContent)

                mNestedScrollView = nestedScrollView {
                    visibility = View.GONE
                    verticalLayout {
                        cardView {
                            elevation = dip(4).toFloat()
                            isClickable = true
                            backgroundColor = blue1
                            foreground = createTouchFeedbackBorderless(this@MainActivity)
                            val sourceLanguageId = 1
                            val resultId = 2
                            val pronunciationId = 3
                            val otherTranslationsId = 4
                            constraintLayout {
                                imageView {
                                    setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_close_white_24dp))
                                    setOnClickListener { if (status == STATUS_INQUIRED) restore() }
                                }.lparams(wrapContent, wrapContent) {
                                    topToTop = PARENT_ID
                                    endToEnd = PARENT_ID
                                }
                                textView {
                                    id = sourceLanguageId
                                    textColor = Color.WHITE
                                    textSize = 16f
                                    text = "简体中文"
                                }.lparams(wrapContent, wrapContent) {
                                    topToTop = PARENT_ID
                                    startToStart = PARENT_ID
                                }
                                mTVResult = textView {
                                    id = resultId
                                    textColor = Color.WHITE
                                    textSize = 22f
                                }.lparams(wrapContent, wrapContent) {
                                    topToBottom = sourceLanguageId
                                    startToStart = PARENT_ID
                                    topMargin = dip(16)
                                    marginStart = dip(8)
                                }
                                mTVPronunciation = textView {
                                    id = pronunciationId
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    topToBottom = resultId
                                    startToStart = resultId
                                    topMargin = dip(4)
                                }
                                mTVOtherTranslation = textView {
                                    id = otherTranslationsId
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    topToBottom = pronunciationId
                                    startToStart = pronunciationId
                                    topMargin = dip(16)
                                }
                                mTVRelatedWords = textView {
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    topToBottom = otherTranslationsId
                                    bottomToBottom = PARENT_ID
                                    startToStart = otherTranslationsId
                                    topMargin = dip(16)
                                }
                            }.lparams(matchParent, wrapContent) {
                                margin = dip(16)
                            }
                        }.lparams(matchParent, wrapContent) {
                            margin = dip(8)
                        }

                        mOtherMeanCard = cardView {
                            elevation = dip(4).toFloat()
                            isClickable = true
                            backgroundColor = Color.WHITE
                            foreground = createTouchFeedbackBorderless(this@MainActivity)
                            verticalLayout {
                                textView {
                                    textColor = Color.BLACK
                                    textSize = 16f
                                    text = "词汇扩展"
                                }.lparams(wrapContent, wrapContent) {
                                    topMargin = dip(8)
                                    bottomMargin = dip(16)
                                    marginStart = dip(8)
                                }
                                mOtherMeanLayout = verticalLayout().lparams(matchParent, wrapContent)
                            }.lparams(matchParent, wrapContent)
                        }.lparams(matchParent, wrapContent) {
                            marginStart = dip(8)
                            marginEnd = dip(8)
                            bottomMargin = dip(16)
                        }
                    }.lparams(matchParent, wrapContent)
                }.lparams(matchParent, matchParent) {
                    behavior = ScrollingViewBehavior()
                }

                mRecyclerView = recyclerView {
                    val linearLayoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    layoutManager = linearLayoutManager
                    itemAnimator = DefaultItemAnimator()
                    var firstPosition = 0
                    var lastPosition = 0
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                            lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                if (firstPosition == 0 && mScrollFlag) {
                                    recyclerView.snackbar( "已滑动到顶部")
                                    mScrollFlag = false
                                }
                                if (lastPosition + 1 == adapter?.itemCount && mScrollFlag) {
                                    recyclerView.snackbar("已滑动到底部")
                                    mScrollFlag = false
                                }
                            }
                        }
                    })
                }.lparams(matchParent, matchParent) {
                    behavior = ScrollingViewBehavior()
                }

            }.lparams(matchParent, matchParent)

            navigationView {
                inflateMenu(R.menu.menu_navigation)
                fitsSystemWindows = true
                isClickable = true
                backgroundColor = deepWhite
                itemTextColor = ContextCompat.getColorStateList(this@MainActivity, R.color.gray600)
                addHeaderView(createHeaderView())
                setNavigationItemSelectedListener {
                    it.isCheckable = false
                    when (it.itemId) {
                        R.id.main_menu_more_features -> startActivity<FeaturesActivity>()
                        R.id.main_menu_mine -> startActivity<AboutDeveloperActivity>()
                        R.id.main_menu_licence -> startActivity<LicenceActivity>()
                    }
                    true
                }
            }.lparams(matchParent, matchParent) {
                gravity = Gravity.START
            }
        }

        setSupportActionBar(mToolBar)
        val toggle = ActionBarDrawerToggle(this@MainActivity, mDrawerLayout, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        launch { ImageManagerService.loadBackground(this@MainActivity, mBackgroundImageView) }
        launch(Dispatchers.IO) { mWordManagerService.getAllWord() }
    }

    private fun createHeaderView() =
            AnkoContext.create(this).apply {
                mBackgroundImageView = imageView {
                    layoutParams = ViewGroup.LayoutParams(matchParent, dip(176))
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    backgroundColor = blue1
                    isClickable = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        foreground = createTouchFeedbackBorderless(this@MainActivity)
                }
            }.view

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //标记位，当值为true时，RecyclerView滑动到顶部或底部才会有弹窗。
    private var mScrollFlag = false
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.move_to_Bottom -> recyclerViewScroll(false)
            R.id.move_to_top -> recyclerViewScroll(true)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun recyclerViewScroll(isTop: Boolean) {
        if (status == STATUS_INQUIRED_NOT) {
            mScrollFlag = true
            if (isTop) mWordManagerService.scrollToTop()
            else mWordManagerService.scrollToBottom()
        }
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (isShouldHideInput(currentFocus, event)) {
                    currentFocus?.windowToken?.let {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(it, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(view: View?, event: MotionEvent): Boolean {
        if (view != null && view == mETInput) {
            val l = intArrayOf(0, 0)
            view.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val right = left + view.width
            val bottom = top + view.height
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    //将界面恢复到未查询的状态
    private fun restore() {
        status = STATUS_INQUIRED_NOT
        mRecyclerView.visibility = View.VISIBLE
        mNestedScrollView.visibility = View.GONE
        mTVSrcPronunciation.visibility = View.GONE
        mCollapsingToolbarLayout.title = mTitleText
        mETInput.setText("")
    }

    override fun getEditText(): EditText = mETInput
    override fun getRecyclerView(): RecyclerView = mRecyclerView
    override fun getContext(): Context = this
    override fun getCoroutineScope(): CoroutineScope = this

    override suspend fun displayInquireResult(inquireResult: InquireResult, word: String) {
        //改变控件状态
        if (status == STATUS_INQUIRED_NOT) {
            mRecyclerView.visibility = View.GONE
            mNestedScrollView.visibility = View.VISIBLE
            mTVSrcPronunciation.visibility = View.VISIBLE
            mAppBarLayout.setExpanded(true, true)
            mCollapsingToolbarLayout.title = word
            status = STATUS_INQUIRED
        }

        //展示读音信息
        inquireResult.word?.let {
            mTVResult.text = it[0].ch
            if (it[1].srcPronunciation.isBlank()) {
                mTVSrcPronunciation.visibility = View.GONE
            } else {
                mTVSrcPronunciation.visibility = View.VISIBLE
                val srcPronunciationText = "读音：${it[1].srcPronunciation}"
                mTVSrcPronunciation.text = srcPronunciationText
            }

            if (it[1].pronunciation.isBlank()) {
                mTVPronunciation.visibility = View.GONE
            } else {
                mTVPronunciation.visibility = View.VISIBLE
                mTVPronunciation.text = it[1].pronunciation
            }
        }

        //显示扩展词意
        mOtherMeanLayout.removeAllViews()
        if (inquireResult.dict == null) {
            mOtherMeanCard.visibility = View.GONE
        } else {
            mOtherMeanCard.visibility = View.VISIBLE
            inquireResult.dict.forEach { dict ->
                val layoutParams1 = LinearLayout.LayoutParams(wrapContent, wrapContent)
                layoutParams1.marginStart = dip(16)
                val headView = TextView(this)
                headView.textSize = 16f
                headView.textColor = gray600
                headView.text = dict.posType
                headView.layoutParams = layoutParams1

                val layoutParams2 = LinearLayout.LayoutParams(matchParent, wrapContent)
                layoutParams2.marginStart = dip(32)
                layoutParams2.marginEnd = dip(8)
                layoutParams2.topMargin = dip(16)
                layoutParams2.bottomMargin = dip(24)
                val recyclerView = RecyclerView(this)
                recyclerView.layoutManager = object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                dict.dictInfo?.let {
                    recyclerView.adapter = OtherMeanAdapter(this, it)
                }
                recyclerView.layoutParams = layoutParams2

                mOtherMeanLayout.addView(headView)
                mOtherMeanLayout.addView(recyclerView)
            }
        }
    }

    override suspend infix fun displayOtherTranslation(words: String) {
        mTVOtherTranslation.setWords("其它义项：", words)
    }

    override suspend infix fun displayRelatedWords(words: String) {
        mTVRelatedWords.setWords("相关词组：", words)
    }

    private fun TextView.setWords(tips: String, words: String) {
        visibility = if (words.isBlank()) {
            View.GONE
        } else {
            val wordText = "$tips\n$words"
            text = wordText
            View.VISIBLE
        }
    }

}