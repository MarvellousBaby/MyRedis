package com.sailing.dscg.controller.safeManage;

import com.sailing.dscg.common.RespCodeEnum;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.safeManage.Role;
import com.sailing.dscg.service.safeManage.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/safemanage/role")
@CrossOrigin
@Api(value = "角色管理接口", description = "角色管理接口")
public class RoleController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IRoleService roleService;


    /**
     * 查询安全管理角色管理列表
     */
    @ApiOperation(value = "查询安全管理角色管理列表", notes = "查询安全管理角色管理列表")
    @RequestMapping(value = "/queryAll", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询安全管理角色管理列表")
    public RespData<List<Role>> queryAll() {
        RespData<List<Role>> resp = new RespData<List<Role>>();
        try {
            List<Role> list = roleService.queryAll();
            resp.setRespCode(RespCodeEnum.SUCCESS);
            resp.setReason("查询安全管理角色管理列表成功！");
            resp.setData(list);

        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("查询异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    /**
     * 查询安全管理角色管理列表
     */
    @ApiOperation(value = "查询单个安全管理角色", notes = "查询单个安全管理角色")
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询单个安全管理角色")
    public RespData<Role> get(@RequestBody Role config) {
        RespData<Role> resp = new RespData<Role>();
        try {
            Role manage = roleService.get(config);
            resp.setRespCode(RespCodeEnum.SUCCESS);
            resp.setReason("查询单个安全管理角色管理列表成功！");
            resp.setData(manage);

        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("查询异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    /**
     * 增加和修改安全管理角色管理信息
     */
    @ApiOperation(value = "保存和修改安全管理角色管理信息", notes = "保存和修改安全管理角色管理信息")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "保存和修改安全管理角色管理信息")
    public RespData save(@RequestBody List<Role> configs) {
        RespData resp = new RespData();
        int count = 0;
        try {
            for (Role config : configs) {
                Boolean result = roleService.save(config);
                if (result) {
                    count++;
                }
            }
            if (count == 3) {
                resp.setRespCode(RespCodeEnum.SUCCESS);
                resp.setReason("保存和修改安全管理角色管理信息成功！");
            } else {
                resp.setRespCode(RespCodeEnum.FAIL);
                resp.setReason("保存和修改安全管理角色管理信息失败！");
            }
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("保存角色异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }

    /**
     * 删除安全管理角色管理信息
     */
    @ApiOperation(value = "删除安全管理角色管理信息", notes = "删除安全管理角色管理信息")
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "删除安全管理角色管理信息")
    public RespData delete(@RequestBody Role config) {
        RespData resp = new RespData();
        String _id = config.getId();
        if (StringUtils.isBlank(_id)) {
            resp.setRespCode(RespCodeEnum.PARAM_FAIL);
            return resp;
        }
        try {
            Boolean result = roleService.delete(config);
            if (result) {
                resp.setRespCode(RespCodeEnum.SUCCESS);
                resp.setReason("删除安全管理角色管理信息成功！");
            } else {
                resp.setRespCode(RespCodeEnum.FAIL);
                resp.setReason("删除安全管理角色管理信息失败！");
            }
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("删除角色异常：" + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return resp;
    }


}
