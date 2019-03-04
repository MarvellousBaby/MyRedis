package com.sailing.dscg.controller.serviceLog;

import com.sailing.dscg.common.CSVUtils;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.common.entity.SingleProperties;
import com.sailing.dscg.common.page.ApachePOIExcelWrite;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.ApplicationProperties;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.Servicelog;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.service.serviceLog.IServicelogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/servicelog")
@CrossOrigin
@Slf4j
@Api(value = "用户操作日志接口", description = "用户操作日志接口")
public class ServicelogController {

    //导出路径
    @Value("${dscg.excel}")
    String path;
    int count = 0;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private IServicelogService iServicelogService;
    @Autowired
    private ApplicationProperties properties;

    @ApiOperation(value = "操作名称", notes = "操作名称")
    @RequestMapping(value = "/operateTypeName", method = RequestMethod.POST)
    public RespData<List<String>> operateType() {
        RespData<List<String>> respData = new RespData<List<String>>();
        try {
            List<String> list = iServicelogService.operateType();
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setData(list);

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            logger.error(e.getMessage(), e);
        }

        return respData;
    }



    //根据service_id列出所有状态记录 暂时用不着
    @ApiOperation(value = "查询管理操作审计", notes = "查询管理操作审计")
    @RequestMapping(value = "/servicelogSelect", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询管理操作审计")
    public RespData<List<Servicelog>> queryList(@RequestBody PageHelper<Servicelog> servicelogPageHelper) {
        RespData respData = new RespData();
        try {
            servicelogPageHelper = iServicelogService.query(servicelogPageHelper);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询管理操作审计成功！");
            respData.setData(servicelogPageHelper);
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            logger.error(e.getMessage(), e);
        }


        return respData;
    }

    /**
     * 查询操作类型数量
     *
     * @param servicelog
     * @return com.sailing.dscg.entity.RespData<java.lang.Integer>
     * @throws
     * @author Panyu
     * @date 2018/7/19 14:13
     */
    @ApiOperation(value = "查询操作类型数量", notes = "查询操作类型数量")
    @RequestMapping(value = "/queryOperateTypeCount", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询操作类型数量")
    public RespData<Long> queryOperateTypeCount(@RequestBody Servicelog servicelog) {

        RespData<Long> respData = new RespData<Long>();
        try {
            long count = iServicelogService.operateTypeCount(servicelog);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询操作类型数量成功！");
            respData.setData(count);

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            logger.error(e.getMessage(), e);
        }

        return respData;
    }

    /**
     * 批量删除操作审计日志
     *
     * @return com.sailing.dscg.entity.RespData
     * @throws
     * @author Panyu
     * @date 2018/7/23 上午 10:07:22
     */
    @ApiOperation(value = "批量删除操作日志", notes = "批量删除操作日志")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/delMulServiceLog", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "批量删除操作日志")
    public RespData delMulServiceLog(@RequestBody SingleProperties singleProperties) throws Exception {
        RespData respData = new RespData();
        try {
            boolean result = iServicelogService.delMulServiceLog(singleProperties.getStrIds());
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("批量删除操作日志成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("批量删除操作日志失败！");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            respData.setRespCode(RespCodeEnum.EXCEPTION);

        }

        return respData;
    }


    /**
     * 清空操作审计日志
     *
     * @param
     * @return com.sailing.dscg.entity.RespData
     * @throws
     * @author Panyu
     * @date 2018/7/23 下午 06:59:52
     */
    @ApiOperation(value = "清空操作审计日志", notes = "清空操作审计日志")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/delAllServiceLog", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "清空操作审计日志")
    public RespData delAllServiceLog() {
        RespData respData = new RespData();

        try {
            Boolean result = iServicelogService.deleteAll();
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("清空操作审计日志成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("清空操作审计日志失败！");
            }

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
        }

        return respData;
    }

    @ApiOperation(value = "清空用户登录日志", notes = "清空用户登录日志")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/delAllLoginLog", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "清空用户登录日志")
    public RespData delAllLoginLog() {
        RespData respData = new RespData();

        try {
            Boolean result = iServicelogService.delAllLoginLog();
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("清空用户登录日志成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("清空用户登录日志失败！");
            }

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
        }

        return respData;
    }

    @ApiOperation(value = "导出用户操作日志", notes = "导出用户操作日志")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/exportServiceLog", method = RequestMethod.GET)
    @ResponseBody
    @OperateType(oprateTypeName = "导出用户操作日志")
    public RespData exportServiceLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RespData resp = new RespData();
        String dateStr = DateTool.getCurrDateString(DateTool.DateFormat_yyyyMMddHHmmss);
        String dirName = "ServiceLog";
        String basePath = CSVUtils.getFilePath(path);
        String filePath = basePath + dirName + File.separator + dateStr;
        String zipFilePath = basePath + dirName;
        String zipName = dirName + dateStr + ".zip";
        List<Servicelog> servicelogList = null;
        try {
            PageHelper<Servicelog> pageHelper = new PageHelper<>();
            Servicelog servicelog = new Servicelog();
            if (!"null".equals(request.getParameter("startDate")) && !"null".equals(request.getParameter("endDate"))) {
                pageHelper.setStartDate(request.getParameter("startDate"));
                pageHelper.setEndDate(request.getParameter("endDate"));
            }
            if (!"null".equals(request.getParameter("userName"))) {
                servicelog.setUserName(request.getParameter("userName"));
            }
            if (!"null".equals(request.getParameter("loginName"))) {
                servicelog.setLoginName(request.getParameter("loginName"));
            }
            if (!"null".equals(request.getParameter("accessIp"))) {
                servicelog.setAccessIp(request.getParameter("accessIp"));
            }
            if (!"null".equals(request.getParameter("operateType"))) {
                servicelog.setOperateType(request.getParameter("operateType"));
            }
            if (!"null".equals(request.getParameter("stateMsg"))) {
                servicelog.setStateMsg(request.getParameter("stateMsg"));
            }
            servicelog.setFlag(0);
            pageHelper.setObject(servicelog);
            long count = iServicelogService.query(pageHelper).getPageCount();
            int pageSize = 10000; //每次取1W条数据
            int page = 0;
            if (count > pageSize) {
                page = (int) count / pageSize + 1;
            } else {
                page = 1;
            }

            String fileName = "";
            int fileCount = 0;
            float fileSize = 0;
            for (int i = 0; i < page; i++) {
                //文件大于50M时重新生成新文件
                if (fileSize > 50) {
                    fileCount++;
                    fileSize = 0;
                }
                fileName = dirName + dateStr + "_" + fileCount + ".csv";
                pageHelper.setPageNum(i + 1);
                pageHelper.setPageSize(pageSize);
                servicelogList = iServicelogService.query(pageHelper).getObjects();
                fileSize += writeCvs(filePath, fileName, servicelogList, true);
            }
            boolean result = ApachePOIExcelWrite.fileToZip(zipFilePath, filePath, zipName);
            if (result) {
                ApachePOIExcelWrite.download(response, zipFilePath, zipName);
                resp.setRespCode(RespCodeEnum.SUCCESS);
            } else {
                resp.setRespCode(RespCodeEnum.EXCEPTION);
            }
            return resp;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return resp;
    }

    private float writeCvs(String filePath, String fileName, List<Servicelog> servicelogList, Boolean append) {
        String[] header = {"操作时间", "用户账户", "用户名称", "用户IP", "操作类型", "操作路径", "执行结果"};
        List<Object[]> datas = new ArrayList<>();
        for (int i = 0; i < servicelogList.size(); i++) {
            Object[] objects = new Object[header.length];
            Servicelog servicelog = servicelogList.get(i);
            objects[0] = DateTool.getDateToString(servicelog.getAccessTime(), null);
            objects[1] = servicelog.getLoginName();
            objects[2] = servicelog.getUserName();
            objects[3] = servicelog.getAccessIp();
            objects[4] = servicelog.getOperateType();
            objects[5] = servicelog.getAccessPath();
            objects[6] = servicelog.getStateMsg();
            datas.add(objects);
        }
        return CSVUtils.writeCSV(filePath, fileName, header, datas, append);
    }

    @ApiOperation(value = "导出用户登陆日志", notes = "导出用户登陆日志")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/exportLoginLog", method = RequestMethod.GET)
    @ResponseBody
    @OperateType(oprateTypeName = "导出用户登陆日志")
    public RespData exportLoginLog(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RespData resp = new RespData();
        String dateStr = DateTool.getCurrDateString(DateTool.DateFormat_yyyyMMddHHmmss);
        String dirName = "Loginlog";
        String basePath = CSVUtils.getFilePath(path);
        String filePath = basePath + dirName + File.separator + dateStr;
        String zipFilePath = basePath + dirName;
        String zipName = dirName + dateStr + ".zip";
        List<Servicelog> servicelogList = null;
        try {
            PageHelper<Servicelog> pageHelper = new PageHelper<>();
            Servicelog servicelog = new Servicelog();
            if (!"null".equals(request.getParameter("startDate"))&&!"null".equals(request.getParameter("endDate"))){
                pageHelper.setStartDate(request.getParameter("startDate"));
                pageHelper.setEndDate(request.getParameter("endDate"));
            }
            if(!"null".equals(request.getParameter("userName"))){
                servicelog.setUserName(request.getParameter("userName"));
            }
            if(!"null".equals(request.getParameter("loginName"))){
                servicelog.setLoginName(request.getParameter("loginName"));
            }
            if(!"null".equals(request.getParameter("accessIp"))){
                servicelog.setAccessIp(request.getParameter("accessIp"));
            }
            if(!"null".equals(request.getParameter("roleName"))){
                servicelog.setRoleName(request.getParameter("roleName"));
            }
            if(!"null".equals(request.getParameter("stateMsg"))){
                servicelog.setStateMsg(request.getParameter("stateMsg"));
            }
            servicelog.setFlag(1);
            pageHelper.setObject(servicelog);
            long count = iServicelogService.query(pageHelper).getPageCount();
            int pageSize = 10000; //每次取1W条数据
            int page = 0;
            if (count > pageSize) {
                page = (int) count / pageSize + 1;
            } else {
                page = 1;
            }

            String fileName = "";
            int fileCount = 0;
            float fileSize = 0;
            for (int i = 0; i < page; i++) {
                //文件大于50M时重新生成新文件
                if (fileSize > 50) {
                    fileCount++;
                    fileSize = 0;
                }
                fileName = dirName + dateStr + "_" + fileCount + ".csv";
                pageHelper.setPageNum(i + 1);
                pageHelper.setPageSize(pageSize);
                servicelogList = iServicelogService.query(pageHelper).getObjects();
                fileSize += writeCvsLogin(filePath, fileName, servicelogList, true);
            }
            boolean result = ApachePOIExcelWrite.fileToZip(zipFilePath, filePath, zipName);
            if (result) {
                ApachePOIExcelWrite.download(response, zipFilePath, zipName);
                resp.setRespCode(RespCodeEnum.SUCCESS);
            } else {
                resp.setRespCode(RespCodeEnum.EXCEPTION);
            }
            return resp;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return resp;
    }

    private float writeCvsLogin(String filePath, String fileName, List<Servicelog> servicelogList, Boolean append) {
        String[] header = {"登录时间", "登录用户账户","登录用户名称","登陆角色","用户登陆IP","登陆结果"};
        List<Object[]> datas = new ArrayList<>();
        for (int i = 0; i < servicelogList.size(); i++) {
            Object[] objects = new Object[header.length];
            Servicelog servicelog = servicelogList.get(i);
            objects[0] = DateTool.dateToString(servicelog.getAccessTime(),null);
            objects[1] = servicelog.getLoginName();
            objects[2] = servicelog.getUserName();
            objects[3] = servicelog.getRoleName();
            objects[4] = servicelog.getAccessIp();
            objects[5] = servicelog.getStateMsg();
            datas.add(objects);
        }
        return CSVUtils.writeCSV(filePath, fileName, header, datas, append);
    }
}
