package com.sailing.dscg.service.safeManage;

import com.sailing.dscg.common.web.IBaseService;
import com.sailing.dscg.entity.safeManage.Admin;
import com.sailing.dscg.entity.safeManage.Role;

import java.util.List;

public interface IAdminService extends IBaseService<Admin> {

    Admin getByLoginName(Admin admin) throws Exception;

    Admin getByLoginName(String loginName) throws Exception;

    List<Admin> queryByRoleId(String roleId) throws Exception;

    Boolean updateStatus(Admin admin) throws Exception;

    void updateMultiRole(Role role) throws Exception;

    Boolean updatepwd(Admin admin) throws Exception;

    Boolean adminLogin(Admin admin) throws Exception;

    Boolean updateFailCount(Admin admin) throws Exception;

    Boolean hasSameLoginName(Admin admin) throws Exception;
}
