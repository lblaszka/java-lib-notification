package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.*;
import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;
import com.github.lblaszka.notification.core.structures.NotificationData;
import com.github.lblaszka.notification.core.structures.SubscriberData;
import com.github.lblaszka.notification.core.utils.Pagination;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

class SubscriberImplTest {
    private SubscriberRepository subscriberRepository;
    private NotificationRepository notificationRepository;
    private NotificationAddresseeRepository notificationAddresseeRepository;
    private EventEmitter eventEmitter;
    private Long subscriberId;
    private Subscriber subscriber;
    private Subject<NotificationData> notificationDataSubject;

    @BeforeEach
    void before() {
        this.subscriberId = new Random().nextLong();

        this.subscriberRepository = mock( SubscriberRepository.class );
        this.notificationAddresseeRepository = mock( NotificationAddresseeRepository.class );
        this.notificationRepository = mock( NotificationRepository.class );

        this.eventEmitter = mock( EventEmitter.class );
        this.notificationDataSubject = ReplaySubject.create();
        when( this.eventEmitter.incomingNotificationToSubscriber( this.subscriberId ) )
                .thenReturn( this.notificationDataSubject );

        this.subscriber = new SubscriberImpl( this.notificationRepository, this.notificationAddresseeRepository, this.subscriberRepository, this.eventEmitter, subscriberId );
    }

    @Test
    void getId() {
        Assertions.assertEquals( this.subscriberId, this.subscriber.getId() );
    }

    @Test
    void incomingNotifications() {
        final long notificationIdUnread = 1L;
        final long notificationIdRead = 2L;
        NotificationData notificationDataFirst = NotificationData.builder().id( notificationIdUnread ).build();
        NotificationData notificationDataSecond = NotificationData.builder().id( notificationIdRead ).build();

        this.notificationDataSubject.onNext( notificationDataFirst );
        this.notificationDataSubject.onNext( notificationDataSecond );

        when( this.notificationAddresseeRepository.findBySubscriberIdAndNotificationId( this.subscriberId, notificationIdUnread ) )
                .thenReturn( Optional.of( NotificationAddresseeData.builder().read( false ).build()) );
        when( this.notificationAddresseeRepository.findBySubscriberIdAndNotificationId( this.subscriberId, notificationIdRead ) )
                .thenReturn( Optional.of( NotificationAddresseeData.builder().read( true ).build()) );


        Observable<Notification> notificationObservable = this.subscriber.incomingNotifications();
        Assertions.assertNotNull( notificationObservable );

        TestObserver<Notification> notificationTestObserver = notificationObservable.test();
        List<Notification> emitNotificationCollection
                = notificationTestObserver.assertNoErrors().assertNotComplete().values();

        Assertions.assertEquals( 2, emitNotificationCollection.size() );
        Assertions.assertEquals( notificationDataFirst, emitNotificationCollection.get( 0 ).getDetails() );
        Assertions.assertFalse(emitNotificationCollection.get(0).isRead());
        Assertions.assertEquals( notificationDataSecond, emitNotificationCollection.get( 1 ).getDetails() );
        Assertions.assertTrue(emitNotificationCollection.get(1).isRead());

    }

    @Test
    void getNotification() {
        long notificationIdNotExist = 1L;
        long notificationIdExistButSubscriberIsNotAddressee = 2L;
        long notificationIdExistAndSubscriberIsAddressee = 3L;

        when( this.notificationRepository.findById( notificationIdNotExist ) )
                .thenReturn( Optional.empty() );
        when( this.notificationRepository.findById( notificationIdExistButSubscriberIsNotAddressee ) )
                .thenReturn( Optional.of( NotificationData.builder().id( notificationIdExistAndSubscriberIsAddressee ).build() ) );
        when( this.notificationRepository.findById( notificationIdExistAndSubscriberIsAddressee ) )
                .thenReturn( Optional.of( NotificationData.builder().id( notificationIdExistAndSubscriberIsAddressee ).build() ) );

        when( this.notificationAddresseeRepository.exist( this.subscriberId, notificationIdNotExist ) )
                .thenReturn( true );
        when( this.notificationAddresseeRepository.exist( this.subscriberId, notificationIdExistButSubscriberIsNotAddressee ) )
                .thenReturn( false );
        when( this.notificationAddresseeRepository.exist( this.subscriberId, notificationIdExistAndSubscriberIsAddressee ) )
                .thenReturn( true );

        when( this.notificationAddresseeRepository.findBySubscriberIdAndNotificationId( this.subscriberId, notificationIdNotExist ) )
                .thenReturn( Optional.empty() );
        when( this.notificationAddresseeRepository.findBySubscriberIdAndNotificationId( this.subscriberId, notificationIdExistAndSubscriberIsAddressee ) )
                .thenReturn( Optional.empty() );
        when( this.notificationAddresseeRepository.findBySubscriberIdAndNotificationId( this.subscriberId, notificationIdExistAndSubscriberIsAddressee ) )
                .thenReturn( Optional.of( NotificationAddresseeData.builder().notificationId( notificationIdExistAndSubscriberIsAddressee ).subscriberId( this.subscriberId ).read( true ).build() )  );

        Assertions.assertFalse( this.subscriber.getNotification( notificationIdNotExist ).isPresent() );
        Assertions.assertFalse( this.subscriber.getNotification( notificationIdExistButSubscriberIsNotAddressee ).isPresent() );
        Assertions.assertTrue( this.subscriber.getNotification( notificationIdExistAndSubscriberIsAddressee ).isPresent() );
    }

    @Test
    void getNotifications() {
        Collection<NotificationData> notificationDataCollection = new HashSet<>();
        Map<Long, NotificationAddresseeData> idAndNotificationAddresseeDataMap = new HashMap<>();
        for( long it = 0; it < 8; it++ ){
            notificationDataCollection
                    .add( NotificationData.builder().id( it ).build() );
            idAndNotificationAddresseeDataMap
                    .put( it, NotificationAddresseeData.builder().notificationId( it ).subscriberId( this.subscriberId ).read( it % 2 == 1 ).build() );
        }

        when( this.notificationRepository.findById(  anyLong() ) )
                .then( inv -> notificationDataCollection.stream().filter( notificationData -> notificationData.id.equals( inv.getArgument(0) ) ).findAny() );
        when( this.notificationAddresseeRepository.findBySubscriberId( anyLong(), any() ) )
                .thenReturn( idAndNotificationAddresseeDataMap );
        when( this.notificationAddresseeRepository.findBySubscriberId( anyLong(), any(), anyBoolean() ) )
                .then( inv -> idAndNotificationAddresseeDataMap.entrySet().stream().filter( entry -> entry.getValue().read.equals( inv.getArgument(2) ) ).collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) ) );

        Collection<Notification> notificationsAll = this.subscriber.getNotifications( Pagination.noPaging() );
        Collection<Notification> notificationsOnlyRead = this.subscriber.getNotifications( Pagination.noPaging(), true );
        Collection<Notification> notificationsOnlyNotRead = this.subscriber.getNotifications( Pagination.noPaging(), false );

        Assertions.assertTrue( notificationsAll.stream().map( Notification::getDetails).collect(Collectors.toSet()).containsAll( notificationDataCollection ) );
        Assertions.assertFalse(notificationsOnlyRead.stream().anyMatch(notification -> !notification.isRead()) );
        Assertions.assertFalse(notificationsOnlyNotRead.stream().anyMatch(Notification::isRead) );
    }

    @Test
    void getSubscribedTags() {
        Collection<String> subscribedTag = Collections.unmodifiableCollection( Arrays.asList( "ONE", "TWO", "TREE" ) );

        when( this.subscriberRepository.findById( this.subscriberId ) )
                .thenReturn( Optional.of(SubscriberData.builder().id( this.subscriberId ).subscribeTagCollection( subscribedTag ).build() ) );

        Collection<String> subscriberTagFromSubscriber = this.subscriber.getSubscribedTags();

        Assertions.assertEquals( subscribedTag.size(), subscriberTagFromSubscriber.size() );
        Assertions.assertTrue( subscribedTag.containsAll( subscriberTagFromSubscriber ) );
        Assertions.assertTrue( subscriberTagFromSubscriber.containsAll( subscribedTag ) );
    }

    @Test
    void addSubscribedTags() {
        Collection<String> subscribedTag = Collections.unmodifiableCollection( Arrays.asList( "ONE", "TWO", "TREE" ) );
        Collection<String> correctSubscribedTagCollectionNrOne = Arrays.asList( "ONE", "TWO", "TREE", "FOUR" );
        Collection<String> correctSubscribedTagCollectionNrTwo = Arrays.asList( "ONE", "TWO", "TREE", "FOUR", "FIVE" );
        Collection<String> correctSubscribedTagCollectionNrTree = Arrays.asList( "ONE", "TWO", "TREE" );
        Collection<String> correctSubscribedTagCollectionNrFour = Arrays.asList( "ONE", "TWO", "TREE", "FOUR" );

        when( this.subscriberRepository.findById( this.subscriberId ) )
                .thenReturn( Optional.of(SubscriberData.builder().id( this.subscriberId ).subscribeTagCollection( subscribedTag ).build() ) );
        ArgumentCaptor<SubscriberData> subscriberDataArgumentCaptor = ArgumentCaptor.forClass( SubscriberData.class );


        this.subscriber.addSubscribedTags("FOUR");
        this.subscriber.addSubscribedTags("FOUR", "FIVE");
        this.subscriber.addSubscribedTags("ONE", "TWO", "TREE");
        this.subscriber.addSubscribedTags("FOUR", "ONE");

        verify( this.subscriberRepository, times(4) ).save( subscriberDataArgumentCaptor.capture() );

        List<SubscriberData> savedSubscriberData = subscriberDataArgumentCaptor.getAllValues();

        Assertions.assertEquals( correctSubscribedTagCollectionNrOne.size(), savedSubscriberData.get(0).subscribeTagCollection.size() );
        Assertions.assertTrue( correctSubscribedTagCollectionNrOne.containsAll( savedSubscriberData.get(0).subscribeTagCollection ) );
        Assertions.assertTrue( savedSubscriberData.get(1).subscribeTagCollection.containsAll( correctSubscribedTagCollectionNrOne ) );

        Assertions.assertEquals( correctSubscribedTagCollectionNrTwo.size(), savedSubscriberData.get(1).subscribeTagCollection.size() );
        Assertions.assertTrue( correctSubscribedTagCollectionNrTwo.containsAll( savedSubscriberData.get(1).subscribeTagCollection ) );
        Assertions.assertTrue( savedSubscriberData.get(1).subscribeTagCollection.containsAll( correctSubscribedTagCollectionNrTwo ) );

        Assertions.assertEquals( correctSubscribedTagCollectionNrTree.size(), savedSubscriberData.get(2).subscribeTagCollection.size() );
        Assertions.assertTrue( correctSubscribedTagCollectionNrTree.containsAll( savedSubscriberData.get(2).subscribeTagCollection ) );
        Assertions.assertTrue( savedSubscriberData.get(2).subscribeTagCollection.containsAll( correctSubscribedTagCollectionNrTree ) );

        Assertions.assertEquals( correctSubscribedTagCollectionNrFour.size(), savedSubscriberData.get(3).subscribeTagCollection.size() );
        Assertions.assertTrue( correctSubscribedTagCollectionNrFour.containsAll( savedSubscriberData.get(3).subscribeTagCollection ) );
        Assertions.assertTrue( savedSubscriberData.get(3).subscribeTagCollection.containsAll( correctSubscribedTagCollectionNrFour ) );
    }

    @Test
    void removeSubscribedTags() {
        Collection<String> subscribedTag = Collections.unmodifiableCollection( Arrays.asList( "ONE", "TWO", "TREE" ) );
        Collection<String> correctSubscribedTagCollectionNrOne = Arrays.asList( "TWO", "TREE" );
        Collection<String> correctSubscribedTagCollectionNrTwo = Arrays.asList( "ONE", "TWO", "TREE" );
        Collection<String> correctSubscribedTagCollectionNrTree = Arrays.asList( "TWO" );
        Collection<String> correctSubscribedTagCollectionNrFour = Arrays.asList( "TWO", "TREE" );

        when( this.subscriberRepository.findById( this.subscriberId ) )
                .thenReturn( Optional.of(SubscriberData.builder().id( this.subscriberId ).subscribeTagCollection( subscribedTag ).build() ) );
        ArgumentCaptor<SubscriberData> subscriberDataArgumentCaptor = ArgumentCaptor.forClass( SubscriberData.class );


        this.subscriber.deleteSubscribedTags("ONE");
        this.subscriber.deleteSubscribedTags("FOUR");
        this.subscriber.deleteSubscribedTags("ONE", "TREE");
        this.subscriber.deleteSubscribedTags("FOUR", "ONE");

        verify( this.subscriberRepository, times(4) ).save( subscriberDataArgumentCaptor.capture() );

        List<SubscriberData> savedSubscriberData = subscriberDataArgumentCaptor.getAllValues();

        Assertions.assertEquals( correctSubscribedTagCollectionNrOne.size(), savedSubscriberData.get(0).subscribeTagCollection.size() );
        Assertions.assertTrue( correctSubscribedTagCollectionNrOne.containsAll( savedSubscriberData.get(0).subscribeTagCollection ) );
        Assertions.assertTrue( savedSubscriberData.get(1).subscribeTagCollection.containsAll( correctSubscribedTagCollectionNrOne ) );

        Assertions.assertEquals( correctSubscribedTagCollectionNrTwo.size(), savedSubscriberData.get(1).subscribeTagCollection.size() );
        Assertions.assertTrue( correctSubscribedTagCollectionNrTwo.containsAll( savedSubscriberData.get(1).subscribeTagCollection ) );
        Assertions.assertTrue( savedSubscriberData.get(1).subscribeTagCollection.containsAll( correctSubscribedTagCollectionNrTwo ) );

        Assertions.assertEquals( correctSubscribedTagCollectionNrTree.size(), savedSubscriberData.get(2).subscribeTagCollection.size() );
        Assertions.assertTrue( correctSubscribedTagCollectionNrTree.containsAll( savedSubscriberData.get(2).subscribeTagCollection ) );
        Assertions.assertTrue( savedSubscriberData.get(2).subscribeTagCollection.containsAll( correctSubscribedTagCollectionNrTree ) );

        Assertions.assertEquals( correctSubscribedTagCollectionNrFour.size(), savedSubscriberData.get(3).subscribeTagCollection.size() );
        Assertions.assertTrue( correctSubscribedTagCollectionNrFour.containsAll( savedSubscriberData.get(3).subscribeTagCollection ) );
        Assertions.assertTrue( savedSubscriberData.get(3).subscribeTagCollection.containsAll( correctSubscribedTagCollectionNrFour ) );
    }
}