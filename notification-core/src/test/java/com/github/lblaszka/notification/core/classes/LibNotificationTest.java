package com.github.lblaszka.notification.core.classes;

import com.github.lblaszka.notification.core.interfaces.NotificationAddresseeRepository;
import com.github.lblaszka.notification.core.interfaces.NotificationRepository;
import com.github.lblaszka.notification.core.interfaces.SubscriberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class LibNotificationTest {

    @BeforeEach
    void before() {
    }

    @Test
    void createAllManagers() {
        LibNotification libNotification = LibNotification.builder()
                .notificationRepository( Mockito.mock(NotificationRepository.class ) )
                .notificationAddresseeRepository( Mockito.mock(NotificationAddresseeRepository.class ) )
                .subscriberRepository( Mockito.mock(SubscriberRepository.class ) )
                .build();

        Assertions.assertNotNull( libNotification );
        Assertions.assertNotNull( libNotification.notificationManager );
        Assertions.assertNotNull( libNotification.subscriberManger );
    }

    @Test
    void throwingIfSomeRepositoryIsNull() {
        Assertions.assertThrows( NullPointerException.class, () -> {
            LibNotification.builder()
                    .notificationAddresseeRepository( Mockito.mock(NotificationAddresseeRepository.class ) )
                    .subscriberRepository( Mockito.mock(SubscriberRepository.class ) )
                    .build();
        });

        Assertions.assertThrows( NullPointerException.class, () -> {
            LibNotification.builder()
                    .notificationRepository( Mockito.mock(NotificationRepository.class ) )
                    .subscriberRepository( Mockito.mock(SubscriberRepository.class ) )
                    .build();
        });

        Assertions.assertThrows( NullPointerException.class, () -> {
            LibNotification.builder()
                    .notificationRepository( Mockito.mock(NotificationRepository.class ) )
                    .notificationAddresseeRepository( Mockito.mock(NotificationAddresseeRepository.class ) )
                    .build();
        });
    }
}