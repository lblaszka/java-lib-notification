package com.github.lblaszka.notification.core.structures;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Collection;

@Builder
public class NotificationData {
    public final Long id;
    public final LocalDateTime dateTime;
    public final Collection<String> tags;
    public final String content;
}