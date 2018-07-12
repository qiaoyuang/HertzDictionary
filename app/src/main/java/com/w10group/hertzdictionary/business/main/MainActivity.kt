package com.w10group.hertzdictionary.business.main

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout.ScrollingViewBehavior
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.business.about.AboutDeveloperActivity
import com.w10group.hertzdictionary.business.bean.InquireResult
import com.w10group.hertzdictionary.business.bean.LocalWord
import com.w10group.hertzdictionary.business.features.FeaturesActivity
import com.w10group.hertzdictionary.business.licence.LicenceActivity
import com.w10group.hertzdictionary.business.manager.ImageManagerService
import com.w10group.hertzdictionary.business.manager.NetworkService
import com.w10group.hertzdictionary.core.ActionBarSize
import com.w10group.hertzdictionary.core.NetworkUtil
import com.w10group.hertzdictionary.core.createTouchFeedbackBorderless
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.titleResource
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.design.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.drawerLayout
import org.jetbrains.anko.support.v4.nestedScrollView
import org.litepal.LitePal
import java.util.concurrent.CopyOnWriteArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mToolBar: Toolbar
    private lateinit var mNestedScrollView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mBackgroundImageView: ImageView
    private lateinit var mOtherMeanCard: CardView
    private lateinit var mOtherMeanLayout: LinearLayout
    private lateinit var mIVClose: ImageView
    private lateinit var mETInput: EditText

    private lateinit var mTVSrcPronunciation: TextView
    private lateinit var mTVResult: TextView
    private lateinit var mTVPronunciation: TextView
    private lateinit var mTVOtherTranslation: TextView
    private lateinit var mTVRelatedWords: TextView

    private val mData by lazy { CopyOnWriteArrayList<LocalWord>() }
    private val mAdapter by lazy {
        WordListAdapter(this, mData,
                itemOnClickListener = {
                    mETInput.setText(it)
                    inquire(it)
                },
                itemOnLongClickListener = { localWord, index, adapter ->
                    selector("", listOf("删除：${localWord.en}", "清空所有单词")) { dialog, which ->
                        if (which == 0) {
                            alert {
                                title = "您确定要删除${localWord.en}吗？"
                                message = "删除单词会使单词的查询次数清零，且不可恢复，请您确认。"
                                okButton {
                                    mData.removeAt(index)
                                    WordListAdapter.sumCount -= localWord.count
                                    adapter.notifyItemRemoved(index)
                                    adapter.notifyItemChanged(0, mData.size)
                                    Observable.just(localWord)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(Schedulers.io())
                                            .subscribeBy { it.delete() }
                                    it.dismiss()
                                }
                                cancelButton { it.dismiss() }
                            }.show()
                        } else {
                            alert {
                                title = "您确定要清空所有单词吗？"
                                message = "清空所有单词会使所有保存的已查询单词数据全部清零，且不可恢复，请您确认。"
                                yesButton {
                                    mData.clear()
                                    WordListAdapter.sumCount = 0
                                    adapter.notifyDataSetChanged()
                                    Observable.just(LocalWord::class.java)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(Schedulers.io())
                                            .subscribeBy { LitePal.deleteAll(it) }
                                    it.dismiss()
                                }
                                cancelButton { it.dismiss() }
                            }.show()
                        }
                        dialog.dismiss()
                    }
                })
    }

    private val gray600 by lazy { ContextCompat.getColor(this, R.color.gray600) }
    private val deepWhite by lazy { ContextCompat.getColor(this, R.color.deepWhite) }
    private val blue1 by lazy { ContextCompat.getColor(this, R.color.blue1) }

    private val mProgressDialog by lazy {
        progressDialog(title = "请稍候......", message = "正在获取单词数据......") {
            setCancelable(false)
            setProgressStyle(0)
        }
    }

    private companion object {
        const val STATUS_INQUIRED_NOT = 0
        const val STATUS_INQUIRED = 1
    }

    private var status = STATUS_INQUIRED_NOT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDrawerLayout = drawerLayout {

            coordinatorLayout {
                backgroundColor = deepWhite
                fitsSystemWindows = true

                themedAppBarLayout(R.style.AppTheme_AppBarOverlay) {
                    backgroundColor = Color.WHITE
                    isFocusableInTouchMode = true
                    elevation = dip(8).toFloat()
                    translationZ = dip(8).toFloat()
                    val scrollFlag = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP

                    mToolBar = toolbar {
                        titleResource = R.string.app_name
                        backgroundColor = blue1
                        popupTheme = R.style.ThemeOverlay_AppCompat_Light
                    }.lparams(matchParent, ActionBarSize.get(this@MainActivity)) {
                        scrollFlags = scrollFlag
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
                        topMargin = dip(16)
                        marginEnd = dip(16)
                        scrollFlags = scrollFlag
                    }

                    mETInput = editText {
                        hint = "点击可输入单词"
                        hintTextColor = gray600
                        textColor = Color.BLACK
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
                                if (status == STATUS_INQUIRED) restore()
                            }
                        })
                    }.lparams(matchParent, wrapContent) {
                        marginStart = dip(16)
                        marginEnd = dip(16)
                        scrollFlags = scrollFlag
                    }

                    mTVSrcPronunciation = textView {
                        visibility = View.GONE
                        textColor = Color.BLACK
                        textSize = 14f
                    }.lparams(wrapContent, wrapContent) {
                        marginStart = dip(16)
                        bottomMargin = dip(16)
                        scrollFlags = scrollFlag
                    }

                }.lparams(matchParent, wrapContent)

                mNestedScrollView = nestedScrollView {
                    visibility = View.GONE
                    verticalLayout {
                        cardView {
                            elevation = dip(4).toFloat()
                            translationZ = dip(4).toFloat()
                            isClickable = true
                            backgroundColor = blue1
                            foreground = createTouchFeedbackBorderless(this@MainActivity)
                            val sourceLanguageId = 1
                            val resultId = 2
                            val pronunciationId = 3
                            val otherTranslationsId = 4

                            relativeLayout {
                                textView {
                                    id = sourceLanguageId
                                    textColor = Color.WHITE
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
                                    textColor = Color.WHITE
                                    textSize = 22f
                                }.lparams(wrapContent, wrapContent) {
                                    below(sourceLanguageId)
                                    alignParentStart()
                                    topMargin = dip(16)
                                    marginStart = dip(16)
                                }

                                mTVPronunciation = textView {
                                    id = pronunciationId
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    below(resultId)
                                    alignParentStart()
                                    topMargin = dip(4)
                                    marginStart = dip(16)
                                }

                                mTVOtherTranslation = textView {
                                    id = otherTranslationsId
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    below(pronunciationId)
                                    alignParentStart()
                                    topMargin = dip(16)
                                    marginStart = dip(16)
                                    marginEnd = dip(16)
                                }

                                mTVRelatedWords = textView {
                                    textColor = Color.WHITE
                                    textSize = 14f
                                }.lparams(wrapContent, wrapContent) {
                                    below(otherTranslationsId)
                                    alignParentStart()
                                    margin = dip(16)
                                }

                            }.lparams(matchParent, wrapContent)
                        }.lparams(matchParent, wrapContent) {
                            margin = dip(8)
                        }

                        mOtherMeanCard = cardView {
                            elevation = dip(4).toFloat()
                            translationZ = dip(4).toFloat()
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
                    layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    itemAnimator = DefaultItemAnimator()
                }.lparams(matchParent, matchParent) {
                    behavior = ScrollingViewBehavior()
                    topMargin = dip(4)
                    bottomMargin = dip(4)
                }

            }.lparams(matchParent, matchParent)

            navigationView {
                inflateMenu(R.menu.menu_main)
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
        ImageManagerService.loadBackground(this, mBackgroundImageView)
        initRecyclerViewData()
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

    private fun initRecyclerViewData() {
        Observable.just(LitePal.order("count desc")
                .find(LocalWord::class.java))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    it?.let { mData.addAll(it) }
                    mRecyclerView.adapter = mAdapter
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
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                if (isShouldHideInput(currentFocus, ev)) {
                    currentFocus.windowToken?.let {
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
        mIVClose.visibility = View.GONE
        mETInput.setText("")
    }

    private fun inquire(word: String) {
        if (!NetworkUtil.checkNetwork(this)) {
            snackbar(mRecyclerView, "当前无网络连接")
            return
        }
        mProgressDialog.show()
        NetworkUtil.create<NetworkService>()
                .inquireWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    refreshRecyclerViewData(it)
                    //改变控件状态
                    if (status == STATUS_INQUIRED_NOT) {
                        mRecyclerView.visibility = View.GONE
                        mNestedScrollView.visibility = View.VISIBLE
                        mTVSrcPronunciation.visibility = View.VISIBLE
                        mIVClose.visibility = View.VISIBLE
                        status = STATUS_INQUIRED
                    }
                    //展示读音信息
                    it.word?.let {
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
                    if (it.dict == null) {
                        mOtherMeanCard.visibility = View.GONE
                    } else {
                        mOtherMeanCard.visibility = View.VISIBLE
                        it.dict.forEach {
                            val layoutParams1 = LinearLayout.LayoutParams(wrapContent, wrapContent)
                            layoutParams1.marginStart = dip(16)
                            val headView = TextView(this)
                            headView.textSize = 16f
                            headView.textColor = gray600
                            headView.text = it.posType
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
                            it.dictInfo?.let {
                                recyclerView.adapter = OtherMeanAdapter(this, it)
                            }
                            recyclerView.layoutParams = layoutParams2

                            mOtherMeanLayout.addView(headView)
                            mOtherMeanLayout.addView(recyclerView)
                        }
                    }
                }
                .observeOn(Schedulers.computation())
                .map {
                    //拼接其它义项
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
                    //拼接相关词组
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
                        onError = {
                            it.printStackTrace()
                            mProgressDialog.dismiss()
                            snackbar(mRecyclerView, "网络出现问题，请稍后再试。")
                        },
                        onComplete = { mProgressDialog.dismiss() }
                )
    }

    //查询动作成功发生后调用此方法来进行数据库操作以及RecyclerView更新
    private fun refreshRecyclerViewData(inquireResult: InquireResult) {
        Observable.just(inquireResult)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .map {
                    val orig = it.word!![0]
                    var word: LocalWord? = null
                    //在mData中查找word是否存在，如果存在则找到它并记录其index
                    mData.forEachIndexed { index, localWord ->
                        if (localWord.en == orig.en) {
                            word = localWord
                            localWord.count++
                            localWord.reSort(index)
                        }
                    }
                    //如果word没有初始化表示word不存在与mData中，所以创建新word
                    if (word == null) {
                        word = LocalWord(ch = orig.ch, en = orig.en)
                        mData.add(word!!)
                    }

                    WordListAdapter.sumCount++
                    word!!
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (it.isSaved) {
                        if (mIsMoved[0] >= 0) {
                            mAdapter.notifyItemRemoved(mIsMoved[0])
                            mAdapter.notifyItemInserted(mIsMoved[1])
                            mAdapter.notifyItemRangeChanged(0, mData.size)
                        } else {
                            mAdapter.notifyItemRangeChanged(0, mData.size)
                        }
                    } else {
                        val index = mData.size - 1
                        mAdapter.notifyItemRangeChanged(0, index)
                        mAdapter.notifyItemInserted(index)
                    }
                }
                .observeOn(Schedulers.io())
                .subscribeBy(
                        onNext = { it.save() },
                        onError = { it.printStackTrace() }
                )
    }

    //第一个数字为负的时候表示未移动过，非负时表示移动前的位置，第二个数表示移动后的位置
    private val mIsMoved = intArrayOf(-1, -1)

    //调整LocalWord在mData中的位置，并返回链表是否被调整过
    private fun LocalWord.reSort(index: Int) {
        mIsMoved[0] = -1
        when {
            index == 0 -> return
            mData[index - 1].count >= count -> return
            else -> {
                val start = index - 1
                for (i in start downTo 0) {
                    val word = mData[i]
                    if (word.count >= count) {
                        mData.removeAt(index)
                        val newIndex = i + 1
                        mData.add(newIndex, this)
                        mIsMoved[0] = index
                        mIsMoved[1] = newIndex
                        return
                    } else if (i == 0 && word.count < count) {
                        mData.removeAt(index)
                        mData.add(i, this)
                        mIsMoved[0] = index
                        mIsMoved[1] = i
                    }
                }
            }
        }
    }

}