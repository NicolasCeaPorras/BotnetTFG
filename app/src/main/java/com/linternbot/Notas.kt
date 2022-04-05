package com.linternbot

import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.naishadhparmar.zcustomcalendar.CustomCalendar
import org.naishadhparmar.zcustomcalendar.OnDateSelectedListener
import org.naishadhparmar.zcustomcalendar.Property
import java.util.*

// Fuente de la clase: https://www.geeksforgeeks.org/how-to-implement-custom-calendar-in-android/
class Notas : AppCompatActivity() {
    // Initialize variable
    var customCalendar: CustomCalendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

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
            textoNota.setOnClickListener(){
                val textoCortado = textoNota.text.substring(0,("No existe ninguna nota para el día ").length)
                if(textoCortado.equals("No existe ninguna nota para el día ")){
                    textoNota.setText("")
                }
            }
        }
    }

    fun pulsaFecha(date: String, db: FirebaseFirestore) {
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
}
