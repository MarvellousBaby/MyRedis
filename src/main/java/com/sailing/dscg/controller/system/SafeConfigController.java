package com.sailing.dscg.controller.system;

import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.safeManage.SafeConfig;
import com.sailing.dscg.service.safeManage.ISafeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:安全管理配置控制器
 * <p>
 * Update by Panyu on 2018/7/24 上午 11:01:11
 */

@RestController
@RequestMapping(value = "/sys/safeconfig")
@CrossOrigin
@Api(value = "安全管理配置控制器", description = "安全管理配置控制器")
public class SafeConfigController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ISafeConfigService iSafeConfigService;

    @ApiOperation(value = "安全配置管理保存", notes = "安全配置管理保存")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "安全配置管理保存")
    public RespData save(@RequestBody SafeConfig safeConfig) {
        RespData respData = new RespData();
        try {
            Boolean result = iSafeConfigService.save(safeConfig);
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("安全配置管理保存成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("安全配置管理保存失败！");
            }
            respData.setData(result);
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            respData.setReason("保存异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return respData;
    }

    @ApiOperation(value = "查询安全管理配置", notes = "查询安全管理配置")
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询安全管理配置")
    public RespData<SafeConfig> get() {
        RespData<SafeConfig> respData = new RespData<>();
        try {
            SafeConfig safeConfig = new SafeConfig();
            List<SafeConfig> list = iSafeConfigService.queryList();
            if (list != null && !list.isEmpty()) {
                safeConfig = list.get(0);
            }
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询安全管理配置成功！");

            respData.setData(safeConfig);

        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            respData.setReason("查询异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return respData;
    }


}
