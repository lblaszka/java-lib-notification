package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.SubscriberData;

import java.util.Collection;
import java.util.Optional;

public interface SubscriberRepository {
    Optional<SubscriberData> findById( Long subscriberId );
    Collection<SubscriberData> find( int pageSize, int pageNumber );


    Long save( SubscriberData subscriberData );
    void remove( Long subscriberId );
}
