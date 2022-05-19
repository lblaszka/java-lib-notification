package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.*;
import com.github.lblaszka.notification.core.structures.SubscriberData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubscriberMangerImpl implements SubscriberManger {

    private final EventEmitter eventEmitter;
    private final NotificationRepository notificationRepository;
    private final NotificationAddresseeRepository notificationAddresseeRepository;
    private final SubscriberRepository subscriberRepository;

    public SubscriberMangerImpl(EventEmitter eventEmitter, NotificationRepository notificationRepository, NotificationAddresseeRepository notificationAddresseeRepository, SubscriberRepository subscriberRepository) {
        this.eventEmitter = eventEmitter;
        this.notificationRepository = notificationRepository;
        this.notificationAddresseeRepository = notificationAddresseeRepository;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public Optional<Subscriber> getById(long subscriberId) {
        return this.subscriberRepository.findById( subscriberId )
                .map( this::subscriberOf );
    }

    @Override
    public Collection<Subscriber> getAll(Pagination pagination) {
        return this.subscriberRepository.findAll( pagination )
                .stream()
                .map( this::subscriberOf )
                .collect( Collectors.toSet() );
    }

    @Override
    public Subscriber createNew(String... subscribedTags) {
        SubscriberData subscriberData = this.createSubscribedData( subscribedTags );

        this.eventEmitter.changeSubscribedTagEvent( subscriberData );
        return this.subscriberOf( subscriberData );
    }

    @Override
    public void delete(long subscriberId) {
        this.eventEmitter.changeSubscribedTagEvent( SubscriberData.builder().id( subscriberId ).subscribeTagCollection(Collections.emptyList() ).build());
        this.subscriberRepository.delete( subscriberId );
        this.notificationAddresseeRepository.deleteAllBySubscriberId( subscriberId );
    }

    private SubscriberData createSubscribedData( String... subscribedTags ) {
        Collection<String> subscribedTag = Arrays
                .stream( subscribedTags )
                .map( String::toUpperCase )
                .collect(Collectors.toSet());

        return this.subscriberRepository.save( SubscriberData.builder().subscribeTagCollection( subscribedTag ).build() );
    }

    private Subscriber subscriberOf( SubscriberData subscriberData ) {
        return new SubscriberImpl( this.notificationRepository, this.notificationAddresseeRepository, this.subscriberRepository, this.eventEmitter, subscriberData.id );
    }
}
