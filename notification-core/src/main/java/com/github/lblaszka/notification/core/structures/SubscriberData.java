package com.github.lblaszka.notification.core.structures;

import lombok.Builder;

import java.util.Collection;

@Builder
public class SubscriberData {
    public final Long id;
    public final Collection<String> subscribeTagCollection;
}
