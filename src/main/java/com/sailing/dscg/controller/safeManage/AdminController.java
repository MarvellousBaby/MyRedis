package com.sailing.dscg.controller.safeManage;

import com.sailing.dscg.common.*;
import com.sailing.dscg.common.entity.SingleProperties;
import com.sailing.dscg.common.secret.Base64Utils;
import com.sailing.dscg.common.secret.RSAUtils;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.page.VerifyUtil;
import com.sailing.dscg.entity.safeManage.Admin;
import com.sailing.dscg.entity.safeManage.MenuRight;
import com.sailing.dscg.entity.safeManage.Role;
import com.sailing.dscg.entity.safeManage.SafeConfig;
import com.sailing.dscg.service.safeManage.IAdminService;
import com.sailing.dscg.service.safeManage.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/safemanage/admin")
@CrossOrigin
@Api(value = "用户管理接口", description = "用户管理接口")
public class AdminController {

    int count = 0;

    //定义全局变量获取验证码
//    private String authCode = null;
    //定义登录次数
//    private int failCount = 0;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IRoleService roleService;
    @Value("${dscg.https}")
    private String https;

    //查询管理员列表
    @ApiOperation(value = "查询管理员列表", notes = "查询管理员列表")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "查询管理员列表")
    public RespData<List<Admin>> queryList(@RequestBody Admin admin, HttpServletRequest request) {
        RespData<List<Admin>> resp = new RespData<List<Admin>>();
        try {
            List<Admin> adminList = adminService.queryList(admin);
            List<Admin> admins = new ArrayList<>();
            Admin adminSession = (Admin) request.getSession().getAttribute("admin");
            if (!adminSession.getLoginName().equals("admin")) {
                for (Admin admin1 : adminList) {
                    if (admin1.getLoginName().equals("admin")) {
                        continue;
                    }
                    admins.add(admin1);
                }
            } else {
                admins = adminList;
            }

            resp.setRespCode(RespCodeEnum.SUCCESS);
            resp.setReason("查询管理员列表成功！");
            resp.setData(admins);
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("查询异常：" + e.getMessage());
            log.error("查询管理员列表异常", e);
        }
        return resp;
    }


    //新增用户
    @ApiOperation(value = "新增管理员", notes = "新增管理员")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "新增管理员")
    public RespData save(@RequestBody Admin admin) {
        RespData resp = new RespData();
        try {
            if (StringUtils.isNotBlank(admin.getPasswd())) {
                String decryptPwd = new String(RSAUtils.decryptByPrivateKey(Base64Utils.decode(admin.getPasswd()), Constants.PRIVATEKEY));
                String passwd = Tools.getMD5(decryptPwd);
                admin.setPasswd(passwd);
            }
            Boolean hasSameLoginName = adminService.hasSameLoginName(admin);
            if (hasSameLoginName) {
                resp.setRespCode(RespCodeEnum.EXCEPTION_303);
            } else {
                Boolean result = adminService.save(admin);
                if (result) {
                    resp.setRespCode(RespCodeEnum.SUCCESS);
                    resp.setReason("新增管理员成功！");
                } else {
                    resp.setRespCode(RespCodeEnum.EXCEPTION_301);
                }
            }
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("保存异常：" + e.getMessage());
            log.error("新增管理员异常", e);
        }
        return resp;
    }


    //更新状态
    @ApiOperation(value = "更新管理员", notes = "更新管理员")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "更新管理员")
    public RespData updateStatus(@RequestBody Admin admin) {
        RespData resp = new RespData();
        try {
            Boolean result = adminService.updateStatus(admin);
            if (result) {
                resp.setRespCode(RespCodeEnum.SUCCESS);
                resp.setReason("更新管理员成功！");
            } else {
                resp.setRespCode(RespCodeEnum.FAIL);
                resp.setReason("更新管理员失败！");
            }
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("更改状态异常：" + e.getMessage());
            log.error("更新管理员异常", e);
        }
        return resp;
    }

    //重置密码
    @ApiOperation(value = "重置密码", notes = "重置密码")
    @RequestMapping(value = "/updatepwd", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "重置密码")
    public RespData updatepwd(@RequestBody Admin admin) {
        RespData resp = new RespData();
        try {
            String decryptPwd = new String(RSAUtils.decryptByPrivateKey(Base64Utils.decode(admin.getPasswd()), Constants.PRIVATEKEY));
            String passwd = Tools.getMD5(decryptPwd);

            admin.setPasswd(passwd);
            Boolean result = adminService.updatepwd(admin);
            if (result) {
                resp.setRespCode(RespCodeEnum.SUCCESS);
                resp.setReason("重置密码成功！");
            } else {
                resp.setRespCode(RespCodeEnum.FAIL);
                resp.setReason("重置密码失败！");
            }
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("重置密码异常：" + e.getMessage());
            log.error("重置密码异常", e);
        }
        return resp;
    }

    //    删除用户
    @ApiOperation(value = "删除管理员", notes = "删除管理员")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "删除管理员")
    public RespData delete(@RequestBody Admin admin,HttpServletRequest request) {
        RespData resp = new RespData();
        try {
            HttpSession session = request.getSession();
            Admin adminCache = (Admin)session.getAttribute("admin");

            if (adminCache.getLoginName().equals(admin.getLoginName())){
                resp.setRespCode(RespCodeEnum.FAIL);
                resp.setReason("不能删除当前登陆的用户！");
                return resp;
            }
            Boolean result = adminService.delete(admin);
            if (result) {
                resp.setRespCode(RespCodeEnum.SUCCESS);
                resp.setReason("删除管理员成功！");
            } else {
                resp.setRespCode(RespCodeEnum.FAIL);
                resp.setReason("删除管理员失败！");

            }
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("删除账号异常："+e.getMessage());
            log.error("删除管理员异常", e);
        }
        return resp;

    }

    //用户登录
    @ApiOperation(value = "管理员登陆", notes = "管理员登陆")
    @RequestMapping(value = "/adminlogin", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "管理员登陆")
    public RespData adminlogin(@RequestBody Admin admin, HttpServletRequest request) {
        RespData resp = new RespData();
        try {
            String decryptPwd = new String(RSAUtils.decryptByPrivateKey(Base64Utils.decode(admin.getPasswd()), Constants.PRIVATEKEY));
            String passwd = Tools.getMD5(decryptPwd);
            admin.setPasswd(passwd);
            SafeConfig safeConfig = CacheUtils.get("safeConfig", SafeConfig.class);

            Admin admin1 = adminService.getByLoginName(admin);
            if (admin1 == null) {
                admin.setRole(new Role());
                request.getSession().setAttribute("loginAdmin", admin);
                resp.setRespCode(RespCodeEnum.EXCEPTION_302);
                return resp;
            }
            request.getSession().setAttribute("loginAdmin", admin1);
            //用户状态禁用不得登入
            int user_status = admin1.getStatus();
            if (user_status == 1) {
                resp.setRespCode(RespCodeEnum.BAN_USER);
                return resp;
            }

            //登陆校验
            Boolean result = adminService.adminLogin(admin);
            //查询失败次数
            Integer loginFailCount = safeConfig.getLoginFail();

            Integer failCount = admin1.getFailCount();

            //校验上次的登陆时间与本次的时间是否相差30min，超过的话错误次数置为1
            Date dateLast = admin1.getLastLoginTime();
            Date dateNow = new Date();
            if (failCount > 1 && (dateNow.getTime() - dateLast.getTime() > 30 * 60 * 1000)) {
                failCount = 1;
                admin1.setFailCount(failCount);
                adminService.updateFailCount(admin1);
            }

            //如果存在失败次数限制
            if (loginFailCount != 0) {
                if (failCount != null && loginFailCount != null && failCount <= loginFailCount) {
                    if (result) {
                        request.getSession().setAttribute("admin", admin);
                        resp.setRespCode(RespCodeEnum.SUCCESS);
                        resp.setReason("登陆成功！");
                        failCount = 1;
                        admin1.setFailCount(failCount);
                        adminService.updateFailCount(admin1);
                    } else {
                        ++failCount;
                        admin1.setFailCount(failCount);
                        admin1.setLastLoginTime(new Date());
                        adminService.updateFailCount(admin1);
                        resp.setData(loginFailCount - failCount + 1);
                        resp.setRespCode(RespCodeEnum.EXCEPTION_306);

                    }
                } else {
                    admin1.setStatus(1);
                    adminService.updateStatus(admin1);
                    resp.setRespCode(RespCodeEnum.BAN_USER);
                }

            } else {
                if (result) {
                    resp.setRespCode(RespCodeEnum.SUCCESS);
                    resp.setReason("登陆成功！");
                } else {
                    resp.setRespCode(RespCodeEnum.EXCEPTION_306);

                }
            }

            if (resp.getCode() == RespCodeEnum.SUCCESS.getCode()) {
                admin1.setPasswd("");
                resp.setData(admin1);
                Admin admin2 = new Admin();
                admin2.setUserName(admin1.getUserName());
                admin2.setLoginName(admin1.getLoginName());
                request.getSession().setAttribute("admin", admin1);
                request.getSession().setAttribute("longTime", DateTool.getCurrentLongTime());
            }

        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("用户登陆异常：" + e.getMessage());
            log.error("用户登陆异常", e);
        }
        return resp;
    }

    //生成验证码返回给前端
    @ApiOperation(value = "生成验证码返回给前端", notes = "生成验证码返回给前端")
    @RequestMapping(value = "/getCode", method = RequestMethod.GET)
    public void getCode(HttpServletResponse response, HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        //利用图片工具生成图片
        //第一个参数是生成的验证码，第二个参数是生成的图片`
        Object[] objs = VerifyUtil.createImage();
        //将验证码存入Session
        session.setAttribute("imageCode", objs[0]);
//        authCode = session.getAttribute("imageCode");
//        System.out.println(count+":AuthCode:" + authCode);
//        ++count;
        //将图片输出给浏览器
        BufferedImage image = (BufferedImage) objs[1];
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(image, "png", os);
    }

    /**
     * 退出登陆
     */
    @ApiOperation(value = "用户退出登陆", notes = "用户退出登陆")
    @RequestMapping(value = "/loginOut", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "用户退出登陆")
    public RespData loginOut(HttpServletRequest request) {
        RespData resp = new RespData();
        request.getSession().removeAttribute("admin");
        resp.setRespCode(RespCodeEnum.SUCCESS);
        resp.setReason("用户退出登陆成功");
        return resp;
    }

    /**
     * 用户未登陆或seesion已失效
     */
    @ApiOperation(value = "用户未登陆或seesion已失效", notes = "用户未登陆或seesion已失效")
    @RequestMapping(value = "/unAdmin", method = RequestMethod.POST)
//    @OperateType(oprateTypeName = "无效访问")
    public RespData unAdmin(HttpServletRequest request) {
        RespData resp = new RespData();
        resp.setRespCode(RespCodeEnum.UNADMIN);
        return resp;
    }

    /**
     * 校验seesion 仅用于前台校验Session
     * 如果通过拦截器则表示正常
     */
    @ApiOperation(value = "校验seesion 仅用于前台校验Session", notes = "校验seesion 仅用于前台校验Session")
    @RequestMapping(value = "/checkSession", method = RequestMethod.POST)
//    @OperateType(oprateTypeName = "校验用户")
    public RespData checkAdmin(HttpServletRequest request) {
        RespData resp = new RespData();
        resp.setRespCode(RespCodeEnum.SUCCESS);
        return resp;
    }

    /**
     * 获取用户菜单权限
     */
    @ApiOperation(value = "获取用户菜单权限", notes = "获取用户菜单权限")
    @RequestMapping(value = "/getMenuRights", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "获取用户菜单权限")
    public RespData<List<MenuRight>> getMenuRights(@RequestBody SingleProperties singleProperties) {
        RespData<List<MenuRight>> resp = new RespData<List<MenuRight>>();
        try {
            Admin admin = new Admin();
            admin.setId(singleProperties.getId());
            Admin admin1 = adminService.get(admin);
            Role role1 = new Role();
            role1.setId(admin1.getRole().getId());
            Role role = roleService.get(role1);

            resp.setRespCode(RespCodeEnum.SUCCESS);
            resp.setReason("获取用户菜单成功!");
            resp.setData(role.getMenuRight());
        } catch (Exception e) {
            resp.setRespCode(RespCodeEnum.EXCEPTION);
            resp.setReason("获取用户菜单异常!" + e.getMessage());
            log.error("获取用户菜单异常", e);
        }
        return resp;
    }

    //单独的验证码校验
    @ApiOperation(value = "单独的验证码校验", notes = "单独的验证码校验")
    @RequestMapping(value = "/checkCode", method = RequestMethod.POST)
    public RespData checkCode(@RequestBody SingleProperties singleProperties, HttpServletRequest request) {
        RespData respData = new RespData();
        HttpSession session = request.getSession();
        String imageCode = (String) session.getAttribute("imageCode");
        if (StringUtils.isNotBlank(singleProperties.getAuthCode())) {
            if (singleProperties.getAuthCode().equalsIgnoreCase(imageCode)) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
            } else {
                respData.setRespCode(RespCodeEnum.EXCEPTION);
                respData.setReason("校验码验证失败");
            }
        } else {
            respData.setRespCode(RespCodeEnum.FAIL);
            respData.setReason("请输入验证码");
        }
        return respData;
    }


    //证书登陆
    @ApiOperation(value = "证书登录", notes = "证书登录")
    @RequestMapping(value = "/checkCert", method = RequestMethod.POST)
    @OperateType(oprateTypeName = "证书登录")
    public RespData checkCert(HttpServletRequest request) throws Exception {
        RespData resp = new RespData();
        String loginName = "";
        if (StringUtils.isBlank(request.getHeader("Client-s-dn-legacy"))) {
            resp.setRespCode(RespCodeEnum.FAIL);
            resp.setReason("证书信息为空，请确保浏览器已导入证书！");
            return resp;
        } else {
            String subject = request.getHeader("Client-s-dn-legacy");
            loginName = subject.substring(subject.indexOf("CN=") + "CN=".length());
        }
        Admin admin = adminService.getByLoginName(loginName);
        if (admin == null) {
            resp.setRespCode(RespCodeEnum.FAIL);
            resp.setReason("证书信息无效，请确保有效证书！");
        } else {
            resp.setRespCode(RespCodeEnum.SUCCESS);
        }

        if (resp.getCode() == RespCodeEnum.SUCCESS.getCode()) {
            resp.setData(admin);
            request.getSession().setAttribute("admin", admin);
            request.getSession().setAttribute("longTime", DateTool.getCurrentLongTime());
        }

        return resp;
    }

    //获取httpsIP端口
    @ApiOperation(value = "获取httpsIP端口", notes = "获取httpsIP端口")
    @RequestMapping(value = "/getHttps", method = RequestMethod.POST)
    public RespData getHttps(HttpServletRequest request) throws Exception {
        RespData resp = new RespData();
        resp.setRespCode(RespCodeEnum.SUCCESS);
        resp.setData(https);
        return resp;
    }


}