package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Collection;

public interface NotificationManager {
    Long emitNotification( Collection<String> notificationTags, String content );
    Collection<NotificationData> getNotificationData(Pagination pagination );
    void deleteNotification(long notificationId );
}
