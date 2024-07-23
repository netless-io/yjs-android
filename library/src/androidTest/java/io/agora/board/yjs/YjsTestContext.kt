package io.agora.board.yjs

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before

abstract class YjsTestContext {
    lateinit var yjs: YJS

    @Before
    fun setup() {
        yjs = YJS.Builder(InstrumentationRegistry.getInstrumentation().context).build()
    }

    @After
    fun cleanup() {
        yjs.release()
    }
}
