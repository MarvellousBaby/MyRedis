package com.sailing.dscg.entity.anno;


import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * Description:定义操作日志的操作类型的注解
 * <p>
 * Update by Panyu on 2018/7/18 9:18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface OperateType {

    /**
     * 操作类型的名称
     * @author      Panyu
     * @date        2018/7/18 9:19
    */
    String oprateTypeName() default "";


}
