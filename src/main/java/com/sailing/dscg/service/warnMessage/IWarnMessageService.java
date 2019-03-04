package com.sailing.dscg.service.warnMessage;

import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.warnMessage.WarnMessage;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/25 下午 05:03:55
 */
public interface IWarnMessageService {

//    Long queryCount(PageHelper<WarnMessage> warnMessagePageHelper) throws Exception;

    PageHelper<WarnMessage> queryList(PageHelper<WarnMessage> warnMessagePageHelper) throws Exception;

    Boolean deleteAll();

    List<String>  verifyType();

    Boolean delete(List<String> ids);
}
