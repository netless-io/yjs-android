package io.agora.board.yjs

/**
 * author : fenglibin
 * date : 2024/7/22
 * description :
 */

interface YDocUpdateObserver {
    fun onDocUpdate(update: ByteArray, origin: Any, doc: YDoc, transaction: YTransaction)
}

interface YDocDestroyObserver {
    fun onDocDestroy(doc: YDoc)
}

interface YDocEventHandler {
    fun onBeforeTransaction(transaction: YTransaction, doc: YDoc) {}

    fun onBeforeObserverCalls(transaction: YTransaction, doc: YDoc) {}

    fun onAfterTransaction(transaction: YTransaction, doc: YDoc) {}
}