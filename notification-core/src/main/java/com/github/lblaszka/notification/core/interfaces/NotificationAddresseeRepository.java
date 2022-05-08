package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Map;
import java.util.Optional;

public interface NotificationAddresseeRepository {
    Map<Long, NotificationAddresseeData> findBySubscriberId( long subscriberId, Pagination pagination );
    Map<Long, NotificationAddresseeData> findBySubscriberId( long subscriberId, Pagination pagination, boolean read );

    boolean hasUnread( long subscriberId );

    boolean exist( long subscriberId, long notificationId );
    Optional<NotificationAddresseeData> findBySubscriberIdAndNotificationId( long subscriberId, long notificationId );

    NotificationAddresseeData save( NotificationAddresseeData notificationAddresseeData );
    void delete( long subscriberId, long notificationId );
    void deleteAllBySubscriberId( long subscriberId );
    void deleteAllByNotificationId( long notificationId );
}
