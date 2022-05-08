package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.Notification;
import com.github.lblaszka.notification.core.interfaces.NotificationAddresseeRepository;
import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;
import com.github.lblaszka.notification.core.structures.NotificationData;
import lombok.AccessLevel;
import lombok.Builder;

@Builder( access = AccessLevel.PACKAGE )
public class NotificationImpl implements Notification {
    private final Long subscriberId;
    private boolean read;
    private final NotificationData notificationData;
    private final NotificationAddresseeRepository notificationAddresseeRepository;

    @Override
    public boolean isRead() {
        return this.read;
    }

    @Override
    public void setRead() {
        this.read = true;

        NotificationAddresseeData notificationAddresseeData = NotificationAddresseeData.builder()
                .notificationId( this.notificationData.id )
                .subscriberId( this.subscriberId )
                .read( true )
                .build();

        this.notificationAddresseeRepository.save( notificationAddresseeData );
    }

    @Override
    public void hide() {
        this.notificationAddresseeRepository.delete( this.subscriberId, this.notificationData.id );
    }

    @Override
    public NotificationData getDetails() {
        return this.notificationData;
    }
}
