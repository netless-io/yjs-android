package io.agora.board.yjs

import com.hippo.quickjs.android.JSArray
import com.hippo.quickjs.android.JSContext
import com.hippo.quickjs.android.JSValue

/**
 * author : fenglibin
 * date : 2024/7/18
 * description :
 */
open class YEvent(private val jsValue: JSValue) {
    companion object {
        const val ACTION_ADD = "add"
        const val ACTION_UPDATE = "update"
        const val ACTION_DELETE = "delete"
    }

    val target: Any // YAbstractType
        get() = jsValue.getKeyedValue("target")

    val currentTarget: Any // YAbstractType
        get() = jsValue.getKeyedValue("currentTarget")

    val origin: Any
        get() = jsValue.getKeyedValue("origin")

    val transaction: YTransaction
        get() = YTransaction(jsValue.getKeyedValue("transaction"))

    val changes: Changes
        get() = Changes(jsValue.getKeyedValue("changes"))

    val path: List<Any>
        get() = listOf()

    data class Changes(val jsValue: JSValue) {
        val keys: Map<String, KeyChange>
            get() = getKeysMap()

        private fun getKeysMap(): Map<String, KeyChange> {
            val context = jsValue.jsContext
            // obj is event.changes.keys, a map
            val obj = jsValue.getKeyedValue("keys")
            val jsKeys = obj.getKeyedFunction("keys").invoke(obj, emptyArray())
            val jsKeysArray = convertToArray(context, jsKeys)

            return createKeyChangeMap(obj, jsKeysArray)
        }

        private fun convertToArray(context: JSContext, jsKeys: JSValue): JSArray {
            val jsArray = context.globalObject.getKeyedValue("Array").getKeyedFunction("from")
            return context.callFunction(jsArray, context.createJSNull(), arrayOf(jsKeys)).cast(JSArray::class.java)
        }

        private fun createKeyChangeMap(value: JSValue, jsKeysArray: JSArray): Map<String, KeyChange> {
            val context = value.jsContext
            val ret = mutableMapOf<String, KeyChange>()
            for (i in 0 until jsKeysArray.length) {
                val key = jsKeysArray.getIndexedString(i)
                val mapGetFunc = value.getKeyedFunction("get")
                val keyChange = KeyChange(context.callFunction(mapGetFunc, value, arrayOf(context.createJSString(key))))
                ret[key] = keyChange
            }
            return ret
        }
    }

    data class KeyChange(val jsValue: JSValue) {
        // "add", "update", "delete"
        val action: String
            get() = jsValue.getKeyedString("action")
        val oldValue: Any
            get() = jsValue.getKeyedValue("oldValue")
    }
}