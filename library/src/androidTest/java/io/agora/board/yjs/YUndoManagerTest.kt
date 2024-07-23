package io.agora.board.yjs

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * author : fenglibin
 * date : 2024/7/20
 * description :
 */
class YUndoManagerTest : YjsTestContext() {
    @Test
    fun undo_manager_api() {
        val doc = yjs.createDoc()
        val map = doc.getMap()
        val undoManager = yjs.createUndoManager(map)

        map.set("a", 1)
        assertEquals(map.toJSON(), "{\"a\":1}")

        undoManager.undo()
        assertEquals(map.toJSON(), "{}")

        undoManager.redo()
        assertEquals(map.toJSON(), "{\"a\":1}")
    }
}