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
        try {
            if (shouldReturnError) {
                return Result.Error("No reminders found")
            }
            return Result.Success(tasks)
        } catch (e: Exception) {
            return Result.Error(e.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        try {
            if (shouldReturnError) {
                return Result.Error("There is Error")
            }
            val result = tasks.find { it.id == id }
            result?.let {
                return Result.Success(it)
            }
            return Result.Error("Reminder Not found")
        } catch (e: Exception) {
            return Result.Error(e.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        tasks.clear()
    }
}