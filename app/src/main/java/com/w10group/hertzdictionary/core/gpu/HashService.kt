package com.w10group.hertzdictionary.core.gpu

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.w10group.hertzdictionary.core.RxBus
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class HashService : Service(), RxBus.OnWorkListener<InquireDataEvent> {

    override fun onBind(p0: Intent?): IBinder {
        throw UnsupportedOperationException("该方法为空实现。")
    }

    override fun onCreate() {
        super.onCreate()
        RxBus.register(this)
    }

    override fun onWork(event: InquireDataEvent) {
        /*val rs = RenderScript.create(this)
        val script = ScriptC_computation(rs)
        val aIn = Allocation.createFromBitmapResource(rs, resources, R.drawable.ic_close_white_24dp)
        val aOut = Allocation.createTyped(rs, aIn.type)
        script.forEach_invert(aIn, aOut)
        rs.destroy()*/
        Observable.just(event.data)
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribeBy {
                    val md5 = md5Hash(it)
                    val sha256 = sha256Hash(it)
                    val sha512 = sha512Hash(it)
                    Log.d(MD5, md5)
                    Log.d(SHA_256, sha256)
                    Log.d(SHA_512, sha512)
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.unRegister(this)
    }
}