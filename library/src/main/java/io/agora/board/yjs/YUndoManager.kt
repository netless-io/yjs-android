package io.agora.board.yjs

import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSValue

/**
 * author : fenglibin
 * date : 2024/7/20
 * description :
 */
class YUndoManager(private val jsValue: JSValue, private val context: JSContext) {
    fun undo() {
        val f = jsValue.getKeyedFunction("undo")
        context.callFunction(f, jsValue, arrayOf())
    }

    fun redo() {
        val f = jsValue.getKeyedFunction("redo")
        context.callFunction(f, jsValue, arrayOf())
    }
}