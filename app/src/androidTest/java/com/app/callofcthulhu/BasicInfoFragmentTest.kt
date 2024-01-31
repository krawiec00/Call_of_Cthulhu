package com.app.callofcthulhu

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.callofcthulhu.view.fragments.BasicInfoFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasicInfoFragmentTest {

    @Test
    fun testEditTextUpdates() {
        // Uruchomienie fragmentu
        val scenario = launchFragmentInContainer<BasicInfoFragment>()

        // Symulacja wpisywania tekstu do EditText
        onView(withId(R.id.card_imie)).perform(typeText("Test Name"))

        // Sprawdzenie, czy tekst został poprawnie wpisany
        onView(withId(R.id.card_imie)).check(matches(withText("Test Name")))
    }
}
