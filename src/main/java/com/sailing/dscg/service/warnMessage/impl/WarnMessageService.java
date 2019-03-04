package com.sailing.dscg.service.warnMessage.impl;

import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.dao.IWarnMessageDao;
import com.sailing.dscg.entity.warnMessage.WarnMessage;
import com.sailing.dscg.service.warnMessage.IWarnMessageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/25 下午 05:04:14
 */
@Service
public class WarnMessageService implements IWarnMessageService {

    @Autowired
    IWarnMessageDao warnMessageDao;

    @Override
    public PageHelper<WarnMessage> queryList(PageHelper<WarnMessage> warnMessagePageHelper) throws Exception {
        PageHelper<WarnMessage> pageHelper = new PageHelper<>();
        List<WarnMessage> warnMessages = new ArrayList<>();
        WarnMessage warnMessage = warnMessagePageHelper.getObject();
        int pageSize = warnMessagePageHelper.getPageSize();
        int pageNum = warnMessagePageHelper.getPageNum();
        pageNum = pageNum > 0 ? (pageNum - 1) * pageSize : pageSize;
        pageSize = pageSize == 0 ? 20 : pageSize;
        warnMessage.setPageSize(pageSize);
        warnMessage.setPageNum(pageNum);

        if (StringUtils.isNotBlank(warnMessagePageHelper.getStartDate())) {
            warnMessage.setStartDate(DateTool.StringToDate(warnMessagePageHelper.getStartDate() + " 00:00:00", ""));
        }
        if (StringUtils.isNotBlank(warnMessagePageHelper.getEndDate())) {
            warnMessage.setEndDate(DateTool.StringToDate(warnMessagePageHelper.getEndDate() + " 23:59:59", ""));
        }

        int count = warnMessageDao.count(warnMessage);
        warnMessages = warnMessageDao.queryList(warnMessage);
        pageHelper.setObjects(warnMessages);
        pageHelper.setPageCount(count);
        pageHelper.setPageSize(warnMessagePageHelper.getPageSize());
        pageHelper.setPageNum(warnMessagePageHelper.getPageNum());

        return pageHelper;
    }

    @Override
    public Boolean deleteAll() {
        int result = warnMessageDao.deleteAll();
        return result == 0 ? false : true;

    }

    //所有的操作类型统计
    @Override
    public List<String> verifyType() {
        List<WarnMessage> warnMessageList = warnMessageDao.queryList(new WarnMessage());
        List<String> types = new ArrayList<>();
        for (WarnMessage warnMessage : warnMessageList) {
            if (types.contains(warnMessage.getVerifyType())) {
                continue;
            }
            types.add(warnMessage.getVerifyType());
        }
        return types;

    }

    @Override
    public Boolean delete(List<String> ids) {
        for (String id : ids) {
            warnMessageDao.delete(id);

        }
        return true;
    }

}
