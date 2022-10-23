package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
//class FakeDataSource(var tasks: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

class FakeDataSource() : ReminderDataSource {
    //    DONE: Create a fake data source to act as a double to the real data source
    private var tasks = mutableListOf<ReminderDTO>()

    private var shouldReturnError = false

    fun setShouldReturnError(shouldReturn: Boolean) {
        shouldReturnError = shouldReturn
    }

    fun setTasks(tasks: MutableList<ReminderDTO>) {
        this.tasks = tasks
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError) {
            Result.Error("No reminders found")
        } else {
            Result.Success(tasks)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (shouldReturnError) {
            Result.Error("There is Error")
        } else {
            val reminder = tasks.find { it.id == id }
            if (reminder == null) {
                Result.Error("Reminder Not found")
            } else {
                Result.Success(reminder)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        tasks.clear()
    }
}