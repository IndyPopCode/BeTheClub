package com.example.betheclub

import android.content.Intent
import android.widget.Button
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.junit.runners.JUnit4

/**
 * Instrumented unit tests for the [MainActivity].
 *
 * This class contains tests that verify the behavior of the [MainActivity],
 * specifically the `findClosestClub` method, in various scenarios.
 *
 * The tests use mocked dependencies for the database ([AppDatabase] and [GolfClubDao])
 * to isolate the [MainActivity] logic and ensure consistent test results.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MainActivityTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var golfClubDao: GolfClubDao
    private lateinit var appDatabase: AppDatabase

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        golfClubDao = mock(GolfClubDao::class.java)
        appDatabase = mock(AppDatabase::class.java)
        `when`(appDatabase.golfClubDao()).thenReturn(golfClubDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun findClosestClub_returnsCorrectClub() = runTest {
        // Given
        val club1 = GolfClub(id = 1, name = "Club 1", shots = listOf(100f, 105f, 95f))
        val club2 = GolfClub(id = 2, name = "Club 2", shots = listOf(150f, 155f, 145f))
        val club3 = GolfClub(id = 3, name = "Club 3", shots = listOf(200f, 205f, 195f))
        val clubs = listOf(club1, club2, club3)

        `when`(golfClubDao.getAllGolfClubs()).thenReturn(flowOf(clubs))

        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.onActivity { activity ->
            // Set up a mock LifecycleOwner for the activity
            val lifecycleOwner = mock(LifecycleOwner::class.java)
            val lifecycle = LifecycleRegistry(lifecycleOwner)
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            `when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

            // When
            val suggestedClub = activity.findClosestClub(152)

            // Then
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(club2, suggestedClub)
        }
        scenario.close()
    }

    @Test
    fun findClosestClub_returnsNullWhenNoClubIsCloseEnough() = runTest {
        // Given
        val club1 = GolfClub(id = 1, name = "Club 1", shots = listOf(100f, 105f, 95f))
        val club2 = GolfClub(id = 2, name = "Club 2", shots = listOf(150f, 155f, 145f))
        val club3 = GolfClub(id = 3, name = "Club 3", shots = listOf(200f, 205f, 195f))
        val clubs = listOf(club1, club2, club3)

        `when`(golfClubDao.getAllGolfClubs()).thenReturn(flowOf(clubs))

        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.onActivity { activity ->
            // Set up a mock LifecycleOwner for the activity
            val lifecycleOwner = mock(LifecycleOwner::class.java)
            val lifecycle = LifecycleRegistry(lifecycleOwner)
            lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            `when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)

            // When
            val suggestedClub = activity.findClosestClub(120)

            // Then
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(null, suggestedClub)
        }
        scenario.close()
    }
}