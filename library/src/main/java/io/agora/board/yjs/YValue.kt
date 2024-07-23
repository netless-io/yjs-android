package io.agora.board.yjs

import com.hippo.quickjs.android.JSArray
import com.hippo.quickjs.android.JSArrayBuffer
import com.hippo.quickjs.android.JSBoolean
import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSNumber
import com.hippo.quickjs.android.JSObject
import com.hippo.quickjs.android.JSString
import com.hippo.quickjs.android.JSValue

/**
 * author : fenglibin
 * date : 2024/7/19
 * description :
 */
fun fromJSValue(jsValue: JSValue): Any? {
    return when (jsValue) {
        is JSString -> {
            jsValue.string
        }

        is JSBoolean -> {
            jsValue.boolean
        }

        is JSNumber -> {
            jsValue.double
        }

        is JSArrayBuffer -> {
            jsValue.toByteArray()
        }

        is JSObject -> {
            jsValue.javaObject as YAbstractType
        }

        else -> {
            throw IllegalArgumentException("Unsupported type")
        }
    }
}

fun Any?.toJSValue(context: JSContext): JSValue = when (this) {
    null -> context.createJSNull()
    is String -> context.createJSString(this)
    is Double -> context.createJSNumber(this)
    is Boolean -> context.createJSBoolean(this)
    is Int -> context.createJSNumber(this)
    is Long -> context.createJSNumber(this.toDouble())
    is ByteArray -> context.createJSArrayBuffer(this)
    is YAbstractType -> this.jsValue
    is Array<*>, is LongArray, is IntArray, is BooleanArray, is DoubleArray -> {
        convertArray(context, this)
    }

    is Map<*, *> -> {
        val jsObject = context.createJSObject()
        this.forEach { (k, v) ->
            jsObject.setProperty(k.toString(), v.toJSValue(context))
        }
        jsObject
    }

    else -> throw IllegalArgumentException("Unsupported type: $this")
}

private fun convertArray(context: JSContext, array: Any): JSArray {
    val jsArray = context.createJSArray()
    when (array) {
        is Array<*> -> {
            array.forEachIndexed { index, any ->
                jsArray.setProperty(index, any.toJSValue(context))
            }
        }

        is LongArray -> {
            array.forEachIndexed { index, any ->
                jsArray.setProperty(index, context.createJSNumber(any.toDouble()))
            }
        }

        is IntArray -> {
            array.forEachIndexed { index, any ->
                jsArray.setProperty(index, context.createJSNumber(any))
            }
        }

        is BooleanArray -> {
            array.forEachIndexed { index, any ->
                jsArray.setProperty(index, context.createJSBoolean(any))
            }
        }

        is DoubleArray -> {
            array.forEachIndexed { index, any ->
                jsArray.setProperty(index, context.createJSNumber(any))
            }
        }

        else -> {
            throw IllegalArgumentException("Unsupported type")
        }
    }
    return jsArray
}