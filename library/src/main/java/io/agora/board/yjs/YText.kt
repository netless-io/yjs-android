package io.agora.board.yjs

import com.hippo.quickjs.android.JSString
import com.hippo.quickjs.android.JSValue

/**
 * author : fenglibin
 * date : 2024/7/22
 * description :
 */
//public class YText: YAbscractType {
//
//    var length: UInt {
//        guard let result = jsValue.objectForKeyedSubscript("length") else {
//            fatalError("[DoYjs] YText.length fail")
//        }
//        return UInt(result.toInt32())
//    }
//
//    func insert(index: UInt, content: String, format: YPlainObject? = nil) {
//        guard let jsFormat = (format?.jsValue ?? JSValue(undefinedIn: jsValue.context)) else {
//            fatalError("[DoYjs] YText.insert fail")
//        }
//        jsValue.invokeMethod("insert", withArguments: [index, content, jsFormat])
//    }
//
//    func format(index: UInt, length: UInt, format: YPlainObject) {
//        jsValue.invokeMethod("format", withArguments: [index, length, format.jsValue])
//    }
//
//    func delete(index: UInt, length: UInt) {
//        jsValue.invokeMethod("delete", withArguments: [index, length])
//    }
//
//    func toString() -> String {
//        guard let result = jsValue.invokeMethod("toString", withArguments: []) else {
//            fatalError("[DoYjs] YText.toString fail")
//        }
//        return result.toString()
//    }
//
//    func clone() -> YText {
//        guard let jsResult = jsValue.invokeMethod("clone", withArguments: []) else {
//            fatalError("[DoYjs] YText.clone fail")
//        }
//        return YText(jsValue: jsResult)
//    }
//}

class YText(val jsText: JSValue) : YAbstractType(jsText) {
    val length: Int
        get() = jsText.getKeyedInt("length")

    val context = jsText.jsContext

    fun insert(index: Int, content: String, attributes: YTextAttributes? = null) {
        jsText.getKeyedFunction("insert").invoke(
            jsText,
            arrayOf(
                context.createJSNumber(index),
                context.createJSString(content),
                attributes?.toJSObject(context) ?: context.createJSUndefined()
            )
        )
    }

    fun format(index: Int, length: Int, attributes: YTextAttributes) {
        jsText.getKeyedFunction("format").invoke(
            jsText,
            arrayOf(
                context.createJSNumber(index),
                context.createJSNumber(length),
                attributes.toJSObject(context)
            )
        )
    }

    fun delete(index: Int, length: Int) {
        jsText.getKeyedFunction("delete").invoke(
            jsText,
            arrayOf(
                context.createJSNumber(index),
                context.createJSNumber(length)
            )
        )
    }

    fun toJSON(): String {
        return jsText.getKeyedFunction("toJSON").invoke(jsText, arrayOf()).cast(JSString::class.java).string
    }

    fun clone(): YText {
        return YText(jsText.getKeyedFunction("clone").invoke(jsText, arrayOf()))
    }
}