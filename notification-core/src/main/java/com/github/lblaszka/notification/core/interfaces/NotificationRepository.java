package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Map;
import java.util.Optional;

public interface NotificationRepository {
    Optional<NotificationData> findById( Long notificationData );
    Map<Long, NotificationData> findAll( Pagination pagination );

    NotificationData save( NotificationData notificationData );
    void delete( Long notificationId );
}
