package com.linternbot

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        val button = findViewById<ImageView>(R.id.boton1)
        val button2 = findViewById<ImageView>(R.id.boton2)
        button2.visibility = INVISIBLE
        button.setOnClickListener { // LA LINTERNA ESTA ENCENDIDA
            if(button.visibility.equals(VISIBLE)){
                button.visibility = INVISIBLE
                button2.visibility = VISIBLE

                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = camManager.cameraIdList[0] // usualmente la camara delantera esta en la posicion 0
                try {
                    camManager.setTorchMode(cameraId, true)
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
