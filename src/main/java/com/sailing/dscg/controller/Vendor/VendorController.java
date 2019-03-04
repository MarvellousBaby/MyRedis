package com.sailing.dscg.controller.Vendor;

import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.vendor.Vendor;
import com.sailing.dscg.service.vendor.IVendorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/11/7 下午 05:29:38
 */
@RestController
@RequestMapping(value = "/vendor")
@CrossOrigin
@Api(value = "集中配置管理", description = "集中配置管理")
public class VendorController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private IVendorService iVendorService;

    /**
     * 查询安全管理角色管理列表
     */
    @ApiOperation(value = "查询集中配置列表", notes = "查询集中配置列表")
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询集中配置列表")
    public RespData<List<Vendor>> queryAll() {
        RespData<List<Vendor>> resp = new RespData<List<Vendor>>();
        try {
            List<Vendor> list = iVendorService.queryAll();
            if (list == null || list.size()==0){
                list = new ArrayList<>();
            }
            resp.setRespCode(RespCodeEnum.SUCCESS);
            resp.setReason("查询集中配置列表成功！");
            resp.setData(list);

        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("查询异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    @ApiOperation(value = "集中配置管理保存", notes = "集中配置管理保存")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "集中配置管理保存")
    public RespData save(@RequestBody Vendor vendor) {
        RespData respData = new RespData();
        try {
            Boolean result = iVendorService.save(vendor);
            if (result) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setReason("集中配置管理保存成功！");
            } else {
                respData.setRespCode(RespCodeEnum.FAIL);
                respData.setReason("集中配置管理保存失败！");

            }
            respData.setData(result);
        } catch (Exception e) {
            respData.setRespCode(RespCodeEnum.EXCEPTION);
            respData.setReason("保存异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return respData;
    }

}
