package com.example.pokemon

import junit.framework.TestCase
import org.junit.Assert

fun assertSameClass(first: Any?, second: Any?) {
    Assert.assertEquals(
        first!!.javaClass.name,
        second!!.javaClass.name
    )
}

inline fun <reified T : Any> assertInstanceOf(any: Any?) {
    if (any !is T) {
        TestCase.fail("Expected instance of ${any!!.javaClass.simpleName}, but found ${T::class.simpleName}")
    }
}