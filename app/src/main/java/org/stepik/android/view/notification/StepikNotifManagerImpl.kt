package org.stepik.android.view.notification

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import org.stepic.droid.receivers.AlarmReceiver
import org.stepic.droid.util.scheduleCompat
import java.util.concurrent.ThreadPoolExecutor
import javax.inject.Inject

class StepikNotifManagerImpl
@Inject constructor(
        private val context: Context,
        private val threadPoolExecutor: ThreadPoolExecutor
) : StepikNotifManager {

    private val alarmManager: AlarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private val notificationManager: NotificationManager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun scheduleNotification(id: String, millis: Long) {
        threadPoolExecutor.execute {
            val intent = AlarmReceiver
                    .createIntent(context, id)

            val pendingIntent = PendingIntent
                    .getBroadcast(context, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.cancel(pendingIntent)

            alarmManager.scheduleCompat(millis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent)
        }
    }

    override fun rescheduleActiveNotifications() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNotification(id: Long, notification: Notification) {
        notificationManager.notify(id.toInt(), notification)
    }
}