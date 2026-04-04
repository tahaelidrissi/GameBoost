package com.gamecoach.ui.player

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.gamecoach.model.Coach
import com.gamecoach.model.CoachStatus
import org.junit.Rule
import org.junit.Test

class CoachListScreenTest {

    // Regle necessaire pour tester les interfaces Jetpack Compose
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun coachCardAfficheLesInformationsDuCoachCorrectement() {
        val mockCoach = Coach(
            id = "1", 
            username = "ProBoost", 
            game = "Valorant", 
            rank = "Radiant",
            hourlyRate = 35.0, 
            status = CoachStatus.APPROVED,
            rating = 4.8,
            totalReviews = 42
        )

        // On injecte le composant de carte stateless
        composeTestRule.setContent {
            CoachCard(
                coach = mockCoach,
                onClick = {}
            )
        }

        // Verification des textes affiches a l'ecran
        composeTestRule.onNodeWithText("ProBoost").assertIsDisplayed()
        composeTestRule.onNodeWithText("Valorant").assertIsDisplayed()
        composeTestRule.onNodeWithText("Radiant").assertIsDisplayed()
        composeTestRule.onNodeWithText("35€/h").assertIsDisplayed()
    }

    @Test
    fun coachCardAppelleLeCallbackAuClic() {
        var clicked = false
        val mockCoach = Coach(
            id = "42", 
            username = "TestCoach", 
            game = "League of Legends",
            rank = "Challenger",
            status = CoachStatus.APPROVED
        )

        composeTestRule.setContent {
            CoachCard(
                coach = mockCoach,
                onClick = { clicked = true }
            )
        }

        // Simule un clic sur la carte
        composeTestRule.onNodeWithText("TestCoach").performClick()
        
        // Assert
        assert(clicked) { "Le callback onClick n'a pas ete appele" }
    }
}
