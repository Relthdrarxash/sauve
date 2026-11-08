package fr.sauve.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

/**
 * Widget SOS, sans donnée ni mise à jour : deux gestes vitaux ouverts directement,
 * et le numéro d'urgence (15 en France, 112 ailleurs) prêt à composer.
 */
class SosWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val views = RemoteViews(context.packageName, R.layout.widget_sos).apply {
            setOnClickPendingIntent(R.id.widget_cardiac, openScreen(context, Screen.CARDIAC, 1))
            setOnClickPendingIntent(R.id.widget_choking, openScreen(context, Screen.CHOKING, 2))
            val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + context.getString(R.string.widget_call_number)))
            setOnClickPendingIntent(R.id.widget_call, PendingIntent.getActivity(context, 3, dial, PendingIntent.FLAG_IMMUTABLE))
            setOnClickPendingIntent(R.id.widget_root, openScreen(context, Screen.HOME, 0))
        }
        appWidgetIds.forEach { appWidgetManager.updateAppWidget(it, views) }
    }

    private fun openScreen(context: Context, screen: Screen, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
            .putExtra(Screen.EXTRA, screen.name)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
