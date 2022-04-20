package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.SubscriberData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Map;
import java.util.Optional;

public interface SubscriberRepository {
    Optional<SubscriberData> findById( Long subscriberId );
    Map<Long, SubscriberData> findAll( Pagination pagination );

    SubscriberData save( SubscriberData subscriberData );
    void remove( Long subscriberId );
}
