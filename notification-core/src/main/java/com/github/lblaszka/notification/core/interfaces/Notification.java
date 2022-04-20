package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationData;

public interface Notification {
    NotificationData getDetails();

    boolean isRead();
    void setRead();

    void hide();
}
