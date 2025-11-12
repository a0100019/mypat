package com.a0100019.mypat.domain.worker

import java.io.*
import java.util.*
import kotlin.math.min

fun main() {
    val br = BufferedReader(InputStreamReader(System.`in`))
    val st = StringTokenizer(br.readLine())
    var n = st.nextToken().toInt()
    val m = st.nextToken().toInt()
    var six = 1000000
    var one = 1000000

    repeat(m) { // 입력 줄 수를 알고 있다면 3번 반복
        val stt = StringTokenizer(br.readLine())

        val sixCandi = stt.nextToken().toInt()
        val oneCandi = stt.nextToken().toInt()

        if(sixCandi < six) {
            six = sixCandi
        }

        if(oneCandi < one) {
            one = oneCandi
        }

    }

    var answer = 0

    while(true) {

        if(n >= 6) {
            n -= 6
            answer += six
        } else {
            answer += min(six, n*one)
            break
        }
    }

    println(answer)

}