package com.sailing.dscg.service.SysRunning;

import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.sysRunning.SysRunning;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/14 下午 02:45:46
 */
public interface ISysRunningService {

    PageHelper<SysRunning> queryList(PageHelper<SysRunning> sysRunningPageHelper) throws Exception;

    List<String> queryPath() throws Exception;

    Boolean delete(List<String> ids) throws Exception;

    Boolean deleteAll() throws Exception;

}
