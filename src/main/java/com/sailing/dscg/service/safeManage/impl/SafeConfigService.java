package com.sailing.dscg.service.safeManage.impl;

import com.sailing.dscg.common.CacheUtils;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.Tools;
import com.sailing.dscg.entity.safeManage.SafeConfig;
import com.sailing.dscg.service.safeManage.ISafeConfigService;
import com.sailing.dscg.zookeeper.ZookeeperServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:安全配置管理
 * <p>
 * Update by Panyu on 2018/7/24 上午 10:16:01
 */
@Service
public class SafeConfigService implements ISafeConfigService {

    @Autowired
    private ZookeeperServer<SafeConfig> zookeeperServer;


    @Override
    public List<SafeConfig> queryList() throws Exception {
        return zookeeperServer.queryAll(SafeConfig.class);
    }

    @Override
    public Boolean save(SafeConfig safeConfig) throws Exception {
        Boolean result = false;
        List<SafeConfig> safeConfigs = queryList();
        if (safeConfigs == null || safeConfigs.isEmpty()) {
            safeConfig.setId(Tools.getUUID());
            safeConfig.setCreateTime(DateTool.getCurrDateString());
            safeConfig.setCreateUser(CacheUtils.getAdminLoginName());
            result = zookeeperServer.create(safeConfig.getId(), safeConfig, SafeConfig.class);
        } else {
            safeConfig.setModifyTime(DateTool.getCurrDateString());
            safeConfig.setModifyUser(CacheUtils.getAdminLoginName());
            result = zookeeperServer.update(safeConfig.getId(), safeConfig, SafeConfig.class);
        }

        if (result) {
            CacheUtils.put("safeConfig", safeConfig);
        }
        return result;
    }
}
