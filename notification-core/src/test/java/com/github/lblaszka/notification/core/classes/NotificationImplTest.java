package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.Notification;
import com.github.lblaszka.notification.core.interfaces.NotificationAddresseeRepository;
import com.github.lblaszka.notification.core.interfaces.NotificationTest;
import com.github.lblaszka.notification.core.structures.NotificationAddresseeData;
import com.github.lblaszka.notification.core.structures.NotificationData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class NotificationImplTest extends NotificationTest {
    private final Long SUBSCRIBER_ID = 1L;

    NotificationAddresseeRepository notificationAddresseeRepositoryMock;

    @Override
    protected Notification of(boolean read, NotificationData notificationData ) {
        this.notificationAddresseeRepositoryMock = mock( NotificationAddresseeRepository.class );

        return NotificationImpl.builder()
            .subscriberId( SUBSCRIBER_ID )
            .read( read )
            .notificationAddresseeRepository( this.notificationAddresseeRepositoryMock )
            .notificationData( notificationData )
            .build();
    }

    @Test
    void testSetReadUpdateCorrectEntity() {
        NotificationAddresseeData expectedNotificationAddresseeData = this.getNotificationAddresseeDataOf( true );
        Notification notification = this.of( false, this.getNotificationData() );

        notification.setRead();

        Mockito.verify( this.notificationAddresseeRepositoryMock, times( 1 ) )
                .save( expectedNotificationAddresseeData );
    }

    @Test
    void testSetReadUpdateEntityOnlyOnes() {
        Notification notification = this.of( false, this.getNotificationData() );

        notification.setRead();
        notification.setRead();

        Mockito.verify( this.notificationAddresseeRepositoryMock, times( 1 ) )
                .save( any() );
    }

    @Test
    void testHideDeleteEntity() {
        Notification notification = this.of( false, this.getNotificationData() );
        notification.hide();

        Mockito.verify( this.notificationAddresseeRepositoryMock, times( 1 ) )
                .delete( SUBSCRIBER_ID, NOTIFICATION_ID );
    }

    @Test
    void testHideTryDeleteEntityOnlyOnes() {
        Notification notification = this.of( false, this.getNotificationData() );
        notification.hide();
        notification.hide();

        Mockito.verify( this.notificationAddresseeRepositoryMock, times( 1 ) )
                .delete( anyLong(), anyLong() );
    }

    @Test
    void testAfterHideReadNotUpdatedEntity() {
        Notification notification = this.of( false, this.getNotificationData() );
        notification.hide();
        notification.setRead();

        Mockito.verify( this.notificationAddresseeRepositoryMock, times( 0 ) )
                .save( any() );
    }

    private NotificationAddresseeData getNotificationAddresseeDataOf( boolean read ) {
        return NotificationAddresseeData.builder()
                .subscriberId( SUBSCRIBER_ID )
                .notificationId( NOTIFICATION_ID )
                .read( read )
                .build();
    }
}