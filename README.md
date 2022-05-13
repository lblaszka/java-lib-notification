#Java- Lib Notification

A library thanks to which you can quickly implement notification mechanisms in your project

##How it's working?
Object `Subscriber` declares the subscribed `tags`. 
When in library was emitted notification, the addressees are determined based on the `tags`.
Then `Subscriber` can read related notifications also reactively via `rxJava3`.

##How to use?
The library does not specify what type of repository you must use. 
This means that you have to write the implementation of the repository interfaces yourself.
```java
package com.examles;

import com.github.lblaszka.notification.core.interfaces.*;
import com.github.lblaszka.notification.core.classes.LibNotification;

public class Example {
    public static void main( String[] args ) {
        LibNotification libNotification = LibNotification
                .builder()
                .notificationRepository( yourImplementationNotificationRepository )
                .notificationAddresseeRepository( yourImplementationNotificationAddresseeRepository )
                .subscriberRepository( yourImplementationSubscriberRepository )
                .build();

        Subscriber newlySubscriber = libNotification.subscriberManger
                .createNew( "SUBSCRIBED_TAG" );

        libNotification.notificationManager.emitNotification(Arrays.asList( "SUBSCRIBED_TAG" ), "MESSAGE");

        Collection<Notification> notifications = newlySubscriber.getNotifications( Pagination.noPaging() );

        System.out.println( notifications.size() ); // Should print: 1
        notifications
                .forEach( notification -> System.out.println( notification.getDetails().content ) );  // should print: MESSAGE
    }
}
```
