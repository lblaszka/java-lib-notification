package com.github.lblaszka.notification.core.interfaces;


import com.github.lblaszka.notification.core.structures.NotificationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;

public abstract class NotificationTest {

    protected final Long NOTIFICATION_ID = 2L;

    protected abstract Notification of(boolean read, NotificationData notificationData );

    @Test
    void testGetDetails() {
        NotificationData expectedNotificationData = this.getNotificationData();
        Notification notification = this.of( false, expectedNotificationData );

        NotificationData actualNotificationData = notification.getDetails();

        Assertions.assertNotNull( actualNotificationData );
        Assertions.assertEquals( expectedNotificationData, actualNotificationData );
    }

    @Test
    void testSetReadChangeReadStatus() {
        Notification notification = this.of( false, this.getNotificationData() );
        Assertions.assertFalse( notification.isRead() );

        notification.setRead();

        Assertions.assertTrue( notification.isRead() );
    }

    protected NotificationData getNotificationData() {
        return NotificationData.builder()
                .id( NOTIFICATION_ID )
                .tags( Collections.emptyList() )
                .content( "" )
                .dateTime( LocalDateTime.of( 2000, 1, 1, 1, 1, 1) )
                .build();
    }
}