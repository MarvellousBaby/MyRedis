package com.sailing.dscg.controller.system;

import com.sailing.dscg.common.Constants;
import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.common.entity.SingleProperties;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.system.ClusterConfig;
import com.sailing.dscg.service.system.IClusterConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 集群配置
 * @Auther:史俊华
 * @Date:2018/6/271919
 */
@RestController
@RequestMapping(value = "/sys/cluster")
@CrossOrigin
@Api(value = "集群配置接口", description = "集群配置接口")
public class ClusterConfigController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IClusterConfigService clusterConfigService;

    /**
     * 查询集群配置列表
     */
    @ApiOperation(value = "查询集群配置列表", notes = "查询集群配置列表")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询集群配置列表")
    public RespData<List<ClusterConfig>> queryConfig(@RequestBody ClusterConfig clusterConfig) {
        RespData<List<ClusterConfig>> resp = new RespData<List<ClusterConfig>>();
        try {
            List<ClusterConfig> list = clusterConfigService.queryList(clusterConfig);
            resp.setRespCode(RespCodeEnum.SUCCESS);
            resp.setReason("查询集群配置列表成功！");
            resp.setData(list);
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("查询异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    /**
     * 保存集群配置
     */
    @ApiOperation(value = "保存集群配置", notes = "保存集群配置")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "保存集群配置")
    public RespData addConfig(@RequestBody ClusterConfig clusterConfig) {
        RespData resp = new RespData();
        Boolean hasSameIp = false;
        try {
            if (Constants.CLUSTER_CONFIG_TYPE_VSCG.equals(clusterConfig.getType())){
                hasSameIp = clusterConfigService.hasSameIp(clusterConfig);
            }else if (Constants.CLUSTER_CONFIG_TYPE_GATEWAY.equals(clusterConfig.getType())) {
                hasSameIp = clusterConfigService.hasSameInIp(clusterConfig);
            }
            if (!hasSameIp) {
                Boolean result = clusterConfigService.save(clusterConfig);
                if (result != null && result) {
                    resp.setRespCode(RespCodeEnum.SUCCESS);
                    resp.setReason("保存集群配置成功！");
                } else {
                    resp.setRespCode(RespCodeEnum.FAIL);
                    resp.setReason("保存集群配置失败！");
                }
            } else {
                resp.setRespCode(RespCodeEnum.CODE_7300);
            }
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("保存异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    /**
     * 删除dscg集群配置
     */
    @ApiOperation(value = "删除网闸配置", notes = "删除网闸配置")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/deleteGateWay", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "删除网闸配置")
    public RespData deleteDscgConfig(@RequestBody ClusterConfig clusterConfig) {
        RespData resp = new RespData();
        String id = clusterConfig.getId();
        if (StringUtils.isBlank(id)) {
            resp.setRespCode(RespCodeEnum.PARAM_FAIL);
            return resp;
        }
        try {
            Boolean result = clusterConfigService.deleteGateWay(clusterConfig);
            if (result != null && result) {
                resp.setRespCode(RespCodeEnum.SUCCESS);
                resp.setReason("删除网闸配置成功！");
            } else {
                resp.setRespCode(RespCodeEnum.FAIL);
                resp.setReason("删除网闸配置失败！");
            }

        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("删除异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    /**
     * 删除集群配置
     */
    @ApiOperation(value = "删除节点配置", notes = "删除节点配置")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "删除节点配置")
    public RespData deleteConfig(@RequestBody ClusterConfig clusterConfig) {
        RespData resp = new RespData();
        String id = clusterConfig.getId();
        if (StringUtils.isBlank(id)) {
            resp.setRespCode(RespCodeEnum.PARAM_FAIL);
            return resp;
        }
        try {
            Boolean result = clusterConfigService.delete(clusterConfig);
            if (result != null && result) {
                resp.setRespCode(RespCodeEnum.SUCCESS);
                resp.setReason("删除节点配置成功！");
            } else {
                resp.setRespCode(RespCodeEnum.FAIL);
                resp.setReason("删除节点配置失败！");
            }

        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("删除异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }


    /**
     * 查询集群树
     */
    @ApiOperation(value = "获取集群树", notes = "获取集群树")
    @RequestMapping(value = "/clusterNodeTree", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "获取集群树")
    public RespData<List<String>> clusterNodeTree(@RequestBody SingleProperties singleProperties) {
        RespData<List<String>> resp = new RespData<>();
        try {
            List<String> list = clusterConfigService.clusterNodeTree(singleProperties.getType());
            resp.setRespCode(RespCodeEnum.SUCCESS);
            resp.setData(list);
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("获取数据异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }
}
