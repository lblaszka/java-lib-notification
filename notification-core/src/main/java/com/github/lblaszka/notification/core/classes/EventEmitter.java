package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.structures.NotificationData;
import com.github.lblaszka.notification.core.structures.SubscriberData;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import lombok.AllArgsConstructor;

import java.util.*;

class EventEmitter {
    private final Subject<NotificationDataWithAddresseeCollection> notificationDataWithAddresseeCollectionSubject = PublishSubject.create();
    private final Subject<SubscriberData> subscriberDataSubject = PublishSubject.create();

    void newNotificationEvent( NotificationData notificationData, Collection<Long> notificationAddresseeCollection ) {
        this.notificationDataWithAddresseeCollectionSubject
                .onNext( new NotificationDataWithAddresseeCollection(notificationData, notificationAddresseeCollection) );
    }

    Observable<NotificationData> incomingNotificationToSubscriber( long subscriberId ) {
        return this.notificationDataWithAddresseeCollectionSubject
                .filter( notificationDataWithAddresseeCollection -> notificationDataWithAddresseeCollection.addresseeCollection.contains( subscriberId ) )
                .map( notificationDataWithAddresseeCollection -> notificationDataWithAddresseeCollection.notificationData );
    }

    void changeSubscribedTagEvent( SubscriberData subscriberData ) {
        this.subscriberDataSubject.onNext( subscriberData );
    }

    Observable<SubscriberData> changedSubscribedTag() {
        return this.subscriberDataSubject;
    }

    @AllArgsConstructor
    private static class NotificationDataWithAddresseeCollection {
        private final NotificationData notificationData;
        private final Collection<Long> addresseeCollection;
    }
}
