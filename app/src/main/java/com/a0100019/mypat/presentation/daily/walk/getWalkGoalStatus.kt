package com.a0100019.mypat.presentation.daily.walk

fun getWalkGoalStatus(totalSteps: Int, goals: List<WalkGoal>): WalkGoalStatus {
    val distanceKm = totalSteps * 0.65 / 1000.0

    var accumulated = 0.0

    for (goal in goals) {
        val goalDistance = goal.distanceKm

        // 아직 도달 못 한 목표
        if (distanceKm < accumulated + goalDistance) {
            val progress = (distanceKm - accumulated) / goalDistance
            val remain = (accumulated + goalDistance) - distanceKm

            return WalkGoalStatus(
                currentGoal = goal,
                progress = progress.coerceIn(0.0, 1.0),
                remainKm = remain
            )
        }

        accumulated += goalDistance
    }

    // 마지막 목표도 도달한 경우
    return WalkGoalStatus(
        currentGoal = goals.last(),
        progress = 1.0,
        remainKm = 0.0
    )
}

data class WalkGoalStatus(
    val currentGoal: WalkGoal,
    val progress: Double,
    val remainKm: Double
)

data class WalkGoal(
    val name: String,
    val distanceKm: Double
)

val walkGoals = listOf(
    WalkGoal("롯데타워 높이", 0.555),
    WalkGoal("지리산", 1.915),
    WalkGoal("후지산", 3.776),
    WalkGoal("토네이도 폭넓이", 4.2),
    WalkGoal("남산 둘레길", 5.0),
    WalkGoal("에베레스트산", 8.848),
    WalkGoal("대류권 높이", 11.0),
    WalkGoal("그랜드캐년", 13.0),
    WalkGoal("하프 마라톤", 21.097),
    WalkGoal("서울 지하철 4호선", 31.1),
    WalkGoal("마라톤", 42.195),
    WalkGoal("베를린 장벽", 43.0),
    WalkGoal("성층권 높이", 50.0),
    WalkGoal("울릉도 ➜ 독도", 87.4),
    WalkGoal("우주 기준선-카르마 라인", 100.0),
    WalkGoal("서울 한바퀴", 156.5),
    WalkGoal("서울 ➜ 부산", 325.0),
    WalkGoal("대한민국 한바퀴", 4500.0),
    WalkGoal("세계 일주", 40075.0)
)