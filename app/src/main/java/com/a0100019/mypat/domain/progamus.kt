package com.a0100019.mypat.domain

// 1. 프로그래머스에서 제공한 클래스 그대로 복사
class Solution {
    fun solution(participant: Array<String>, completion: Array<String>): String {
        val map = mutableMapOf<String, Int>()

        for (p in participant) {
            map[p] = map.getOrDefault(p, 0) + 1
        }

        for (c in completion) {
            map[c] = map[c]!! - 1
        }

        return map.filter { it.value > 0 }.keys.first()
    }
}

// 2. 안드로이드 스튜디오에서 실행해보기 위한 main 함수
fun main() {
    val sol = Solution()

    // 테스트 케이스 직접 입력
    val part = arrayOf("mislav", "stanko", "mislav", "ana")
    val comp = arrayOf("stanko", "ana", "mislav")

    // 결과 출력
    val result = sol.solution(part, comp)
    println("결과: $result") // 출력: 결과: mislav
}