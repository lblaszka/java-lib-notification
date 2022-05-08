package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.structures.SubscriberData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Collection;
import java.util.Optional;

public interface SubscriberRepository {
    Optional<SubscriberData> findById( long subscriberId );
    Collection<SubscriberData> findAll(Pagination pagination );

    SubscriberData save( SubscriberData subscriberData );
    void delete(long subscriberId );
}
