package com.sailing.dscg.zookeeper;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:定义zookeeper节点名称
 * <p>
 * Update by sjh on 2018/9/25 16:25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Node {
    /**节点名称*/
    String name();
}
