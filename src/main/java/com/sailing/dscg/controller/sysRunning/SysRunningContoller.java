package com.sailing.dscg.controller.sysRunning;

import com.sailing.dscg.common.CSVUtils;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.common.entity.SingleProperties;
import com.sailing.dscg.common.page.ApachePOIExcelWrite;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.ApplicationProperties;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.sysRunning.SysRunning;
import com.sailing.dscg.service.SysRunning.ISysRunningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/14 下午 02:45:07
 */
@RestController
@RequestMapping(value = "/sysRunning")
@CrossOrigin
@Slf4j
@Api(value = "服务运行日志接口", description = "服务运行日志接口")
public class SysRunningContoller {

    //导出路径
    @Value("${dscg.excel}")
    String path;
    @Autowired
    private ISysRunningService iSysRunningService;
    @Autowired
    private ApplicationProperties properties;

    @ApiOperation(value = "删除服务运行日志", notes = "删除服务运行日志")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "删除服务运行日志")
    public RespData queryCount(@RequestBody SingleProperties singleProperties) {
        RespData respData = new RespData();
        try {
            Boolean result = iSysRunningService.delete(singleProperties.getStrIds());
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("删除服务运行日志成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("删除服务运行日志失败！");
            }
        } catch (Exception e) {
            log.error("删除服务运行日志失败:" + e.getMessage());
        }

        return respData;
    }

    @ApiOperation(value = "查询服务运行日志", notes = "查询服务运行日志")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询服务运行日志")
    public RespData queryList(@RequestBody PageHelper<SysRunning> sysRunningPageHelper) {
        RespData respData = new RespData();
        try {
            sysRunningPageHelper = iSysRunningService.queryList(sysRunningPageHelper);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询服务运行日志成功！");
            respData.setData(sysRunningPageHelper);
        } catch (Exception e) {
            log.error("查询服务运行日志失败:" + e.getMessage());
        }

        return respData;
    }

    @ApiOperation(value = "查询路径", notes = "查询路径")
    @RequestMapping(value = "/queryPath", method = RequestMethod.POST)
    public RespData queryPath() {
        RespData respData = new RespData();
        try {
            List<String> paths = iSysRunningService.queryPath();
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setData(paths);
        } catch (Exception e) {
            log.error("查询传输路径失败:" + e.getMessage());
        }

        return respData;
    }

    @ApiOperation(value = "清空服务运行日志", notes = "清空服务运行日志")
    @RequestMapping(value = "/deleteAll", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "清空服务运行日志")
    public RespData queryDetail() {
        RespData respData = new RespData();
        try {
            Boolean result = iSysRunningService.deleteAll();
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("清空服务运行日志成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("清空服务运行日志失败！");
            }
        } catch (Exception e) {
            log.error("清空服务运行日志失败:" + e.getMessage());
        }

        return respData;
    }

    @ApiOperation(value = "导出系统运行日志", notes = "导出系统运行日志")
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @RequestMapping(value = "/exportSysRunning", method = RequestMethod.GET)
    @OperateType(oprateTypeName = "导出系统运行日志")
    public RespData exportAuditCenter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RespData resp = new RespData();
        String dateStr = DateTool.getCurrDateString(DateTool.DateFormat_yyyyMMddHHmmss);
        String dirName = "SysRunning";
        String basePath = CSVUtils.getFilePath(path);
        String filePath = basePath + dirName + File.separator + dateStr;
        String zipFilePath = basePath + dirName;
        String zipName = dirName + dateStr + ".zip";
        List<SysRunning> sysRunningList = null;
        try {
            PageHelper<SysRunning> pageHelper = new PageHelper<>();
            SysRunning sysRunning = new SysRunning();
            if (!"null".equals(request.getParameter("serviceName"))) {
                sysRunning.setServiceName(request.getParameter("serviceName"));

            }
            if (!"null".equals(request.getParameter("platName"))) {
                sysRunning.setPlatName(request.getParameter("platName"));

            }
            if (!"null".equals(request.getParameter("transferPath"))) {
                sysRunning.setTransferPath(request.getParameter("transferPath"));

            }
            if (!"null".equals(request.getParameter("startTime")) && !"null".equals(request.getParameter("endTime"))) {
                sysRunning.setTransportStartTime(request.getParameter("startTime"));
                sysRunning.setTransportStopTime(request.getParameter("endTime"));
            }
            pageHelper.setObject(sysRunning);

            long count = iSysRunningService.queryList(pageHelper).getPageCount();
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
                fileName = dirName+dateStr + fileCount + ".csv";
                pageHelper.setPageNum(i + 1);
                pageHelper.setPageSize(pageSize);
                sysRunningList = iSysRunningService.queryList(pageHelper).getObjects();
                fileSize += writeCvs(filePath, fileName, sysRunningList, true);
            }
            boolean result = ApachePOIExcelWrite.file2Zip(zipFilePath, filePath, zipName);
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

    private float writeCvs(String filePath, String fileName, List<SysRunning> sysRunningList, Boolean append) {
        String[] header = {"服务名称", "平台名称", "开始时间", "访问时长（s）", "流量大小（MB）","终端地址","操作类型","传输路径"};
        List<Object[]> datas = new ArrayList<>();
        for (int i = 0; i < sysRunningList.size(); i++) {
            Object[] objects = new Object[8];
            SysRunning sysRunning = sysRunningList.get(i);
            objects[0] = sysRunning.getServiceName();
            objects[1] = sysRunning.getPlatName();
            objects[2] = sysRunning.getTransportStartTime();
            objects[3] = sysRunning.getVisitTime();
            objects[4] = sysRunning.getTransportSize();
            objects[5] = sysRunning.getTargetIp();
            objects[6] = sysRunning.getRequestType();
            objects[7] = sysRunning.getTransferPath();
            datas.add(objects);
        }
        return CSVUtils.writeCSV(filePath, fileName, header, datas, append);
    }


}
