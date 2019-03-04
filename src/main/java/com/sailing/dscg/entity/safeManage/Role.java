package com.sailing.dscg.entity.safeManage;

import com.sailing.dscg.entity.BaseEntity;
import com.sailing.dscg.zookeeper.Node;
import lombok.Data;

import java.util.List;


/**
 * @Description: 安全管理，角色管理实体类
 * @Auther:李超群
 * @Date:2018/7/24
 */
@Data
@Node(name = "safeMng_Role")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色值
     */
    private String value;
    /**
     * 菜单权限集合
     */
    private List<MenuRight> menuRight;
}
