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
    WalkGoal("에펠탑", 0.324),
    WalkGoal("롯데타워", 0.555),
    WalkGoal("지리산", 1.915),
    WalkGoal("후지산", 3.776),
    WalkGoal("서울 ➜ 부산", 325.0),            // 325km
    WalkGoal("한국縦断", 500.0),
    WalkGoal("세계 일주", 40075.0)
)

