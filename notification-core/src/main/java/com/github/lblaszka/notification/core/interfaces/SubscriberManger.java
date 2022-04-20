package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Collection;
import java.util.Optional;

public interface SubscriberManger {
    Optional<Subscriber> getById( Long subscriberId );
    Collection<Subscriber> getAll( Pagination pagination );

    Subscriber createNew( String... subscribedTags );
    void remove( Long subscriberId );
}
