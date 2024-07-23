package io.agora.board.yjs

import com.hippo.quickjs.android.JSArray
import com.hippo.quickjs.android.JSBoolean
import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSFunctionCallback
import com.hippo.quickjs.android.JSNumber
import com.hippo.quickjs.android.JSString
import com.hippo.quickjs.android.JSValue

/**
 * author : fenglibin
 * date : 2024/7/18
 * description :
 */
class YMap(val jsMap: JSValue) : YAbstractType(jsMap) {
    private val context: JSContext = jsMap.jsContext
    private val observerMap: MutableMap<YMapObserver, YJSObserver> = mutableMapOf()

    val doc: YDoc? = null

    val size: Int
        get() = jsMap.getKeyedInt("size")

    val parent: YAbstractType? = null

    fun set(key: String, value: Any) {
        val jsSetFun = jsMap.getKeyedFunction("set")
        val k = context.createJSString(key)
        val v = value.toJSValue(context)
        context.callFunction(jsSetFun, jsMap, arrayOf(k, v))
    }

    fun get(key: String): Any? {
        val jsGetFun = jsMap.getKeyedFunction("get")
        val obj = context.callFunction(jsGetFun, jsMap, arrayOf(context.createJSString(key)))
        return fromJSValue(obj)
    }

    internal fun getJsValue(key: String): JSValue? {
        val jsGetFun = jsMap.getKeyedFunction("get")
        return context.callFunction(jsGetFun, jsMap, arrayOf(context.createJSString(key)))
    }

    fun delete(key: String) {
        val jsDeleteFun = jsMap.getKeyedFunction("delete")
        context.callFunction(jsDeleteFun, jsMap, arrayOf(context.createJSString(key)))
    }

    fun has(key: String): Boolean {
        val jsHasFun = jsMap.getKeyedFunction("has")
        val obj = context.callFunction(jsHasFun, jsMap, arrayOf(context.createJSString(key)))
        return obj.cast(JSBoolean::class.java).boolean
    }

    fun keys(): List<String> {
        val jsKeys = context.callFunction(jsMap.getKeyedFunction("keys"), jsMap, arrayOf())
        val jsArray = context.globalObject.getKeyedValue("Array").getKeyedFunction("from")
        val jsKeysArray =
            context.callFunction(jsArray, context.createJSNull(), arrayOf(jsKeys)).cast(JSArray::class.java)
        val keys = mutableListOf<String>()
        for (i in 0 until jsKeysArray.length) {
            val key = jsKeysArray.getIndexedString(i)
            keys.add(key)
        }
        return keys
    }

    fun toJSON(): String {
        val f = jsMap.getKeyedFunction("toJSON")
        val jsonObj = context.callFunction(f, jsMap, arrayOf())
        val toJSON = context.globalObject.getKeyedValue("JSON").getKeyedFunction("stringify")
        val jsString = context.callFunction(toJSON, context.createJSNull(), arrayOf(jsonObj)).cast(JSString::class.java)
        return jsString.string
    }

    fun addObserver(observer: YMapObserver) {
        val jsObserver = context.createJSFunction(JSFunctionCallback { context, args ->
            val event = args[0] as JSValue
            observer.onChange(YMapEvent(event))
            return@JSFunctionCallback context.createJSUndefined()
        })
        observerMap[observer] = jsObserver
        jsMap.getKeyedFunction("observe").invoke(jsMap, arrayOf(jsObserver))
    }

    fun removeObserver(observer: YMapObserver) {
        val jsObserver = observerMap[observer]
        jsMap.getKeyedFunction("unobserve").invoke(jsMap, arrayOf(jsObserver))
    }

    fun addDeepObserver() {}

    fun removeDeepObserver() {}
}

fun YMap.getString(key: String): String? {
    return getJsValue(key)?.cast(JSString::class.java)?.string
}

fun YMap.getInt(key: String): Int? {
    return getJsValue(key)?.cast(JSNumber::class.java)?.int
}

fun YMap.getLong(key: String): Long? {
    return getJsValue(key)?.cast(JSNumber::class.java)?.long
}

fun YMap.getBoolean(key: String): Boolean? {
    return getJsValue(key)?.cast(JSBoolean::class.java)?.boolean
}