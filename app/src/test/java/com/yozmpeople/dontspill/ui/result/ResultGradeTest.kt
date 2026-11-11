package com.yozmpeople.dontspill.ui.result

import org.junit.Assert.assertEquals
import org.junit.Test

class ResultGradeTest {

    @Test
    fun `시안 예시 점수는 시안 등급과 같다`() {
        assertEquals(ResultGrade.Perfect, ResultGrade.of(99))
        assertEquals(ResultGrade.Good, ResultGrade.of(75))
        assertEquals(ResultGrade.Bad, ResultGrade.of(68))
    }

    @Test
    fun `경계 점수는 높은 등급에 들어간다`() {
        assertEquals(ResultGrade.Perfect, ResultGrade.of(90))
        assertEquals(ResultGrade.Good, ResultGrade.of(89))
        assertEquals(ResultGrade.Good, ResultGrade.of(70))
        assertEquals(ResultGrade.Bad, ResultGrade.of(69))
    }
}
