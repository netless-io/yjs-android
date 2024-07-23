package io.agora.board.yjs

import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSObject

/**
 * author : fenglibin
 * date : 2024/7/22
 * description :
 */
data class YTextAttributes(
    val bold: Boolean? = false,
    val color: String? = null,
    val background: String? = null,
)

fun YTextAttributes.toJSObject(context: JSContext): JSObject {
    return context.createJSObject().apply {
        bold?.let { setProperty("bold", context.createJSBoolean(it)) }
        color?.let { setProperty("color", context.createJSString(it)) }
        background?.let { setProperty("background", context.createJSString(it)) }
    }
}