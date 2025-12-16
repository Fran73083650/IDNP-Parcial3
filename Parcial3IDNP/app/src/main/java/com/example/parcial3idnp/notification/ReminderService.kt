package com.example.parcial3idnp.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.parcial3idnp.R

class ReminderService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Crear notificación de foreground service
        val notification = NotificationCompat.Builder(this, "activity_reminders")
            .setContentTitle("Servicio de Recordatorios")
            .setContentText("Monitoreando actividades pendientes")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        // Aquí iría la lógica de verificación de recordatorios
        // Por ahora solo es una implementación básica

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}