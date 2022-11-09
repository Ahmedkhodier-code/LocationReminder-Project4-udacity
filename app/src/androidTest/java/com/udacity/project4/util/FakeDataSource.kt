package com.udacity.project4.util


import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource(var tasks: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = true
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Error Reminder not found!")
        }
        tasks?.let { return Result.Success(ArrayList(it)) }
        return Result.Error("Reminders not found.")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        tasks?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        val result = tasks?.firstOrNull { it.id == id }
        result?.let {
            return Result.Success(it)
        }
        return Result.Error("Reminder Not Found")

    }

    override suspend fun deleteAllReminders() {
        tasks?.clear()
    }
}