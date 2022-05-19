package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.NotificationAddresseeRepository;
import com.github.lblaszka.notification.core.interfaces.NotificationRepository;
import com.github.lblaszka.notification.core.interfaces.SubscriberManger;
import com.github.lblaszka.notification.core.interfaces.SubscriberRepository;
import com.github.lblaszka.notification.core.structures.SubscriberData;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubscriberMangerImplTest {
    private EventEmitter eventEmitter;
    private NotificationRepository notificationRepository;
    private NotificationAddresseeRepository notificationAddresseeRepository;
    private SubscriberRepository subscriberRepository;
    private SubscriberManger subscriberManger;

    @BeforeEach
    void before() {
        this.eventEmitter = mock( EventEmitter.class );
        when( this.eventEmitter.incomingNotificationToSubscriber( anyLong() ) )
                .thenReturn( Observable.never() );

        this.notificationRepository = mock( NotificationRepository.class );
        this.notificationAddresseeRepository = mock( NotificationAddresseeRepository.class );

        this.subscriberRepository = mock( SubscriberRepository.class );
        when( this.subscriberRepository.save( any() ) )
                .then( inv -> SubscriberData.builder().id( 1L ).subscribeTagCollection( ((SubscriberData) inv.getArgument( 0 )).subscribeTagCollection ).build() );

        this.subscriberManger = new SubscriberMangerImpl( this.eventEmitter, this.notificationRepository, this.notificationAddresseeRepository, this.subscriberRepository );

    }

    @Test
    void checkSendEmitAfterAddNewUser() {
        String[] subscribedTagArray = {"ONE", "TWO", "TREE"};

        this.subscriberManger.createNew( subscribedTagArray );

        ArgumentCaptor<SubscriberData> subscriberDataArgumentCaptor = ArgumentCaptor.forClass( SubscriberData.class );
        verify( this.eventEmitter, times(1) ).changeSubscribedTagEvent( subscriberDataArgumentCaptor.capture() );

        List<SubscriberData> sendSubscribeDataCollection = subscriberDataArgumentCaptor.getAllValues();

        Assertions.assertEquals( 1, sendSubscribeDataCollection.size() );
        Assertions.assertEquals( subscribedTagArray.length, sendSubscribeDataCollection.get(0).subscribeTagCollection.size() );
        Assertions.assertTrue( sendSubscribeDataCollection.get(0).subscribeTagCollection.containsAll( Arrays.asList( subscribedTagArray ) ) );
        Assertions.assertTrue( Arrays.asList( subscribedTagArray ).containsAll( sendSubscribeDataCollection.get(0).subscribeTagCollection ) );
    }

    @Test
    void checkUpperCaseSubscribeTagAndRemoveDuplicates() {
        String[] subscribedTagArray = {"One", "two", "tRee", "TWO"};
        Collection<String> correctSubscribedTag = Arrays.stream( subscribedTagArray ).map( String::toUpperCase ).collect(Collectors.toSet());
        this.subscriberManger.createNew( subscribedTagArray );

        ArgumentCaptor<SubscriberData> subscriberDataArgumentCaptor = ArgumentCaptor.forClass( SubscriberData.class );
        verify( this.subscriberRepository, times(1) ).save( subscriberDataArgumentCaptor.capture() );


        List<SubscriberData> sendSubscribeDataCollection = subscriberDataArgumentCaptor.getAllValues();

        Assertions.assertEquals( 1, sendSubscribeDataCollection.size() );
        Assertions.assertEquals( correctSubscribedTag.size(), sendSubscribeDataCollection.get(0).subscribeTagCollection.size() );
        Assertions.assertTrue( sendSubscribeDataCollection.get(0).subscribeTagCollection.containsAll( correctSubscribedTag ) );
        Assertions.assertTrue( correctSubscribedTag.containsAll( sendSubscribeDataCollection.get(0).subscribeTagCollection ) );
    }
}