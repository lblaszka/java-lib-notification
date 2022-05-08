package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.*;
import lombok.Builder;

import java.util.Objects;

public class LibNotification {
    public final NotificationManager notificationManager;
    public final SubscriberManger subscriberManger;

    @Builder
    private LibNotification( NotificationRepository notificationRepository, NotificationAddresseeRepository notificationAddresseeRepository, SubscriberRepository subscriberRepository ){
        Objects.requireNonNull( notificationRepository,"notificationRepository is null!" );
        Objects.requireNonNull( notificationAddresseeRepository,"notificationAddresseeRepository is null!" );
        Objects.requireNonNull( subscriberRepository,"subscriberRepository is null!" );

        EventEmitter eventEmitter = new EventEmitter();
        this.notificationManager = new NotificationManagerImpl( eventEmitter, notificationRepository, notificationAddresseeRepository, subscriberRepository );
        this.subscriberManger = new SubscriberMangerImpl( eventEmitter, notificationRepository, notificationAddresseeRepository, subscriberRepository );
    }
}
