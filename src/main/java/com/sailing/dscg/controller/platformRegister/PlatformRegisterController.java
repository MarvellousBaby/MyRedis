package com.sailing.dscg.controller.platformRegister;

import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.platformRegister.PlatformRegister;
import com.sailing.dscg.service.platformRegister.IPlatformRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/14 上午 11:20:35
 */
@Slf4j
@RestController
@RequestMapping(value = "/platformRegister")
@CrossOrigin
@Api(value = "平台管理接口", description = "平台管理接口")
public class PlatformRegisterController {

    @Autowired
    private IPlatformRegisterService iPlatformRegisterService;

    @ApiOperation(value = "查询全部平台", notes = "查询全部平台")
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询全部平台")
    public RespData<PageHelper<PlatformRegister>> queryAll() {
        RespData respData = new RespData();

        try {
            PageHelper<PlatformRegister> registerPageHelper = iPlatformRegisterService.queryAll();
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询全部平台成功！");
            respData.setData(registerPageHelper);
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage());
        }

        return respData;
    }

    @ApiOperation(value = "查询平台", notes = "查询平台")
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询平台")
    public RespData<PageHelper<PlatformRegister>> get(@RequestBody PageHelper<PlatformRegister> platformRegisterPageHelper) {
        RespData respData = new RespData();

        try {
            PageHelper<PlatformRegister> registerPageHelper = iPlatformRegisterService.get(platformRegisterPageHelper);
            respData.setRespCode(RespCodeEnum.SUCCESS);
            respData.setReason("查询平台成功！");
            respData.setData(registerPageHelper);
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage());
        }

        return respData;
    }

    @ApiOperation(value = "保存平台", notes = "保存平台")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "保存平台")
    public RespData save(@RequestBody PageHelper<PlatformRegister> platformRegisterPageHelper) {
        RespData respData = new RespData();
        System.out.println("platformRegisterPageHelper:"+platformRegisterPageHelper);
        try {
            PlatformRegister platformRegister = platformRegisterPageHelper.getObject();
            if(StringUtils.isBlank(platformRegister.getRtpIp())){
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("流媒体地址不能为空！");
                return respData;
            }
            if(StringUtils.isBlank(platformRegister.getRequestIp())){
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("平台Ip不能为空！");
                return respData;

            }
            Boolean result = iPlatformRegisterService.save(platformRegisterPageHelper);
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("保存平台成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("保存平台失败！");
            }
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage());
        }


        return respData;
    }

    @ApiOperation(value = "删除平台", notes = "删除平台")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "删除平台")
    public RespData delete(@RequestBody PlatformRegister platformRegister) {
        RespData respData = new RespData();
        try {
            Boolean result = iPlatformRegisterService.deleteSingle(platformRegister);
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("删除平台成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("删除平台失败！");
            }
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            log.error(e.getMessage());
        }

        return respData;
    }


}
