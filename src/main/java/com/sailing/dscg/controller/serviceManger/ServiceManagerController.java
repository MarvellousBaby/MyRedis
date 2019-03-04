package com.sailing.dscg.controller.serviceManger;

import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.serviceManager.GetGateWay;
import com.sailing.dscg.entity.serviceManager.ServiceManager;
import com.sailing.dscg.entity.system.ClusterConfig;
import com.sailing.dscg.service.serviceManger.IServiceMangerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/12 下午 06:19:02
 */
@RestController
@RequestMapping(value = "/serviceManager")
@CrossOrigin
@Slf4j
@Api(value = "服务配置管理接口", description = "服务配置管理接口")
public class ServiceManagerController {

    @Autowired
    private IServiceMangerService iServiceMangerService;

    @ApiOperation(value = "查询全部服务管理", notes = "查询全部服务管理")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询全部服务管理")
    public RespData queryAll(@RequestBody PageHelper<ServiceManager> pageHelper) {
        RespData respData = new RespData();
        try {
            pageHelper = iServiceMangerService.queryList(pageHelper);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询全部服务管理成功！");
            respData.setData(pageHelper);

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }
        return respData;
    }

    @ApiOperation(value = "查询单个服务管理", notes = "查询单个服务管理")
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询单个服务管理")
    public RespData get(@RequestBody ServiceManager serviceManager) {
        RespData respData = new RespData();
        try {
            serviceManager = iServiceMangerService.get(serviceManager);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询单个服务管理成功！");
            respData.setData(serviceManager);

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }
        return respData;
    }

    @ApiOperation(value = "保存服务管理", notes = "保存服务管理")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "保存服务管理")
    public RespData save(@RequestBody ServiceManager serviceManager) {
        RespData respData = new RespData();
        try {
            Boolean result = iServiceMangerService.save(serviceManager);
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("保存服务管理成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("保存服务管理失败！");
            }

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }
        return respData;
    }

    @ApiOperation(value = "删除服务管理", notes = "删除服务管理")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "删除服务管理")
    public RespData delete(@RequestBody ServiceManager serviceManager) {
        RespData respData = new RespData();
        try {
            Boolean result = iServiceMangerService.delete(serviceManager);
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("删除服务管理成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("删除服务管理失败！");
            }

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }
        return respData;
    }

    @ApiOperation(value = "部署服务管理", notes = "部署服务管理")
    @RequestMapping(value = "/deploy", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "部署服务管理")
    public RespData deploy(@RequestBody ServiceManager serviceManager) {
        RespData respData = new RespData();
        try {
            respData = iServiceMangerService.deploy(serviceManager);
            if (respData.getCode() == 200) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("部署服务管理成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("部署服务管理失败！");
            }

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }
        return respData;
    }

//    @RequestMapping(value = "/getPlats", method = RequestMethod.GET)
//    public RespData getPlats() {
//        RespData respData = new RespData();
//        try {
//            List<PlatformRegister> platformRegisterList = iServiceMangerService.getPlats();
//            respData.setRespCode(RespCodeEnum.SUCCESS);
//            respData.setData(platformRegisterList);
//
//        } catch (Exception e) {
//            respData.setRespCode(RespCodeEnum.EXCEPTION);
//            log.error(e.getMessage(), e);
//        }
//        return respData;
//    }

    @ApiOperation(value = "获取集群", notes = "获取集群")
    @RequestMapping(value = "/getClusters", method = RequestMethod.POST)
    public RespData getClusters() {
        RespData respData = new RespData();
        try {
            List<ClusterConfig> clusterConfigList = iServiceMangerService.getClusters();
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("获取集群成功！");
            respData.setData(clusterConfigList);

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }
        return respData;
    }


    @ApiOperation(value = "获取网闸", notes = "获取网闸")
    @RequestMapping(value = "/getGateWay", method = RequestMethod.POST)
    public RespData getGateWay(@RequestBody GetGateWay getGateWay) {
        RespData respData = new RespData();
        try {
            List<String> ipAndPort = iServiceMangerService.getGateWay(getGateWay);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("获取网闸成功！");
            respData.setData(ipAndPort);

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage(), e);
        }
        return respData;
    }


}
