package com.polarbookshop.order.event;

import java.util.StringJoiner;

public record OrderAcceptedEvent(Long orderId) {
    @Override
    public String toString() {
        return new StringJoiner(", ", OrderAcceptedEvent.class.getSimpleName() + "[", "]")
                .add("orderId=" + orderId)
                .toString();
    }
}
