package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.Notification;
import com.github.lblaszka.notification.core.structures.NotificationData;
import com.github.lblaszka.notification.core.structures.SubscriberData;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EventEmitterTest {

    private EventEmitter eventEmitter;

    @BeforeEach
    void before() {
        this.eventEmitter = new EventEmitter();
    }

    @Test
    void newNotificationEvent() {
        long subscriberWithOneNotification = 1L;
        long subscriberWithManyNotifications = 2L;
        long subscriberWithoutNotification = 3L;

        Map<NotificationData, Collection<Long>> notificationDataAndAddresseeIdCollectionMap = new HashMap<>();
        notificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 0L ).build(), Collections.singletonList( subscriberWithOneNotification ) );
        notificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 1L ).build(), Arrays.asList( subscriberWithOneNotification, subscriberWithManyNotifications ) );
        notificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 2L ).build(), Collections.emptyList() );
        notificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 3L ).build(), Collections.singletonList( 4L ) );

        TestObserver<NotificationData> oneNotificationShouldIncome = this.eventEmitter.incomingNotificationToSubscriber( subscriberWithOneNotification ).test();
        TestObserver<NotificationData> manyNotificationShouldIncome = this.eventEmitter.incomingNotificationToSubscriber( subscriberWithManyNotifications ).test();
        TestObserver<NotificationData> nothingShouldIncome = this.eventEmitter.incomingNotificationToSubscriber( subscriberWithoutNotification ).test();

        notificationDataAndAddresseeIdCollectionMap.forEach( this.eventEmitter::newNotificationEvent );


        oneNotificationShouldIncome
                .assertNotComplete()
                .assertNoErrors()
                .assertValueSequence( this.getNotificationDataBySubscriberId( notificationDataAndAddresseeIdCollectionMap, subscriberWithOneNotification ) );

        manyNotificationShouldIncome
                .assertNotComplete()
                .assertNoErrors()
                .assertValueSequence( this.getNotificationDataBySubscriberId( notificationDataAndAddresseeIdCollectionMap, subscriberWithManyNotifications ) );

        nothingShouldIncome
                .assertNotComplete()
                .assertNoErrors()
                .assertValueSequence( this.getNotificationDataBySubscriberId( notificationDataAndAddresseeIdCollectionMap, subscriberWithoutNotification ) );
    }

    @Test
    void incomingNotificationToSubscriber() {
        long subscriberWithNotifications = 1L;

        Map<NotificationData, Collection<Long>> fistPartOfNotificationDataAndAddresseeIdCollectionMap = new HashMap<>();
        fistPartOfNotificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 0L ).build(), Collections.singletonList( subscriberWithNotifications ) );
        fistPartOfNotificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 1L ).build(), Collections.singletonList( subscriberWithNotifications ) );
        fistPartOfNotificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 2L ).build(), Collections.singletonList( subscriberWithNotifications ) );

        TestObserver<NotificationData> allNotificationsShouldIncome = this.eventEmitter.incomingNotificationToSubscriber( subscriberWithNotifications ).test();
        fistPartOfNotificationDataAndAddresseeIdCollectionMap.forEach( this.eventEmitter::newNotificationEvent );

        Map<NotificationData, Collection<Long>> secondPartOfNotificationDataAndAddresseeIdCollectionMap = new HashMap<>();
        secondPartOfNotificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 3L ).build(), Collections.singletonList( subscriberWithNotifications ) );
        secondPartOfNotificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 4L ).build(), Collections.singletonList( subscriberWithNotifications ) );
        secondPartOfNotificationDataAndAddresseeIdCollectionMap.put( NotificationData.builder().id( 5L ).build(), Collections.singletonList( subscriberWithNotifications ) );

        TestObserver<NotificationData> halfNotificationsShouldIncome = this.eventEmitter.incomingNotificationToSubscriber( subscriberWithNotifications ).test();
        secondPartOfNotificationDataAndAddresseeIdCollectionMap.forEach( this.eventEmitter::newNotificationEvent );

        TestObserver<NotificationData> nothingShouldIncome = this.eventEmitter.incomingNotificationToSubscriber( subscriberWithNotifications ).test();


        Collection<NotificationData> allNotificationDataCollection = this.getNotificationDataBySubscriberId( fistPartOfNotificationDataAndAddresseeIdCollectionMap, subscriberWithNotifications );
        allNotificationDataCollection.addAll( this.getNotificationDataBySubscriberId( secondPartOfNotificationDataAndAddresseeIdCollectionMap, subscriberWithNotifications ) );

        allNotificationsShouldIncome
                .assertNotComplete()
                .assertNoErrors()
                .assertValueSequence( allNotificationDataCollection );

        halfNotificationsShouldIncome
                .assertNotComplete()
                .assertNoErrors()
                .assertValueSequence( this.getNotificationDataBySubscriberId( secondPartOfNotificationDataAndAddresseeIdCollectionMap, subscriberWithNotifications ) );

        nothingShouldIncome
                .assertNotComplete()
                .assertNoErrors()
                .assertValueSequence( Collections.emptyList() );
    }

    private Collection<NotificationData> getNotificationDataBySubscriberId( Map<NotificationData, Collection<Long>> notificationDataAndAddresseeIdCollectionMap, Long subscriberId ) {
        return notificationDataAndAddresseeIdCollectionMap.entrySet()
                .stream()
                .filter( entry -> entry.getValue().contains( subscriberId ) )
                .map( Map.Entry::getKey )
                .collect(Collectors.toList());
    }

    @Test
    public void subscriberChangeEmitTest() {
        Collection<SubscriberData> dataFirstPart = Arrays.asList(
                SubscriberData.builder().id( 1L ).subscribeTagCollection( Arrays.asList("ONE", "TWO") ).build(),
                SubscriberData.builder().id( 2L ).subscribeTagCollection(Collections.emptyList()).build()
        );

        Collection<SubscriberData> dataSecondPart = Arrays.asList(
                SubscriberData.builder().id( 3L ).subscribeTagCollection(Collections.singletonList("TREE")).build(),
                SubscriberData.builder().id( 4L ).subscribeTagCollection(Collections.singletonList("FOUR")).build()
        );


        Collection<SubscriberData> dataAll = new ArrayList<>();
        Assertions.assertTrue( dataAll.addAll( dataFirstPart ), "Failed create testing data set");
        Assertions.assertTrue( dataAll.addAll( dataSecondPart ), "Failed create testing data set");


        TestObserver<SubscriberData> subscriberDataTestObserverWithAllData = this.eventEmitter.changedSubscribedTag().test();
        dataFirstPart.forEach( this.eventEmitter::changeSubscribedTagEvent );
        TestObserver<SubscriberData> subscriberDataTestObserverWithHalf = this.eventEmitter.changedSubscribedTag().test();
        dataSecondPart.forEach( this.eventEmitter::changeSubscribedTagEvent );


        subscriberDataTestObserverWithAllData
                .assertNotComplete()
                .assertNoErrors()
                .assertValueSequence( dataAll );

        subscriberDataTestObserverWithHalf
                .assertNotComplete()
                .assertNoErrors()
                .assertValueSequence( dataSecondPart );
    }
}