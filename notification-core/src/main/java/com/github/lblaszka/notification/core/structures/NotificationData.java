package com.github.lblaszka.notification.core.structures;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder
@EqualsAndHashCode
public class NotificationData {
    public final Long id;
    public final LocalDateTime dateTime;
    public final Collection<String> tags;
    public final String content;

    public static NotificationData.NotificationDataBuilder copy( NotificationData notificationData ) {
        return builder()
                .id( notificationData.id )
                .dateTime( notificationData.dateTime )
                .content( notificationData.content )
                .tags( notificationData.tags );
    }
}
