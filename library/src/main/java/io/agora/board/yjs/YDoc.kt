package io.agora.board.yjs

import android.util.Log
import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSFunctionCallback
import com.hippo.quickjs.android.JSObject
import com.hippo.quickjs.android.JSValue

/**
 * author : fenglibin
 * date : 2024/7/18
 * description :
 */
class YDoc(val jsDoc: JSValue) {
    private val context: JSContext = jsDoc.jsContext

    private val updateObserverMap: MutableMap<YDocUpdateObserver, YJSObserver> = mutableMapOf()
    private val destroyObserverMap: MutableMap<YDocDestroyObserver, YJSObserver> = mutableMapOf()

    val clientID: Number
        get() = jsDoc.getKeyedLong("clientID")

    val gc: Boolean
        get() = jsDoc.getKeyedBool("gc")

    fun getArray(s: String = ""): YArray {
        return jsDoc.getKeyedValue("getArray").let {
            val jsArray = context.callFunction(it, jsDoc, arrayOf(context.createJSString(s)))
            YArray(jsArray.cast(JSObject::class.java))
        }
    }

    fun getMap(id: String = ""): YMap {
        val jsFun = jsDoc.getKeyedFunction("getMap")
        val jdId = context.createJSString(id)
        val jsMap = context.callFunction(jsFun, jsDoc, arrayOf(jdId))
        return YMap(jsMap.cast(JSObject::class.java))
    }

    fun getText(s: String = ""): YText {
        val jsFun = jsDoc.getKeyedFunction("getText")
        val jdId = context.createJSString(s)
        val jsText = context.callFunction(jsFun, jsDoc, arrayOf(jdId))
        return YText(jsText.cast(JSObject::class.java))
    }

    fun destroy() {
        val jsFun = jsDoc.getKeyedValue("destroy")
        context.callFunction(jsFun, jsDoc, arrayOf())
    }

    fun transact(transact: () -> Unit) {
        val jsFun = jsDoc.getKeyedValue("transact")
        context.callFunction(jsFun, jsDoc, arrayOf(context.createJSFunction(JSFunctionCallback { _, _ ->
            transact()
            return@JSFunctionCallback context.createJSUndefined()
        })))
    }

    fun addUpdateObserver(observer: YDocUpdateObserver) {
        val jsObserver = context.createJSFunction(JSFunctionCallback { context, args ->
            val update = args[0].cast(JSObject::class.java)
            val origin = args[1]
            val doc = args[2]
            val tr = args[3]
            return@JSFunctionCallback context.createJSUndefined()
        })
        updateObserverMap[observer] = jsObserver
        jsDoc.getKeyedFunction("on").invoke(jsDoc, arrayOf(context.createJSString("update"), jsObserver))
    }

    fun removeUpdateObserver(observer: YDocUpdateObserver) {
        val jsObserver = updateObserverMap[observer]
        jsDoc.getKeyedFunction("off").invoke(jsDoc, arrayOf(context.createJSString("update"), jsObserver))
    }

    fun addDestroyObserver(observer: YDocDestroyObserver) {
        val jsObserver = context.createJSFunction(JSFunctionCallback { context, args ->
            if (args[0].pointer != jsDoc.pointer) {
                Log.e("YDoc", "destroy doc not equal")
            }
            observer.onDocDestroy(this)
            return@JSFunctionCallback context.createJSUndefined()
        })
        destroyObserverMap[observer] = jsObserver
        jsDoc.getKeyedFunction("on").invoke(jsDoc, arrayOf(context.createJSString("destroy"), jsObserver))
    }

    fun removeDestroyObserve(observer: YDocDestroyObserver) {
        val jsObserver = destroyObserverMap[observer]
        jsDoc.getKeyedFunction("off").invoke(jsDoc, arrayOf(context.createJSString("destroy"), jsObserver))
    }
}

