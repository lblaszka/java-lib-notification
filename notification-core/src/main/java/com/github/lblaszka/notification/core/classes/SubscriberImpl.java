package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.*;
import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;
import com.github.lblaszka.notification.core.structures.NotificationData;
import com.github.lblaszka.notification.core.structures.SubscriberData;
import com.github.lblaszka.notification.core.utils.Pagination;
import io.reactivex.rxjava3.core.Observable;

import java.util.*;
import java.util.stream.Collectors;

public class SubscriberImpl implements Subscriber {

    private final NotificationRepository notificationRepository;
    private final SubscriberRepository subscriberRepository;
    private final NotificationAddresseeRepository notificationAddresseeRepository;

    private final Long id;
    private final Observable<Notification> incomingNotificationObservable;
    private final Observable<Boolean> unreadNotificationObservable;

    SubscriberImpl( NotificationRepository notificationRepository, NotificationAddresseeRepository notificationAddresseeRepository, SubscriberRepository subscriberRepository,  EventEmitter eventEmitter, Long subscriberId ) {
        this.notificationRepository = notificationRepository;
        this.subscriberRepository = subscriberRepository;
        this.notificationAddresseeRepository = notificationAddresseeRepository;

        this.id = subscriberId;

        this.incomingNotificationObservable = eventEmitter.incomingNotificationToSubscriber( this.id )
                .map( this::notificationFrom )
                .filter( Optional::isPresent )
                .map( Optional::get );

        this.unreadNotificationObservable = Observable.just( this.notificationAddresseeRepository.hasUnread( this.id ) )
                .mergeWith( this.incomingNotificationObservable.map( ignore -> true ) );
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public Observable<Notification> incomingNotifications() {
        return this.incomingNotificationObservable;
    }

    @Override
    public Observable<Boolean> unreadNotifications() {
        return this.unreadNotificationObservable;
    }

    @Override
    public Optional<Notification> getNotification(long notificationId) {
        if( this.notificationAddresseeRepository.exist( this.id, notificationId ) ) {
            return this.notificationRepository.findById( notificationId )
                    .flatMap( this::notificationFrom );
        }
        return Optional.empty();
    }

    @Override
    public Collection<Notification> getNotifications(Pagination pagination) {
        Objects.requireNonNull( pagination, "Pagination parameter is null!");

        return this.notificationAddresseeRepository.findBySubscriberId( this.id, pagination )
                .values().stream()
                .map( this::notificationFrom )
                .filter( Optional::isPresent )
                .map( Optional::get )
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Notification> getNotifications(Pagination pagination, boolean read) {
        Objects.requireNonNull( pagination, "Pagination parameter is null!");

        return this.notificationAddresseeRepository.findBySubscriberId( this.id, pagination, read )
                .values().stream()
                .map( this::notificationFrom )
                .filter( Optional::isPresent )
                .map( Optional::get )
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getSubscribedTags() {
        return this.subscriberRepository.findById( this.id )
                .map( subscriberData -> subscriberData.subscribeTagCollection )
                .orElse(Collections.emptyList());
    }

    @Override
    public void addSubscribedTags(String... tag) {
        Set<String> subscribedTagSet = this.getSubscribedTagSetFromRepository();
        subscribedTagSet.addAll( Arrays.asList( tag) );
        this.saveSubscribedTagInRepository( subscribedTagSet );
    }

    @Override
    public void deleteSubscribedTags(String... tag) {
        Set<String> subscribedTagSet = this.getSubscribedTagSetFromRepository();

        Arrays.asList( tag )
                .forEach(subscribedTagSet::remove);

        this.saveSubscribedTagInRepository( subscribedTagSet );
    }

    private Set<String> getSubscribedTagSetFromRepository() {
        return this.subscriberRepository.findById( this.id )
                .map( subscriberData -> subscriberData.subscribeTagCollection )
                .map(HashSet::new)
                .orElseThrow( () -> new RuntimeException("Not found subscriber in repository!") );
    }

    private void saveSubscribedTagInRepository( Collection<String> subscribedTagCollection ) {
        this.subscriberRepository
                .save( SubscriberData.builder().id( this.id ).subscribeTagCollection( subscribedTagCollection ).build() );
    }

    private Optional<Notification> notificationFrom( NotificationAddresseeData notificationAddresseeData ) {
        return this.notificationRepository.findById( notificationAddresseeData.notificationId )
                .map( notificationData -> this.notificationBuild( notificationData, notificationAddresseeData ) );
    }

    private Optional<Notification> notificationFrom(NotificationData notificationData ) {
        return this.notificationAddresseeRepository.findBySubscriberIdAndNotificationId( this.id, notificationData.id )
                .map( notificationAddresseeData -> this.notificationBuild( notificationData, notificationAddresseeData ) );
    }

    private Notification notificationBuild(NotificationData notificationData, NotificationAddresseeData notificationAddresseeData ) {
        return NotificationImpl.builder()
                .subscriberId( this.id )
                .notificationAddresseeRepository( this.notificationAddresseeRepository )
                .read( notificationAddresseeData.read )
                .notificationData( notificationData )
                .build();
    }
}
