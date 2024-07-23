package io.agora.board.yjs

import com.hippo.quickjs.android.JSArrayBuffer
import com.hippo.quickjs.android.JSBoolean
import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSFunction
import com.hippo.quickjs.android.JSNumber
import com.hippo.quickjs.android.JSObject
import com.hippo.quickjs.android.JSString
import com.hippo.quickjs.android.JSValue

/**
 * author : fenglibin
 * date : 2024/7/20
 * description :
 */
fun ByteArray.toJSUint8Array(context: JSContext): JSValue {
    val f = context.globalObject.getProperty("Uint8Array")
    val jsBuffer = context.createJSArrayBuffer(this, 0, this.size)
    return context.callConstructor(f, arrayOf(jsBuffer))
}

fun JSValue.toByteArray(): ByteArray {
    val jsBuffer = getKeyedValue("buffer").cast(JSArrayBuffer::class.java)
    return jsBuffer.toByteArray()
}

fun JSValue.getKeyedValue(key: String): JSValue {
    val obj = this.cast(JSObject::class.java)
    return obj.getProperty(key)
}

fun JSValue.getKeyedInt(key: String): Int {
    return getKeyedValue(key).cast(JSNumber::class.java).int
}

fun JSValue.getKeyedLong(key: String): Long {
    return getKeyedValue(key).cast(JSNumber::class.java).long
}

fun JSValue.getKeyedBool(key: String): Boolean {
    return getKeyedValue(key).cast(JSBoolean::class.java).boolean
}

fun JSValue.getKeyedFunction(key: String): JSFunction {
    return getKeyedValue(key).cast(JSFunction::class.java)
}

fun JSValue.getKeyedString(key: String): String {
    return getKeyedValue(key).cast(JSString::class.java).string
}

fun JSValue.getIndexedValue(index: Int): JSValue {
    val obj = this.cast(JSObject::class.java)
    return obj.getProperty(index)
}

fun JSValue.getIndexedInt(index: Int): Int {
    return getIndexedValue(index).cast(JSNumber::class.java).int
}

fun JSValue.getIndexedLong(index: Int): Long {
    return getIndexedValue(index).cast(JSNumber::class.java).long
}

fun JSValue.getIndexedBool(index: Int): Boolean {
    return getIndexedValue(index).cast(JSBoolean::class.java).boolean
}

fun JSValue.getIndexedFunction(index: Int): JSFunction {
    return getIndexedValue(index).cast(JSFunction::class.java)
}

fun JSValue.getIndexedString(index: Int): String {
    return getIndexedValue(index).cast(JSString::class.java).string
}

//fun JSValue.invokeMethod(key: String, arrayOf: Array<JSValue>): String {
//    return getKeyedValue(key).toString()
//}

typealias YJSObserver = JSValue

fun toJSONForTest(context: JSContext, value: JSValue): String {
    val toJSON = context.globalObject.getKeyedValue("JSON").getKeyedFunction("stringify")
    return context.callFunction(
        toJSON,
        context.createJSNull(),
        arrayOf(value)
    ).cast(JSString::class.java).string
}