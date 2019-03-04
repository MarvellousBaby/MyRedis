package com.sailing.dscg.service.safeManage.impl;

import com.sailing.dscg.common.CacheUtils;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.Tools;
import com.sailing.dscg.entity.safeManage.Admin;
import com.sailing.dscg.entity.safeManage.Role;
import com.sailing.dscg.service.safeManage.IAdminService;
import com.sailing.dscg.service.safeManage.IRoleService;
import com.sailing.dscg.zookeeper.ZookeeperServer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {

    @Autowired
    private ZookeeperServer<Admin> zookeeperServer;

    @Autowired
    private IRoleService roleService;

    @Override
    public List<Admin> queryAll() throws Exception {
        return zookeeperServer.queryAll(Admin.class);
    }

    @Override
    public List<Admin> queryList(Admin admin) throws Exception {
        List<Admin> list = zookeeperServer.queryAll(Admin.class);
        if (StringUtils.isNotBlank(admin.getLoginName())) {
            list = list.stream().filter(admin1 -> StringUtils.isNotBlank(admin1.getLoginName())
                    && admin1.getLoginName().contains(admin.getLoginName())).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(admin.getUserName())) {
            list = list.stream().filter(admin1 -> StringUtils.isNotBlank(admin1.getUserName())
                    && admin1.getUserName().contains(admin.getUserName())).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public Admin get(Admin admin) throws Exception {
        return zookeeperServer.get(admin.getId(), Admin.class);
    }

    @Override
    public Admin getByLoginName(Admin admin) throws Exception {
        List<Admin> admins = queryList(admin);
        if (admins != null && !admins.isEmpty()) {
            return admins.get(0);
        }
        return null;
    }

    @Override
    public Admin getByLoginName(String loginName) throws Exception {
        Admin admin = new Admin();
        admin.setLoginName(loginName);
        return getByLoginName(admin);
    }

    @Override
    public List<Admin> queryByRoleId(String roleId) throws Exception {
        List<Admin> list = queryAll();
        list = list.stream().filter(admin -> admin.getRole().getId()!=null).filter(admin -> admin.getRole().getId().equals(roleId)).collect(Collectors.toList());
        return list;
    }

    @Override
    public Boolean save(Admin admin) throws Exception {
        if (!"admin".equals(admin.getLoginName())) {
            try {
                admin.setRole(roleService.get(admin.getRole()));
            } catch (Exception e) {
                throw e;
            }
        } else {
            Role role = new Role();
            role.setName("管理员");
            role.setValue("admin");
        }

        if (StringUtils.isBlank(admin.getId())) {
            admin.setId(Tools.getUUID());
            admin.setLastLoginTime(new Date());
            admin.setCreatetime(DateTool.getCurrDateString());
            admin.setFailCount(1);
            admin.setCreatetime(DateTool.getCurrDateString());
            admin.setCreateUser(CacheUtils.getAdminLoginName());
            return zookeeperServer.create(admin.getId(), admin, Admin.class);
        } else {
            Admin admin1 = zookeeperServer.get(admin.getId(), Admin.class);
            admin1.setUserName(admin.getUserName());
            admin1.setLoginName(admin.getLoginName());
            admin1.setRole(admin.getRole());
            admin1.setMobile(admin.getMobile());
            admin1.setModifyTime(DateTool.getCurrDateString());
            admin1.setModifyUser(CacheUtils.getAdminLoginName());
            return zookeeperServer.update(admin.getId(), admin1, Admin.class);
        }
    }

    @Override
    public Boolean updateStatus(Admin admin) throws Exception {
        Admin admin1 = zookeeperServer.get(admin.getId(), Admin.class);
        admin1.setStatus(admin.getStatus());
        admin1.setModifyTime(DateTool.getCurrDateString());
        admin1.setModifyUser(CacheUtils.getAdminLoginName());
        return zookeeperServer.update(admin1.getId(), admin1, Admin.class);
    }

    @Override
    public void updateMultiRole(Role role) throws Exception {
        List<Admin> admins = queryByRoleId(role.getId());
        if (admins != null) {
            for (Admin admin : admins) {
                admin.setRole(role);
                admin.setModifyTime(DateTool.getCurrDateString());
                zookeeperServer.update(admin.getId(), admin, Admin.class);
            }
        }
    }

    @Override
    public Boolean updatepwd(Admin admin) throws Exception {
        Admin admin1 = get(admin);
        admin1.setPasswd(admin.getPasswd());
        admin1.setModifyTime(DateTool.getCurrDateString());
        admin1.setModifyUser(CacheUtils.getAdminLoginName());
        return zookeeperServer.update(admin1.getId(), admin1, Admin.class);
    }

    @Override
    public Boolean delete(Admin admin) throws Exception {
        return zookeeperServer.delNode(admin.getId(), Admin.class);
    }

    @Override
    public Boolean adminLogin(Admin admin) throws Exception {
        List<Admin> admins = queryList(admin);
        if (admins != null && !admins.isEmpty()) {
            admins = admins.stream().filter(admin1 -> admin1.getPasswd().equals(admin.getPasswd())).collect(Collectors.toList());
            return !admins.isEmpty();
        } else {
            return false;
        }
    }

    //更新用户的登陆失败次数
    @Override
    public Boolean updateFailCount(Admin admin) throws Exception {
        Admin admin1 = getByLoginName(admin);
        admin1.setFailCount(admin.getFailCount());
        admin1.setLastLoginTime(admin.getLastLoginTime());
        admin1.setModifyTime(DateTool.getCurrDateString());
        admin1.setModifyUser(CacheUtils.getAdminLoginName());
        return zookeeperServer.update(admin1.getId(), admin1, Admin.class);
    }

    /***
     * 判断是否存在相同用户名用户
     * @param admin
     * @return
     */
    @Override
    public Boolean hasSameLoginName(Admin admin) throws Exception {
        Boolean needCheck = true;
        if (StringUtils.isNotBlank(admin.getId())) {
            Admin oldAdmin = get(admin);
            if (StringUtils.isNotBlank(oldAdmin.getLoginName())
                    && oldAdmin.getLoginName().equals(admin.getLoginName())) {
                needCheck = false;
            }
        }

        if (needCheck) {
            Admin admin1 = getByLoginName(admin);
            return admin1 == null ? false : true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean deleteAll() throws Exception {
        return null;
    }
}
