package com.sailing.dscg.service.serviceLog;

import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.common.web.IBaseService;
import com.sailing.dscg.entity.Servicelog;

import java.util.List;

public interface IServicelogService extends IBaseService<Servicelog> {

    /**
     * 操作员操作日志
     */

    //操作类型返回
    List<String> operateType();

    //查询操作类型的数量
     Long operateTypeCount(Servicelog servicelog);

    //批量删除servicelog信息
     Boolean delMulServiceLog(List<String> ids);

    PageHelper<Servicelog> query(PageHelper<Servicelog> servicelogPageHelper);

    //清空登陆日志
    Boolean delAllLoginLog();


    //清空操作日志
//     Boolean delAllServiceLog();


}
