package com.sailing.dscg.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.sailing.dscg.common.CacheUtils;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.Tools;
import com.sailing.dscg.entity.safeManage.Admin;
import com.sailing.dscg.entity.safeManage.Role;
import com.sailing.dscg.entity.safeManage.SafeConfig;
import com.sailing.dscg.service.safeManage.IAdminService;
import com.sailing.dscg.service.safeManage.IRoleService;
import com.sailing.dscg.service.safeManage.ISafeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @Description: 初始化数据
 * @Auther:史俊华
 * @Date:2018/8/1020
 */
@Component
public class InitDataApplicationRunner implements ApplicationRunner {
    private String operator ="{\"menuRight\":[{\"children\":[{\"icon\":\"desktop\",\"key\":\"platformRegister\",\"title\":\"平台注册\",\"url\":\"/vscg/platformRegister\"},{\"icon\":\"switcher\",\"key\":\"clusterManage\",\"title\":\"集群管理\",\"url\":\"/vscg/clusterManage\"},{\"icon\":\"user\",\"key\":\"userManage\",\"title\":\"用户管理\",\"url\":\"/vscg/usermanagement\"},{\"icon\":\"switcher\",\"key\":\"roleConfigure\",\"title\":\"角色权限管理\",\"url\":\"/vscg/roleConfigure\"}],\"icon\":\"setting\",\"key\":\"systemManage\",\"title\":\"系统设置\",\"url\":\"\"}],\"name\":\"配置管理员\",\"value\":\"operator\"}";
    private String auditor ="{\"menuRight\":[{\"children\":[{\"icon\":\"database\",\"key\":\"sysRunLog\",\"title\":\"系统运行日志\",\"url\":\"/vscg/sysRunLog\"},{\"icon\":\"laptop\",\"key\":\"operateLog\",\"title\":\"管理操作审计\",\"url\":\"/vscg/operatorLog\"},{\"icon\":\"hdd\",\"key\":\"deviceLog\",\"title\":\"设备认证审计\",\"url\":\"/vscg/deviceAuthenLog\"},{\"icon\":\"video-camera\",\"key\":\"videoLog\",\"title\":\"视频传输审计\",\"url\":\"/vscg/videoTransmissionLog\"}],\"icon\":\"book\",\"key\":\"logManage\",\"title\":\"日志管理\",\"url\":\"\"}],\"name\":\"日志管理员\",\"value\":\"auditor\"}";
    private String safetyMnger ="{\"menuRight\":[{\"children\":[{\"icon\":\"user\",\"key\":\"userManage\",\"title\":\"用户管理\",\"url\":\"/vscg/usermanagement\"},{\"icon\":\"switcher\",\"key\":\"roleConfigure\",\"title\":\"角色权限管理\",\"url\":\"/vscg/roleConfigure\"},{\"icon\":\"switcher\",\"key\":\"clusterManage\",\"title\":\"集群管理\",\"url\":\"/vscg/clusterManage\"}],\"icon\":\"setting\",\"key\":\"systemManage\",\"title\":\"系统设置\",\"url\":\"\"}],\"name\":\"系统管理员\",\"value\":\"safetyMnger\"}";

    @Autowired
    private ISafeConfigService safeConfigService;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IRoleService roleService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        initAdmin();
        initSafeConfig();
        initRole();
    }

    /**
     * 初始化安全配置
     */
    private void initSafeConfig(){
        try {
            List<SafeConfig> safeConfigs = safeConfigService.queryList();
            if(safeConfigs==null || safeConfigs.isEmpty()){
                SafeConfig safeConfig = new SafeConfig();
                safeConfig.setSessionTimeout(30);
                safeConfig.setLoginFail(0);
                safeConfigService.save(safeConfig);
            }else{
                SafeConfig safeConfig = safeConfigs.get(0);
                CacheUtils.put("safeConfig",safeConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化安全配置
     */
    private void initAdmin(){
        try {
            Admin admin = new Admin();
            admin.setLoginName("admin");
            Admin admin1 = adminService.getByLoginName(admin);
            if(admin1==null){
                admin.setUserName("系统管理员");
                admin.setPasswd(Tools.getMD5("sailing2018"));
                admin.setStatus(0);
                Role role = new Role();
                role.setName("管理员");
                role.setValue("admin");
                admin.setRole(role);
                admin.setFailCount(1);
                admin.setLastLoginTime(new Date());
                adminService.save(admin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化角色
     */
    private void initRole(){
        try {
            List<Role> roles = roleService.queryAll();
            if(roles==null || roles.isEmpty()){
                Role safetyMngerRole = JSONObject.parseObject(safetyMnger,Role.class);
                safetyMngerRole.setCreateTime(DateTool.getCurrDateString());
                Role operatorRole = JSONObject.parseObject(operator,Role.class);
                operatorRole.setCreateTime(DateTool.getCurrDateString());
                Role auditorRole = JSONObject.parseObject(auditor,Role.class);
                auditorRole.setCreateTime(DateTool.getCurrDateString());
                roleService.save(safetyMngerRole);
                roleService.save(operatorRole);
                roleService.save(auditorRole);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
