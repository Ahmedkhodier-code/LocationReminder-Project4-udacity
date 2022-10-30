package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO

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
                return Result.Error("Location reminders cannot retrieve due to exception occurs")
            }
            return Result.Success(tasks)
        } catch (e: Exception) {
            return Result.Error(e.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        try {
            val result = tasks.firstOrNull { it.id == id }
            if (shouldReturnError) {
                throw Exception("Location reminder with $id cannot retrieved due to exception occurs")
            } else if (result == null) {
                Result.Error("Reminder not found!")
            } else {
                Result.Success(result)
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }

    override suspend fun deleteAllReminders() {
        tasks.clear()
    }
}