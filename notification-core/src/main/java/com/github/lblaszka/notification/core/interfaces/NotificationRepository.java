package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationData;

import java.util.Collection;
import java.util.Optional;

public interface NotificationRepository {
    Optional<NotificationData> findById( Long notificationData );
    Collection<NotificationData> find( int pageSize, int pageNumber );

    Long save( NotificationData notificationData );
    void delete( Long notificationId );
}
