package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Collection;
import java.util.Optional;

public interface NotificationRepository {
    Optional<NotificationData> findById( long notificationDataId );
    Collection<NotificationData> findAll(Pagination pagination );

    long save( NotificationData notificationData );
    void delete( long notificationId );
}
