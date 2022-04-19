package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationData;

public interface Notification {
    boolean isRead();
    void setRead();

    void remove();

    NotificationData getDetails();
}
