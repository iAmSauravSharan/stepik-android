package org.stepik.android.view.notification.delegate

import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import org.stepic.droid.model.CourseListType
import org.stepic.droid.notifications.model.RetentionNotificationType
import org.stepic.droid.preferences.SharedPreferenceHelper
import org.stepic.droid.storage.operations.DatabaseFacade
import org.stepic.droid.ui.activities.SplashActivity
import org.stepic.droid.util.AppConstants
import org.stepic.droid.util.DateTimeHelper
import org.stepik.android.view.notification.NotificationDelegate
import org.stepik.android.view.notification.StepikNotifManager
import org.stepik.android.view.notification.helpers.NotificationHelper
import javax.inject.Inject

class RetentionDelegate
@Inject constructor(
    val context: Context,
    val sharedPreferenceHelper: SharedPreferenceHelper,
    val databaseFacade: DatabaseFacade,
    val notificationHelper: NotificationHelper,
    stepikNotifManager: StepikNotifManager
) : NotificationDelegate("show_retention_notification", stepikNotifManager) {
    companion object {
        private const val RETENTION_NOTIFICATION_ID = 4432L
    }

    override fun onNeedShowNotification() {
        val lastSessionTimestamp = sharedPreferenceHelper.lastSessionTimestamp
        val now = DateTimeHelper.nowUtc()

        if (sharedPreferenceHelper.authResponseFromStore == null ||
            sharedPreferenceHelper.isStreakNotificationEnabled ||
            databaseFacade.getAllCourses(CourseListType.ENROLLED).isEmpty() ||
            now - lastSessionTimestamp < AppConstants.MILLIS_IN_24HOURS / 2
        ) {
            return
        }

        val notificationType =
            if (now - lastSessionTimestamp > AppConstants.MILLIS_IN_24HOURS * 2) {
                RetentionNotificationType.DAY3
            } else {
                RetentionNotificationType.DAY1
            }

        val title = context.getString(notificationType.titleRes)
        val message = context.getString(notificationType.messageRes)

        val intent = Intent(context, SplashActivity::class.java)
        val taskBuilder = TaskStackBuilder
                .create(context)
                .addNextIntent(intent)

        val notification = notificationHelper.makeSimpleNotificationBuilder(
            stepikNotification = null,
            justText = message,
            taskBuilder = taskBuilder,
            title = title,
            id = RETENTION_NOTIFICATION_ID
        )

        showNotification(RETENTION_NOTIFICATION_ID, notification.build())
        // TODO Schedule retention notification
    }
}