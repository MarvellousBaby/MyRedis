package com.sailing.dscg.service.safeManage;

import com.sailing.dscg.entity.safeManage.SafeConfig;

import java.util.List;

/**
 * Description:安全管理配置
 * <p>
 * Update by Panyu on 2018/7/24 上午 10:13:40
 */
public interface ISafeConfigService {

    List<SafeConfig> queryList() throws Exception;

    Boolean save(SafeConfig safeConfig) throws Exception;


}
