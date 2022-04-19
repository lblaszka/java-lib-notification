package com.github.lblaszka.notification.core.structures;

import lombok.Builder;

@Builder
public class NotificationAddresseeData {
    public final Long id;
    public final Long subscriberId;
    public final Long notificationId;
    public final Boolean read;
}
