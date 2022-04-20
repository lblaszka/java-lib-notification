package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Map;

public interface NotificationAddresseeRepository {
    Map<Long, NotificationAddresseeData> findBySubscriberId( Long subscriberId, Pagination pagination );
    Map<Long, NotificationAddresseeData> findBySubscriberId( Long subscriberId, Pagination pagination, boolean read );

    NotificationAddresseeData save( NotificationAddresseeData notificationAddresseeData );
    void delete( Long subscriberId, Long notificationId );
}
