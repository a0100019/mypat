package com.a0100019.mypat.domain

import java.util.*
import java.io.*
import kotlin.math.max

fun main() {
    val br = BufferedReader(InputStreamReader(System.`in`))
    val n = br.readLine().toInt()

    val list = IntArray(n)

    repeat(n) {
        list[it] = br.readLine().toInt()
    }

    list.sort()

    repeat(n) {
        println(list[it])

    }

}