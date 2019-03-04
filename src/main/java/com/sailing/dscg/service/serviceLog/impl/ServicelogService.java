package com.sailing.dscg.service.serviceLog.impl;

import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.Tools;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.dao.IServiceLogDao;
import com.sailing.dscg.entity.Servicelog;
import com.sailing.dscg.service.serviceLog.IServicelogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicelogService implements IServicelogService {

    @Autowired
    IServiceLogDao serviceLogDao;


    //所有的操作类型统计
    @Override
    public List<String> operateType() {
        return serviceLogDao.getOperateTypes();
    }

    @Override
    public PageHelper<Servicelog> query(PageHelper<Servicelog> servicelogPageHelper) {
        PageHelper<Servicelog> pageHelper = new PageHelper<>();
        List<Servicelog> list = new ArrayList<Servicelog>();
        Servicelog servicelog = servicelogPageHelper.getObject();
        int pageSize = servicelogPageHelper.getPageSize();
        int pageNum = servicelogPageHelper.getPageNum();
        pageNum = pageNum > 0 ? (pageNum - 1) * pageSize : pageSize;
        pageSize = pageSize == 0 ? 20 : pageSize;
        servicelog.setPageSize(pageSize);
        servicelog.setPageNum(pageNum);
        if (StringUtils.isNotBlank(servicelogPageHelper.getStartDate())) {
            servicelog.setStartDate(DateTool.StringToDate(servicelogPageHelper.getStartDate() + " 00:00:00", ""));
        }
        if (StringUtils.isNotBlank(servicelogPageHelper.getEndDate())) {
            servicelog.setEndDate(DateTool.StringToDate(servicelogPageHelper.getEndDate() + " 23:59:59", ""));
        }
        int count = serviceLogDao.count(servicelog);
        list = serviceLogDao.queryList(servicelog);
        pageHelper.setObjects(list);
        pageHelper.setPageCount(count);
        pageHelper.setPageSize(servicelogPageHelper.getPageSize());
        pageHelper.setPageNum(servicelogPageHelper.getPageNum());
        return pageHelper;
    }

    @Override
    public Boolean save(Servicelog servicelog) throws Exception {
        if (StringUtils.isBlank(servicelog.getId())) servicelog.setId(Tools.getUUID());
        int retult = serviceLogDao.insert(servicelog);
        return retult == 1 ? true : false;
    }

    /**
     * 操作员日志批量删除
     *
     * @return java.lang.Boolean
     * @throws
     * @author Panyu
     * @date 2018/7/23 上午 09:47:36
     */
    @Override
    public Boolean delMulServiceLog(List<String> ids) {
        for (String id : ids) {
            Servicelog servicelog = new Servicelog();
            servicelog.setId(id);
            serviceLogDao.delete(servicelog);
        }
        return true;
    }

    /**
     * 查询操作类型的数量
     *
     * @param servicelog
     * @return java.lang.Integer
     * @throws
     * @author Panyu
     * @date 2018/7/19 15:27
     */
    @Override
    public Long operateTypeCount(Servicelog servicelog) {
        return serviceLogDao.getOperateTypeCount(servicelog);
    }

    @Override
    public Boolean deleteAll() throws Exception {
        int result = serviceLogDao.deleteAll();
        return result > 0 ? true : false;
    }

    @Override
    public Boolean delAllLoginLog() {
        int result = serviceLogDao.delAllLoginLog();
        return result >0 ? true : false;
    }

    @Override
    public List<Servicelog> queryAll() throws Exception {
        return null;
    }

    @Override
    public Servicelog get(Servicelog servicelog) throws Exception {
        return null;
    }

    @Override
    public Boolean delete(Servicelog servicelog) throws Exception {
        return null;
    }

    @Override
    public List<Servicelog> queryList(Servicelog servicelog) throws Exception {
        return null;
    }
}

