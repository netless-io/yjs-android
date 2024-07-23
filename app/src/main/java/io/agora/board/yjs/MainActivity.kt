package io.agora.board.yjs

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.agora.board.yjs.YJS.Builder

class MainActivity : AppCompatActivity() {
    private lateinit var yjs: YJS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.test).setOnClickListener {
            val doc = yjs.createDoc()
            val map = doc.getMap()
            map.set("key", "123")
            Log.d("YJS", "key: ${map.get("key")}")
        }

        yjs = Builder(this).build()
    }
}