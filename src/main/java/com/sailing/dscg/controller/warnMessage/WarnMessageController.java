package com.sailing.dscg.controller.warnMessage;

import com.sailing.dscg.common.CSVUtils;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.common.entity.SingleProperties;
import com.sailing.dscg.common.page.ApachePOIExcelWrite;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.ApplicationProperties;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.videoTransmission.VideoTransmission;
import com.sailing.dscg.entity.warnMessage.WarnMessage;
import com.sailing.dscg.service.warnMessage.IWarnMessageService;
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
 * Update by Panyu on 2018/9/25 下午 06:20:04
 */
@RestController
@RequestMapping(value = "/warnMessage")
@CrossOrigin
@Slf4j
@Api(value = "告警信息管理", description = "告警信息管理")
public class WarnMessageController {

    //导出路径
    @Value("${dscg.excel}")
    String path;
    @Autowired
    private IWarnMessageService iWarnMessageService;
    @Autowired
    private ApplicationProperties properties;

//    @RequestMapping(value = "/queryCount", method = RequestMethod.POST)
//    public RespData queryCount(@RequestBody PageHelper<WarnMessage> warnMessagePageHelper) {
//        RespData respData = new RespData();
//
//        try {
//            long count = iWarnMessageService.queryCount(warnMessagePageHelper);
//            respData.setRespCode(RespCodeEnum.SUCCESS);
//            respData.setData(count);
//        } catch (Exception e) {
//            log.error("查询异常告警总条数失败：" + e.getMessage());
//            respData.setRespCode(RespCodeEnum.EXCEPTION);
//        }
//
//        return respData;
//    }

    @ApiOperation(value = "清空异常告警列表", notes = "清空异常告警列表")
    @RequestMapping(value = "/deleteAll", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "清空异常告警列表")
    public RespData deleteAll() {
        RespData respData = new RespData();

        try {
            boolean result = iWarnMessageService.deleteAll();
            if (result){
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("清空异常告警列表成功！");
            }else{
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("清空异常告警列表失败！");

            }
        } catch (Exception e) {
            log.error("清空异常告警失败：" + e.getMessage());
            respData.setRespCode(RespCodeEnum.EXCEPTION);
        }

        return respData;
    }

    @ApiOperation(value = "查询异常告警列表", notes = "查询异常告警列表")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询异常告警列表")
    public RespData queryList(@RequestBody PageHelper<WarnMessage> warnMessagePageHelper) {
        RespData respData = new RespData();

        try {
            warnMessagePageHelper = iWarnMessageService.queryList(warnMessagePageHelper);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询异常告警列表成功！");
            respData.setData(warnMessagePageHelper);
        } catch (Exception e) {
            log.error("查询异常告警失败：" + e.getMessage());
            respData.setRespCode(RespCodeEnum.EXCEPTION);
        }

        return respData;
    }

    @ApiOperation(value = "删除异常告警列表", notes = "删除异常告警列表")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "删除异常告警列表")
    public RespData delete(@RequestBody SingleProperties singleProperties) {
        RespData respData = new RespData();

        try {
            Boolean result = iWarnMessageService.delete(singleProperties.getStrIds());
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("删除异常告警列表成功！");
            }else{
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("删除异常告警列表失败！");
            }
        } catch (Exception e) {
            log.error("删除异常告警失败：" + e.getMessage());
            respData.setRespCode(RespCodeEnum.EXCEPTION);
        }

        return respData;
    }

    @ApiOperation(value = "认证类型名称", notes = "认证类型名称")
    @RequestMapping(value = "/verifyTypeName", method = RequestMethod.POST)
    public RespData<List<String>> operateType() {
        RespData<List<String>> respData = new RespData<List<String>>();
        try {
            List<String> list = iWarnMessageService.verifyType();
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询认证类型名称成功！");
            respData.setData(list);

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }

        return respData;
    }

    @ApiOperation(value = "导出异常告警日志", notes = "导出异常告警日志")
    @SuppressWarnings("rawtypes")
    @ResponseBody
    @RequestMapping(value = "/exportVideo", method = RequestMethod.GET)
    @OperateType(oprateTypeName = "导出异常告警日志")
    public RespData exportAuditCenter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RespData resp = new RespData();
        String dateStr = DateTool.getCurrDateString(DateTool.DateFormat_yyyyMMddHHmmss);
        String dirName = "WarnMessage";
        String basePath = CSVUtils.getFilePath(path);
        String filePath = basePath + dirName + File.separator + dateStr;
        String zipFilePath = basePath + dirName;
        String zipName = dirName + dateStr + ".zip";
        List<WarnMessage> warnMessageList = null;
        try {
            PageHelper<WarnMessage> pageHelper = new PageHelper<>();
            WarnMessage warnMessage = new WarnMessage();
            pageHelper.setObject(warnMessage);
            pageHelper.setPageNum(0);

            long count = iWarnMessageService.queryList(pageHelper).getPageCount();
            int pageSize = 10000; //每次取1W条数据
            int page = 0;
            if (count > pageSize) {
                page = (int) count / pageSize+1;
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
                fileName = dateStr + fileCount + ".csv";
                pageHelper.setPageNum(i + 1);
                pageHelper.setPageSize(pageSize);
                warnMessageList = iWarnMessageService.queryList(pageHelper).getObjects();
                fileSize += writeCvs(filePath, fileName, warnMessageList, true);
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

    private float writeCvs(String filePath, String fileName, List<WarnMessage> warnMessageList, Boolean append) {
        String[] header = {"验证日期", "IP地址", "MAC地址", "验证类型", "验证状态"};
        List<Object[]> datas = new ArrayList<>();
        for (int i = 0; i < warnMessageList.size(); i++) {
            Object[] objects = new Object[5];
            WarnMessage warnMessage = warnMessageList.get(i);
            objects[0] = warnMessage.getVerifyDate();
            objects[1] = warnMessage.getIpAddress();
            objects[2] = warnMessage.getMacAddress();
            objects[3] = warnMessage.getVerifyType();
            objects[4] = warnMessage.getStatus();

            datas.add(objects);
        }
        return CSVUtils.writeCSV(filePath, fileName, header, datas, append);
    }



}
