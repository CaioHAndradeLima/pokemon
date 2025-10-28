package com.example.pokemon.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope

import kotlinx.coroutines.*
import kotlinx.coroutines.test.*

class FlowTestCollector<T>(
    val items: MutableList<T> = mutableListOf()
) {
    val first get() = items.first()
    val last get() = items.last()
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend inline fun <T> Flow<T>.testIn(
    scope: TestScope,
    assert: (list: FlowTestCollector<T>) -> Unit
) {
    val flow = FlowTestCollector<T>()
    val job = scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
        scope.advanceUntilIdle()
        collect { value ->
            flow.items.add(value)
        }
    }

    assert(flow)
    job.cancelAndJoin()
}
