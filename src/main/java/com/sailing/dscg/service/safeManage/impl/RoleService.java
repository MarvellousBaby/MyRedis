package com.sailing.dscg.service.safeManage.impl;

import com.sailing.dscg.common.CacheUtils;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.Tools;
import com.sailing.dscg.entity.safeManage.Role;
import com.sailing.dscg.service.safeManage.IAdminService;
import com.sailing.dscg.service.safeManage.IRoleService;
import com.sailing.dscg.zookeeper.ZookeeperServer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private ZookeeperServer<Role> zookeeperServer;

    @Autowired
    private IAdminService adminService;

    @Override
    public List<Role> queryAll() throws Exception {
        return zookeeperServer.queryAll(Role.class);
    }

    @Override
    public List<Role> queryList(Role config) throws Exception {
        List<Role> list = queryAll();
        //用角色名模糊查询
        if (StringUtils.isNotBlank(config.getName())) {
            list = list.stream().filter(role -> role.getName().contains(config.getName())).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Role get(Role role) throws Exception {
        return zookeeperServer.get(role.getId(), Role.class);
    }

    @Override
    public Boolean save(Role config) throws Exception {
        if (StringUtils.isBlank(config.getId())) {
            config.setId(Tools.getUUID());
            config.setCreateTime(DateTool.getCurrDateString());
            config.setCreateUser(CacheUtils.getAdminLoginName());
            return zookeeperServer.create(config.getId(), config, Role.class);
        } else {
            Role role = get(config);
            role.setName(config.getName());
            role.setValue(config.getValue());
            role.setMenuRight(config.getMenuRight());
            role.setModifyTime(DateTool.getCurrDateString());
            role.setModifyUser(CacheUtils.getAdminLoginName());

            Boolean result = zookeeperServer.update(role.getId(), role, Role.class);
            if (result) {
                adminService.updateMultiRole(role);
            }
            return result;
        }
    }


    @Override
    public Boolean delete(Role config) throws Exception {
        return zookeeperServer.delNode(config.getId(), Role.class);
    }

    @Override
    public Boolean deleteAll() throws Exception {
        return null;
    }
}
