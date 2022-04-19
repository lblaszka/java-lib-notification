package com.github.lblaszka.notification.core.interfaces;

import io.reactivex.rxjava3.core.Observable;

import java.util.Collection;
import java.util.Optional;

public interface Subscriber {
    Observable<Notification> incomingNotifications();
    Observable<Boolean> unreadNotifications();

    Optional<Notification> getNotification( Long notificationId );
    Collection<Notification> getNotifications( int pageSize, int pageNumber );
    Collection<Notification> getNotifications( int pageSize, int pageNumber, boolean read );

    Collection<String> getSubscribedTags();
    void addSubscribedTags( String... tag );
    void removeSubscribedTags( String... tag );
}
