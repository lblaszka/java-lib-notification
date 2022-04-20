package com.github.lblaszka.notification.core.structures;

import lombok.Builder;

@Builder
public class NotificationAddresseeData {
    public final Long subscriberId;
    public final Long notificationId;
    public final Boolean read;

    public static NotificationAddresseeData.NotificationAddresseeDataBuilder copy( NotificationAddresseeData notificationAddresseeData ) {
        return builder()
                .subscriberId( notificationAddresseeData.subscriberId )
                .notificationId( notificationAddresseeData.notificationId )
                .read( notificationAddresseeData.read );
    }
}
