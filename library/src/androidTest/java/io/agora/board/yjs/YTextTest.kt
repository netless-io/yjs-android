package io.agora.board.yjs

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * author : fenglibin
 * date : 2024/7/22
 * description :
 */
class YTextTest : YjsTestContext() {
    // https://docs.yjs.dev/api/shared-types/y.text
    @Test
    fun ytest_api() {
        val ydoc = yjs.createDoc()
        val ytext = ydoc.getText("text")

        val ytestNested = yjs.createText()
        ydoc.getMap("another shared structure").set("my nested text", ytestNested)

        ytext.insert(0, "abc")
        ytext.format(1, 2, YTextAttributes(bold = true))
        assertEquals(ytext.toJSON(), "abc")

        ytext.delete(1, 2)
        assertEquals(ytext.toJSON(), "a")
    }
}