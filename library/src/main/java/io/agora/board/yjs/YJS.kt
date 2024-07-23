package io.agora.board.yjs

import android.content.Context
import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSFunctionCallback
import com.hippo.quickjs.android.JSRuntime
import com.hippo.quickjs.android.JSString
import com.hippo.quickjs.android.JSValue
import com.hippo.quickjs.android.QuickJS
import kotlin.random.Random

/**
 * author : fenglibin
 * date : 2024/7/18
 * description :
 */
class YJS(builder: Builder) {
    private val quickJS: QuickJS = QuickJS.Builder().build()
    private val runtime: JSRuntime = quickJS.createJSRuntime()
    private val context: JSContext = runtime.createJSContext()

    init {
        context.evaluate("var console = { log: function(message) { _consoleLog(message) } }", "consoleFun")
        context.globalObject.setProperty("_consoleLog", context.createJSFunction(
            JSFunctionCallback { context, args ->
                return@JSFunctionCallback context.createJSUndefined()
            }
        ))

        context.evaluate("var crypto = { getRandomValues: function() { return [_getRandomValues()]; } }", "randomFun")
        context.globalObject.setProperty("_getRandomValues", context.createJSFunction(
            JSFunctionCallback { context, args ->
                return@JSFunctionCallback context.createJSNumber(Random.nextInt())
            }
        ))

        context.evaluate(getAssetsYjs(builder.context), "doY.js")
    }

    private fun getAssetsYjs(c: Context): String {
        c.resources.assets.open("doY.js").use {
            return it.bufferedReader().readText()
        }
    }

    private val yjsRoot: JSValue = context.globalObject.getProperty("Y")

    fun createDoc(): YDoc {
        val fDoc = yjsRoot.getKeyedValue("Doc")
        val jsDoc = context.callConstructor(fDoc, arrayOf())
        return YDoc(jsDoc)
    }

    fun createMap(): YMap {
        val fMap = yjsRoot.getKeyedValue("Map")
        val jsMap = context.callConstructor(fMap, arrayOf())
        return YMap(jsMap)
    }

    fun createArray(): YArray {
        val fArray = yjsRoot.getKeyedValue("Array")
        val jsArray = context.callConstructor(fArray, arrayOf())
        return YArray(jsArray)
    }

    fun createText(): YText {
        val fText = yjsRoot.getKeyedValue("Text")
        val jsText = context.callConstructor(fText, arrayOf())
        return YText(jsText)
    }

    fun createUndoManager(type: YAbstractType): YUndoManager {
        val jsFun = yjsRoot.getKeyedValue("UndoManager")
        val jsUndoManager = context.callConstructor(jsFun, arrayOf(type.jsValue))
        return YUndoManager(jsUndoManager, context)
    }

    fun encodeStateAsUpdate(ydoc: YDoc): ByteArray {
        val f = yjsRoot.getKeyedValue("encodeStateAsUpdate")
        val jsUpdate = context.callFunction(f, yjsRoot, arrayOf(ydoc.jsDoc))
        return jsUpdate.toByteArray()
    }

    fun applyUpdate(ydoc: YDoc, update: ByteArray) {
        val f = yjsRoot.getKeyedValue("applyUpdate")
        val jsUpdate = update.toJSUint8Array(context)
        context.callFunction(f, yjsRoot, arrayOf(ydoc.jsDoc, jsUpdate))
    }

    fun release() {
        context.close()
        runtime.close()
    }

    class Builder(val context: Context) {
        fun build(): YJS {
            return YJS(this)
        }
    }

    companion object {
        const val VERSION = "0.1.0-alpha01"
    }
}