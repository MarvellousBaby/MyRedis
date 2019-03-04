package com.sailing.dscg.entity.safeManage;

import com.sailing.dscg.entity.BaseEntity;
import com.sailing.dscg.zookeeper.Node;
import lombok.Data;

/**
 * Description:安全管理接口
 * <p>
 * Update by Panyu on 2018/7/24 上午 09:56:25
 */
@Data
@Node(name = "sys_SafeConfig")
public class SafeConfig extends BaseEntity {

    private Integer sessionTimeout;                 //登陆超时时间 默认30分钟
    private Integer loginFail;                      //登陆失败次数
    private Integer memoryCycle;                   //存储周期
    private String memoryPath;                    //存储路径
}
