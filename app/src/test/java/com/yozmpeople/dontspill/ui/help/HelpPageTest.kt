package com.yozmpeople.dontspill.ui.help

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HelpPageTest {

    @Test
    fun `페이지 번호는 1부터 선언 순서대로다`() {
        assertEquals(listOf(1, 2, 3), HelpPage.entries.map { it.number })
    }

    @Test
    fun `다음 페이지는 선언 순서의 다음이다`() {
        assertEquals(HelpPage.Result, HelpPage.Skin.next())
        assertEquals(HelpPage.Leaderboard, HelpPage.Result.next())
    }

    @Test
    fun `이전 페이지는 선언 순서의 이전이다`() {
        assertEquals(HelpPage.Result, HelpPage.Leaderboard.previous())
        assertEquals(HelpPage.Skin, HelpPage.Result.previous())
    }

    @Test
    fun `첫 페이지의 이전과 마지막 페이지의 다음은 없다`() {
        assertNull(HelpPage.Skin.previous())
        assertNull(HelpPage.Leaderboard.next())
    }
}
