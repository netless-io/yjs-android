package io.agora.board.yjs

import android.util.Log
import com.hippo.quickjs.android.JSArray
import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSString
import com.hippo.quickjs.android.JSValue

/**
 * author : fenglibin
 * date : 2024/7/18
 * description :
 */
class YArray(private val jsArray: JSValue) : YAbstractType(jsArray) {
    private val context: JSContext = jsArray.jsContext
    private val observerMap: MutableMap<YArrayObserver, YJSObserver> = mutableMapOf()

    val length: Int
        get() = jsArray.getKeyedInt("length")

    fun addObserver(observer: YArrayObserver) {
        val jsObserver = context.createJSFunction { _, args ->
            Log.e("YArray", "addChangeObserver ${args.size}")
            observer.onChange(YArrayEvent(args[0]))

            return@createJSFunction context.createJSUndefined()
        }
        observerMap[observer] = jsObserver
        context.callFunction(jsArray.getKeyedValue("observe"), jsArray, arrayOf(jsObserver))
    }

    fun removeObserver(observer: YArrayObserver) {
        val jsObserver = observerMap[observer]
        context.callFunction(jsArray.getKeyedValue("unobserve"), jsArray, arrayOf(jsObserver))
    }

    fun addDeepChangeObserver(observer: YArrayObserver) {

    }

    fun removeDeepChangeObserver(observer: YArrayObserver) {

    }

    fun insert(index: Int, content: Array<*>) {
        context.callFunction(
            jsArray.getKeyedValue("insert"),
            jsArray,
            arrayOf(context.createJSNumber(index), convertToJsContent(content))
        )
    }

    private fun convertToJsContent(content: Array<*>): JSArray {
        val jsContent = context.createJSArray()
        content.map { it.toJSValue(context) }.forEachIndexed { index, jsValue ->
            jsContent.setProperty(index, jsValue)
        }
        return jsContent
    }

    fun delete(index: Int, length: Int) {
        context.callFunction(
            jsArray.getKeyedValue("delete"),
            jsArray,
            arrayOf(context.createJSNumber(index), context.createJSNumber(length))
        )
    }

    fun push(content: Array<*>) {
        context.callFunction(
            jsArray.getKeyedValue("push"),
            jsArray,
            arrayOf(convertToJsContent(content))
        )
    }

    fun unshift(content: Array<*>) {
        insert(0, content)
    }

    fun slice(start: Int, end: Int): YArray {
        val p = context.callFunction(
            jsArray.getKeyedValue("slice"),
            jsArray,
            arrayOf(context.createJSNumber(start), context.createJSNumber(end))
        )
        return YArray(p)
    }

    fun get(index: Int): Any? {
        val jsValue = context.callFunction(
            jsArray.getKeyedValue("get"),
            jsArray,
            arrayOf(context.createJSNumber(index))
        )
        return fromJSValue(jsValue)
    }

    fun toArray() {
        throw NotImplementedError()
    }

    fun toJSON(): String {
        val jsonObj = jsArray.getKeyedFunction("toJSON").invoke(jsArray, arrayOf())
        val toJSON = context.globalObject.getKeyedValue("JSON").getKeyedFunction("stringify")
        val jsString = toJSON.invoke(context.globalObject, arrayOf(jsonObj)).cast(JSString::class.java)
        return jsString.string
    }
}