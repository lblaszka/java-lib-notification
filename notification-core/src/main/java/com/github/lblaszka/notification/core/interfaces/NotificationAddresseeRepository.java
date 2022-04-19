package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;

import java.util.Collection;

public interface NotificationAddresseeRepository {
    Collection<NotificationAddresseeData> findBySubscriberId( Long subscriberId, int pageSize, int pageNumber );
    Collection<NotificationAddresseeData> findBySubscriberId( Long subscriberId, int pageSize, int pageNumber, boolean read );

    Long save( NotificationAddresseeData notificationAddresseeData );
    void delete( Long notificationAddresseeDataId );
}
