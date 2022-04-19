package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationData;

import java.util.Collection;

public interface NotificationManager {
    Long emitNotification( Collection<String> notificationTags, String content );
    Collection<NotificationData> getNotifications( int pageSize, int pageNumber );
    void removeNotification( Long notificationId );
}
