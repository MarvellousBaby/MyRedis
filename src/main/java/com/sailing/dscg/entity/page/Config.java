//package com.sailing.common;
package com.sailing.dscg.entity.page;

import lombok.Data;

import java.util.Map;

@Data
public class Config {
    private String position;
    private String protocol;
    private String serviceId;
    private Integer size;
    private String persisted;
    private Map<String,String> realData;
}
