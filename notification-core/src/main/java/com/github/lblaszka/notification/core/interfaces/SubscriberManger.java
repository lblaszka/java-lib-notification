package com.github.lblaszka.notification.core.interfaces;

import java.util.Collection;
import java.util.Optional;

public interface SubscriberManger {
    Collection<Subscriber> getAll( int pageSize, int pageNumber );
    Optional<Subscriber> getById( Long subscriberId );

    Subscriber createNew();
    void remove( Long subscriberId );
}
