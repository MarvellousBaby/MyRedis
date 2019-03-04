package com.sailing.dscg.entity.page;

import lombok.Data;

@Data
public class ConsumerPosition {
    String topic;
    String partition;
    String currentOffset;
    String logEndOffset;
    String lag;
    String consumerID;
    String host;
    String clientId;
}
