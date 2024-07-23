package io.agora.board.yjs

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * author : fenglibin
 * date : 2024/7/20
 * description :
 */
class YArrayTest : YjsTestContext() {
    // https://docs.yjs.dev/getting-started/working-with-shared-types
    @Test
    fun yarray_api() {
        val ydoc = yjs.createDoc()
        val yarray = ydoc.getArray("my array")
        val observer = object : YArrayObserver {
            override fun onChange(event: YArrayEvent) {
            }
        }
        yarray.addObserver(observer)
        val deepObserver = object : YArrayObserver {
            override fun onChange(event: YArrayEvent) {

            }
        }
        yarray.addDeepChangeObserver(deepObserver)
        yarray.insert(0, arrayOf("some content"))
        yarray.toJSON()

        // TODO 官网例子中使用UInt8Array，这里传递的是普通数组对象
        yarray.insert(0, arrayOf(1, mapOf("bool" to true), intArrayOf(1, 2, 3)))
        yarray.toJSON()

        val subArray = yjs.createArray()
        yarray.insert(0, arrayOf(subArray))
        subArray.insert(0, arrayOf("nope")) // [observer not called]
        subArray.insert(0, arrayOf("this works"))
        // TODO 会插入导致容器异常，当前JS插入报错但会污染数据
        // assertThrows(Exception::class.java) {
        //     yarray.insert(0, arrayOf(subArray))
        // }
        val expected = """[["this works","nope"],1,{"bool":true},[1,2,3],"some content"]"""
        val actual = yarray.toJSON()
        assertEquals(expected, actual)
    }

    @Test
    fun add_observer() {
        var changed = false

        val ydoc = yjs.createDoc()
        val yarray = ydoc.getArray("my array")
        val observer = object : YArrayObserver {
            override fun onChange(event: YArrayEvent) {
                changed = true
            }
        }

        yarray.addObserver(observer)
        yarray.insert(0, arrayOf("some content"))

        // val eventCaptor = ArgumentCaptor.forClass(YArrayEvent::class.java)
        assert(changed)
    }

    /**
     * from iOS test case
     *
     * TODO 实现 array 的迭代器
     */
    @Test
    fun array_as_sequence() {
        val ydoc = yjs.createDoc()
        val yarray = ydoc.getArray("my array")
        yarray.insert(0, arrayOf(1, 2, 3, 4, 5))

        var sum = 0
        for (i in 0 until yarray.length) {
            sum += (yarray.get(i) as Double).toInt()
        }

        assertEquals(15, sum)
    }

    /**
     * from iOS test case
     */
    @Test
    fun array_manipulator() {
        val ydoc = yjs.createDoc()
        val yarray = ydoc.getArray("manipulator")

        var modified = false
        val observer = object : YArrayObserver {
            override fun onChange(event: YArrayEvent) {
                modified = true
            }
        }
        yarray.addObserver(observer)

        yarray.insert(0, arrayOf(1, 2))
        assertEquals(yarray.length, 2)
        assertEquals(modified, true)

        modified = false
        yarray.removeObserver(observer)
        yarray.delete(0, 1)
        assertEquals(yarray.length, 1)
        assertEquals(modified, false)

        yarray.push(arrayOf(123))
        assertEquals(yarray.length, 2)

        yarray.unshift(arrayOf(1))
        assertEquals(yarray.length, 3)

        val portion = yarray.slice(1, 2)
        assertEquals(portion.length, 1)

        val expected = """[1,2,123]"""
        val actual = yarray.toJSON()
        assertEquals(expected, actual)
    }
}