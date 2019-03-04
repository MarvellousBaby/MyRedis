package com.sailing.dscg.service.platformRegister;

import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.common.web.INewBaseService;
import com.sailing.dscg.entity.platformRegister.PlatformRegister;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/14 上午 11:21:00
 */
public interface IPlatformRegisterService extends INewBaseService<PageHelper<PlatformRegister>> {

    Boolean deleteSingle(PlatformRegister platformRegister) throws Exception;
}
