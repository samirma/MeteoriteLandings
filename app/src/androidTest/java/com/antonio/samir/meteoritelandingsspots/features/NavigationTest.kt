package com.antonio.samir.meteoritelandingsspots.features

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.TAG_BACK_BUTTON
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.TAG_DETAIL_MAP
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.TAG_SEARCH_BUTTON
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.TAG_SEARCH_FIELD
import com.antonio.samir.meteoritelandingsspots.features.list.TAG_METEORITE_LIST
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression test for the navigation flow, the part most likely to break silently: the app went
 * through Nav2 -> Nav3 -> per-entry ViewModel scoping with nothing guarding it.
 *
 * Runs against the real application graph rather than a Hilt test component, so it also covers
 * DI wiring, the Room migration and the prepopulated database.
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun awaitTag(tag: String, timeoutMillis: Long = 15_000) {
        composeRule.waitUntil(timeoutMillis) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun listOpensDetailAndBackReturnsToTheList() {
        awaitTag(TAG_METEORITE_LIST)
        composeRule.onNodeWithTag(TAG_METEORITE_LIST).assertIsDisplayed()

        composeRule.onNodeWithTag(TAG_METEORITE_LIST).onChildren().onFirst().performClick()

        awaitTag(TAG_DETAIL_MAP, timeoutMillis = 20_000)
        composeRule.onNodeWithTag(TAG_BACK_BUTTON).performClick()

        awaitTag(TAG_METEORITE_LIST)
        composeRule.onNodeWithTag(TAG_METEORITE_LIST).assertIsDisplayed()
    }

    @Test
    fun searchOpensFromTheTopBar() {
        awaitTag(TAG_METEORITE_LIST)

        composeRule.onNodeWithTag(TAG_SEARCH_BUTTON).performClick()

        awaitTag(TAG_SEARCH_FIELD, timeoutMillis = 5_000)
        composeRule.onNodeWithTag(TAG_SEARCH_FIELD).assertIsDisplayed()
    }
}
