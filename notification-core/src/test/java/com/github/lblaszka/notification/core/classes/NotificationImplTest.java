package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.Notification;
import com.github.lblaszka.notification.core.interfaces.NotificationAddresseeRepository;
import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;
import com.github.lblaszka.notification.core.structures.NotificationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationImplTest {

    NotificationAddresseeRepository notificationAddresseeRepositoryMock;
    NotificationData notificationData;
    Long subscriberId;
    Notification notification;


    @BeforeEach
    public void before() {
        this.notificationData = NotificationData.builder().id( 1L ).build();
        this.notificationAddresseeRepositoryMock = mock( NotificationAddresseeRepository.class );
        this.subscriberId = new Random().nextLong();
        this.notification = this.getNotificationImpl( this.subscriberId, this.notificationData, this.notificationAddresseeRepositoryMock );
    }

    @Test
    void isRead() {
        Assertions.assertFalse(this.notification.isRead());

        this.notification.setRead();
        Assertions.assertTrue(this.notification.isRead());
    }

    @Test
    void setRead() {
        Assertions.assertFalse(this.notification.isRead());

        this.notification.setRead();
        Assertions.assertTrue(this.notification.isRead());

        NotificationAddresseeData notificationAddresseeData = NotificationAddresseeData.builder()
                .notificationId( this.notificationData.id )
                .subscriberId( this.subscriberId )
                .read( true )
                .build();

        verify( this.notificationAddresseeRepositoryMock ).save( notificationAddresseeData );
    }

    @Test
    void hide() {
        this.notification.hide();
        verify( this.notificationAddresseeRepositoryMock ).delete( this.subscriberId, this.notificationData.id );
    }

    @Test
    void getDetails() {
        Assertions.assertEquals( this.notificationData, this.notification.getDetails() );
    }

    private Notification getNotificationImpl( Long subscriberId, NotificationData notificationData, NotificationAddresseeRepository notificationAddresseeRepository ) {
        return NotificationImpl.builder()
                .subscriberId( subscriberId)
                .read( false )
                .notificationAddresseeRepository( notificationAddresseeRepository )
                .notificationData( notificationData )
                .build();
    }

}