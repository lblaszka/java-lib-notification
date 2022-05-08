package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.NotificationAddresseeRepository;
import com.github.lblaszka.notification.core.interfaces.NotificationManager;
import com.github.lblaszka.notification.core.interfaces.NotificationRepository;
import com.github.lblaszka.notification.core.interfaces.SubscriberRepository;
import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;
import com.github.lblaszka.notification.core.structures.NotificationData;
import com.github.lblaszka.notification.core.structures.SubscriberData;
import com.github.lblaszka.notification.core.utils.Pagination;

import java.time.LocalDateTime;
import java.util.*;

public class NotificationManagerImpl implements NotificationManager {
    private final EventEmitter eventEmitter;
    private final NotificationRepository notificationRepository;
    private final NotificationAddresseeRepository notificationAddresseeRepository;

    private final Map<String, Set<Long>> tagAndSubscriberIdSetMap = new HashMap<>();

    NotificationManagerImpl( EventEmitter eventEmitter, NotificationRepository notificationRepository, NotificationAddresseeRepository notificationAddresseeRepository, SubscriberRepository subscriberRepository ) {
        this.eventEmitter = eventEmitter;
        this.notificationRepository = notificationRepository;
        this.notificationAddresseeRepository = notificationAddresseeRepository;

        this.loadSubscribedTagsAndUpdateMap( subscriberRepository );
        this.eventEmitter.changedSubscribedTag()
                .subscribe( this::updateSubscriptions );
    }

    @Override
    public Long emitNotification(Collection<String> notificationTags, String content) {
        Objects.requireNonNull( notificationTags, "Notification parameter tag collection is null!" );
        Objects.requireNonNull( content, "Content parameter is null!");

        NotificationData notificationData = this.saveNotificationDataFrom( notificationTags, content );
        Collection<Long> addresseeIdCollection = this.getNotificationAddresseesOfTagCollection( notificationTags );
        this.saveNotificationAddressees( notificationData.id, addresseeIdCollection );
        this.eventEmitter.newNotificationEvent( notificationData, addresseeIdCollection );

        return notificationData.id;
    }

    @Override
    public Collection<NotificationData> getNotificationData(Pagination pagination) {
        Objects.requireNonNull( pagination, "Pagination parameter is null!");

        return this.notificationRepository.findAll( pagination );
    }

    @Override
    public void deleteNotification(long notificationId) {
        this.notificationRepository.delete( notificationId );
        this.notificationAddresseeRepository.deleteAllByNotificationId( notificationId );
    }

    private NotificationData saveNotificationDataFrom( Collection<String> notificationTags, String content ) {
        NotificationData notificationDataToSave =  NotificationData.builder()
                .id( null )
                .dateTime( LocalDateTime.now() )
                .tags( notificationTags )
                .content( content )
                .build();

        long notificationDataEntityId = this.notificationRepository.save( notificationDataToSave );

        return NotificationData.copy( notificationDataToSave )
                .id( notificationDataEntityId )
                .build();
    }

    private void saveNotificationAddressees( Long notificationId, Collection<Long> addresseeIdCollection ) {
        addresseeIdCollection.stream()
                .map( addresseeId -> NotificationAddresseeData.builder()
                        .subscriberId( addresseeId )
                        .notificationId( notificationId )
                        .read( false )
                        .build()
                ).forEach(this.notificationAddresseeRepository::save );
    }

    private Collection<Long> getNotificationAddresseesOfTagCollection( Collection<String> tagCollection ) {
        return this.tagAndSubscriberIdSetMap.entrySet()
                .stream()
                .filter( entry -> tagCollection.contains( entry.getKey() ) )
                .map( Map.Entry::getValue )
                .reduce( new HashSet<>(), (acc, set) -> { acc.addAll( set ); return acc; } );
    }

    private void loadSubscribedTagsAndUpdateMap(SubscriberRepository subscriberRepository ) {
        subscriberRepository.findAll( Pagination.noPaging() )
                .forEach( this::updateSubscriptions );
    }

    void updateSubscriptions( SubscriberData subscriberData ) {
        this.tagAndSubscriberIdSetMap
                .forEach( (tag, subscriberIdCollection ) -> subscriberIdCollection.remove( subscriberData.id ) );

        subscriberData.subscribeTagCollection
                .forEach( subscribedTag -> this.loadSubscribedTagsAndUpdateMap( subscriberData.id, subscribedTag ) );
    }

    private void loadSubscribedTagsAndUpdateMap(long subscriberId, String subscribedTag ) {

        if( this.tagAndSubscriberIdSetMap.containsKey( subscribedTag ) ) {
            this.tagAndSubscriberIdSetMap.get( subscribedTag ).add( subscriberId );
        } else {
            Set<Long> set = new HashSet<>();
            set.add( subscriberId );
            this.tagAndSubscriberIdSetMap.put( subscribedTag, set );
        }
    }

}
