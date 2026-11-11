package com.yozmpeople.dontspill.domain.score

// 감점 항목의 진입 조건 판정 결과. 값을 몰라서 판단할 수 없으면 UNKNOWN이고, NOT_MET으로 바꾸지 않는다.
enum class ConditionState {
    MET,
    NOT_MET,
    UNKNOWN
}

internal fun Boolean?.toConditionState(): ConditionState = when (this) {
    true -> ConditionState.MET
    false -> ConditionState.NOT_MET
    null -> ConditionState.UNKNOWN
}

// 값이 없으면 UNKNOWN, 있으면 predicate 결과
internal inline fun <T : Any> T?.judge(predicate: (T) -> Boolean): ConditionState =
    this?.let { predicate(it).toConditionState() } ?: ConditionState.UNKNOWN
