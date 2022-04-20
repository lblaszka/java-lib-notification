package com.github.lblaszka.notification.core.interfaces;

import com.github.lblaszka.notification.core.utils.Pagination;
import io.reactivex.rxjava3.core.Observable;

import java.util.Collection;
import java.util.Optional;

public interface Subscriber {
    Long getId();

    Observable<Notification> incomingNotifications();
    Observable<Boolean> unreadNotifications();

    Optional<Notification> getNotification( Long notificationId );
    Collection<Notification> getNotifications( Pagination pagination );
    Collection<Notification> getNotifications( Pagination pagination, boolean read );

    Collection<String> getSubscribedTags();
    void addSubscribedTags( String... subscribedTags );
    void removeSubscribedTags( String... unsubscribedTags );
}
