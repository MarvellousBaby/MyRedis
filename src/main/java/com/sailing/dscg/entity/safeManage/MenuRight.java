package com.sailing.dscg.entity.safeManage;

import com.sailing.dscg.entity.BaseEntity;
import lombok.Data;

import java.util.List;


/**
 * @Description: 安全管理，角色管理 ,右菜单实体类
 * @Date:2018/7/24
 */

@Data
public class MenuRight{
    /**主键KEY*/
    private String key;
    /***标题*/
    private String title;
    /**图标**/
    private String icon;
    /**访问地址**/
    private String url;
    /**子菜单**/
    private List<MenuRight> children;
}
