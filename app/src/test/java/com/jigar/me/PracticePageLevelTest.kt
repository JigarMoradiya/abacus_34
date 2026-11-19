package com.jigar.me

import com.jigar.me.ui.view.home.screens.abacus_practice.set_list.viewmodels.practicePageLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PracticePageLevelTest {
    @Test fun levelNamesGiveTheirNumber() {
        assertEquals(1, practicePageLevel("level1"))
        assertEquals(8, practicePageLevel("level8"))
        assertEquals(10, practicePageLevel("level10"))
    }

    @Test fun otherNamesGiveNoNumber() {
        assertNull(practicePageLevel("freelevel"))
        assertNull(practicePageLevel("level0"))
        assertNull(practicePageLevel("level11"))
        assertNull(practicePageLevel("Level 2"))
        assertNull(practicePageLevel(null))
    }
}
