package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.data.dto.Result.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var reminderDao: RemindersDao
    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    val list = listOf<ReminderDTO>(
        ReminderDTO("title1", "description", "location", 0.0, 0.0),
        ReminderDTO(
            "title", "description", "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title2", "description", "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title3", "description", "location",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        )
    )
    private val reminder1 = list[0]
    private val reminder2 = list[1]
    private val reminder3 = list[2]


    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).build()
        reminderDao = database.reminderDao()
        remindersLocalRepository = RemindersLocalRepository(reminderDao, Dispatchers.Unconfined)
    }

    @After
    fun release() {
        database.close()
    }

    @Test
    fun addAndGetAllReminders() = runBlockingTest {
        //Given - Database have some Reminders
        reminderDao.saveReminder(reminder1)
        reminderDao.saveReminder(reminder2)
        reminderDao.saveReminder(reminder3)

        //When - Getting reminders using repo
        val retrievedReminders = remindersLocalRepository.getReminders() as Success

        //Then - check the size of retrieved reminders list
        Assert.assertThat(retrievedReminders.data.size, `is`(3))
    }

    @Test
    fun saveSpecificReminderAndGetItById() = runBlockingTest {
        // save reminder using the repo
        remindersLocalRepository.saveReminder(reminder1)

        // Getting the reminder using repo
        val retrievedReminder = remindersLocalRepository.getReminder(reminder1.id) as Success

        // check it's the same saved reminder
        Assert.assertThat(retrievedReminder.data.id, `is`(reminder1.id))
        Assert.assertThat(retrievedReminder.data.title, `is`(reminder1.title))

        // check it's not the same saved reminder
        Assert.assertThat(retrievedReminder.data.title, `is`(not(reminder2.title)))
    }

    // Not Found Reminder Scenario
    @Test
    fun getNonExistReminderById_returnError() = runBlockingTest {
        // Getting non exist reminder by wrong id
        val retrievedReminder = remindersLocalRepository.getReminder("Test") as Error

        // check its error message equal to the not found message returned from the local repo
        Assert.assertThat(retrievedReminder.message, `is`("Reminder not found!"))
    }


    @Test
    fun deleteAllReminders() = runBlockingTest {
        //Given - Database have some Reminders
        reminderDao.saveReminder(reminder1)
        reminderDao.saveReminder(reminder2)
        reminderDao.saveReminder(reminder3)
        // Check data existence
        var retrievedReminders = remindersLocalRepository.getReminders() as Success
        Assert.assertThat(retrievedReminders.data.size, `is`(3))


        // delete all reminders using repo
        remindersLocalRepository.deleteAllReminders()

        // check that the database is empty
        retrievedReminders = remindersLocalRepository.getReminders() as Success
        Assert.assertThat(retrievedReminders.data, `is`(emptyList()))
        Assert.assertThat(retrievedReminders.data.size, `is`(0))
    }

}