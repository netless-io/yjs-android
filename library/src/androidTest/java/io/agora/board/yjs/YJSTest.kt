package io.agora.board.yjs

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * author : fenglibin
 * date : 2024/7/20
 * description :
 */
class YJSTest : YjsTestContext() {

    @Test
    fun yjs_api_define() {
        val doc = yjs.createDoc()
        val map = doc.getMap()
        map.set("keyA", "valueA")

        val remoteDoc = yjs.createDoc()
        val remoteMap = remoteDoc.getMap()
        remoteMap.set("keyB", "valueB")

        val update = yjs.encodeStateAsUpdate(remoteDoc)
        yjs.applyUpdate(doc, update)

        val expected = """{"keyA":"valueA","keyB":"valueB"}"""
        val actual = map.toJSON()
        assertEquals(expected, actual)
    }
}