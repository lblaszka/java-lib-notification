package com.github.lblaszka.notification.core.structures;

import lombok.Builder;

import java.util.Collection;

@Builder
public class SubscriberData {
    public final Long id;
    public final Collection<String> subscribeTagCollection;

    public static SubscriberData.SubscriberDataBuilder copy( SubscriberData subscriberData ) {
        return builder()
                .id( subscriberData.id )
                .subscribeTagCollection( subscriberData.subscribeTagCollection );
    }
}
