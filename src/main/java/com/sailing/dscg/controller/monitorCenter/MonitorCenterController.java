package com.sailing.dscg.controller.monitorCenter;

import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.common.entity.SingleProperties;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.monitoring.MonitorCount;
import com.sailing.dscg.entity.monitoring.MonitorGraphic;
import com.sailing.dscg.entity.monitoring.RefuseCata;
import com.sailing.dscg.entity.monitoring.test.GraphicMonitor;
import com.sailing.dscg.service.monitorCenter.IMonitorCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/26 下午 03:16:31
 */
@RestController
@RequestMapping(value = "/monitor")
@Slf4j
@CrossOrigin
@Api(value = "首页监控接口", description = "首页监控接口")
public class MonitorCenterController {

    @Autowired
    private IMonitorCenterService iMonitorCenterService;

//    @ApiOperation(value = "查询监控数量", notes = "查询监控数量")
//    @RequestMapping(value = "/queryMonitorCount", method = RequestMethod.POST)
//    public RespData queryMonitorCount() {
//        RespData respData = new RespData();
//
//        MonitorCount monitorCount = iMonitorCenterService.queryMonitorCount();
//        respData.setRespCode(RespCodeEnum.SUCCESS);
//        respData.setData(monitorCount);
//
//        return respData;
//
//    }

//    @RequestMapping(value = "/queryMonitorGraphic", method = RequestMethod.POST)
//    @OperateType(oprateTypeName = "监控中心图像查询")
//    public RespData queryMonitorGraphic(@RequestBody int model) {
//        RespData respData = new RespData();
//        try {
//            List<MonitorGraphic> monitorGraphicList = iMonitorCenterService.queryMonitorGraphic(model);
//            respData.setRespCode(RespCodeEnum.SUCCESS);
//            respData.setData(monitorGraphicList);
//        } catch (Exception e) {
//            respData.setRespCode(RespCodeEnum.EXCEPTION);
//            log.error(e.getMessage(), e);
//        }
//
//        return respData;
//
//    }

    @ApiOperation(value = "监控中心图像查询新", notes = "监控中心图像查询新")
    @RequestMapping(value = "/getMonitorGraphic", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "监控中心图像查询新")
    public RespData getMonitorGraphic(@RequestBody SingleProperties singleProperties) {
        RespData respData = new RespData();
        try {
            List<GraphicMonitor> graphicMonitorList = iMonitorCenterService.getMonitorGraphic(singleProperties.getModel());
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询成功！");
            respData.setData(graphicMonitorList);
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }

        return respData;

    }

    @ApiOperation(value = "查询拒绝策略", notes = "查询拒绝策略")
    @RequestMapping(value = "/getRefuseCata", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询拒绝策略")
    public RespData getRefuseCata(HttpServletRequest request) {
        RespData respData = new RespData();
        String serviceId = request.getParameter("serviceId");
        try {
            RefuseCata refuseCata = iMonitorCenterService.getRefuseCata(serviceId);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询拒绝策略成功！");
            respData.setData(refuseCata);
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }

        return respData;

    }

    //&useAffectedRows=true
    @ApiOperation(value = "保存拒绝策略", notes = "保存拒绝策略")
    @RequestMapping(value = "/saveRefuseCata", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "保存拒绝策略")
    public RespData saveRefuseCata(@RequestBody RefuseCata refuseCata) {
        RespData respData = new RespData();
        try {
            Boolean result = iMonitorCenterService.saveRefuseCata(refuseCata);
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("保存拒绝策略成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("保存拒绝策略失败！");
            }
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }

        return respData;

    }


}
