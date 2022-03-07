package com.linternbot

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class UploadWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val calendar: Calendar = Calendar.getInstance()
        val hour24hrs: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val hour12hrs: Int = calendar.get(Calendar.HOUR)
        val minutes: Int = calendar.get(Calendar.MINUTE)
        val seconds: Int = calendar.get(Calendar.SECOND)
        // Do the work here--in this case, upload the images.
        Log.d("SCHEDULE","Se ejecuta la tarea programada a las "+  + hour24hrs + ":" + minutes +":"+ seconds)

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
