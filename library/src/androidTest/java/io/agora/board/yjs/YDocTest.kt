package io.agora.board.yjs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * author : fenglibin
 * date : 2024/7/22
 * description :
 */
class YDocTest : YjsTestContext() {

    @Test
    fun destroy() {
        val ydoc = yjs.createDoc()

        var called = false
        val observer: YDocDestroyObserver = object : YDocDestroyObserver {
            override fun onDocDestroy(doc: YDoc) {
                called = true
            }
        }
        ydoc.addDestroyObserver(observer)

        ydoc.destroy()
        assertTrue(called)
    }

    /**
     * https://docs.yjs.dev/getting-started/working-with-shared-types#transactions
     * changes: Map({ number: { action: 'added' }, food: { action: 'updated', oldValue: 'pizza' } })
     */
    @Test
    fun transact() {
        val ydoc = yjs.createDoc()
        val ymap = ydoc.getMap("favorites")

        ymap.set("food", "pizza")

        var count = 0
        val observer = object : YMapObserver {
            override fun onChange(event: YMapEvent) {
                count++
                val changes = event.changes.keys
                val number = changes["number"]
                val food = changes["food"]
                assertEquals(YEvent.ACTION_ADD, number?.action)
                assertEquals(YEvent.ACTION_UPDATE, food?.action)
            }
        }
        ymap.addObserver(observer)

        ydoc.transact {
            ymap.set("food", "pencake")
            ymap.set("number", 31)
        }


        assertTrue(count == 1)
    }
}