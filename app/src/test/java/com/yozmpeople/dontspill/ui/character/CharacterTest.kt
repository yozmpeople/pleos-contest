package com.yozmpeople.dontspill.ui.character

import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterTest {

    @Test
    fun `다음 캐릭터는 선언 순서의 다음이다`() {
        assertEquals(Character.Tofu, Character.WaterCup.next())
        assertEquals(Character.Sunfish, Character.Tofu.next())
        assertEquals(Character.Laika, Character.Sunfish.next())
    }

    @Test
    fun `마지막 캐릭터의 다음은 처음 캐릭터다`() {
        assertEquals(Character.WaterCup, Character.Laika.next())
    }

    @Test
    fun `이전 캐릭터는 선언 순서의 이전이다`() {
        assertEquals(Character.Sunfish, Character.Laika.previous())
        assertEquals(Character.Tofu, Character.Sunfish.previous())
        assertEquals(Character.WaterCup, Character.Tofu.previous())
    }

    @Test
    fun `처음 캐릭터의 이전은 마지막 캐릭터다`() {
        assertEquals(Character.Laika, Character.WaterCup.previous())
    }
}
