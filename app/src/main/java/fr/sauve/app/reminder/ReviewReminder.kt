package fr.sauve.app.reminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import fr.sauve.app.MainActivity
import fr.sauve.app.R
import fr.sauve.app.Screen
import java.util.concurrent.TimeUnit

/**
 * Rappel de révision facultatif, tous les 3 mois : les gestes s'oublient vite sans pratique.
 * Il ouvre directement l'entraînement au massage cardiaque. Désactivé tant qu'on ne l'active pas.
 */
object ReviewReminder {

    private const val WORK_NAME = "revision"
    const val CHANNEL_ID = "revision"
    private const val PREFS = "sauve"
    private const val KEY_ENABLED = "review_reminder"
    const val INTERVAL_DAYS = 90L

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ENABLED, enabled).apply()
        val manager = WorkManager.getInstance(context)
        if (enabled) {
            val request = PeriodicWorkRequestBuilder<ReviewWorker>(INTERVAL_DAYS, TimeUnit.DAYS)
                .setInitialDelay(INTERVAL_DAYS, TimeUnit.DAYS)
                .build()
            manager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        } else {
            manager.cancelUniqueWork(WORK_NAME)
        }
    }

    fun canNotify(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
}

class ReviewWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    @SuppressLint("MissingPermission") // Vérifiée par canNotify.
    override suspend fun doWork(): Result {
        val context = applicationContext
        if (!ReviewReminder.isEnabled(context) || !ReviewReminder.canNotify(context)) return Result.success()
        val manager = context.getSystemService(NotificationManager::class.java) ?: return Result.success()
        manager.createNotificationChannel(
            NotificationChannel(ReviewReminder.CHANNEL_ID, context.getString(R.string.review_channel), NotificationManager.IMPORTANCE_LOW),
        )
        val open = Intent(context, MainActivity::class.java)
            .putExtra(Screen.EXTRA, Screen.TRAINING.name)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pending = PendingIntent.getActivity(context, 0, open, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, ReviewReminder.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.review_title))
            .setContentText(context.getString(R.string.review_text))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(1, notification)
        return Result.success()
    }
}
