package io.agora.board.yjs

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * author : fenglibin
 * date : 2024/7/18
 * description :
 */
class YMapTest : YjsTestContext() {

    @Test
    fun ymap_api() {
        val doc = yjs.createDoc()
        val map = doc.getMap()
        map.set("foo", "123")
        val retS = map.getString("foo")
        map.set("bar", 456)
        val retI = map.getInt("bar")
        assertTrue(retS == "123")
        assertTrue(retI == 456)
    }

    @Test
    fun keys() {

        val doc = yjs.createDoc()
        val map = doc.getMap()
        map.set("keyA", "valueA")
        map.set("keyB", "valueB")
        val keys = map.keys()
        assertTrue(keys.size == 2)
        assertTrue(keys.contains("keyA"))
        assertTrue(keys.contains("keyB"))
    }

}