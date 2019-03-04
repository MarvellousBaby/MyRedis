package com.sailing.dscg.aspect;

import com.alibaba.fastjson.JSONObject;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.Servicelog;
import com.sailing.dscg.entity.anno.OperateType;
import com.sailing.dscg.entity.safeManage.Admin;
import com.sailing.dscg.service.serviceLog.IServicelogService;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Description:获取操作日志相关信息
 * <p>
 * Update by Panyu on 2018/7/17 13:58
 */
@Aspect
@Component
public class Log_Aspect {

    private final String ExpGetResultDataPonit = "execution( * com.sailing.dscg.controller.*.*.*(..))";
    @Autowired
    private IServicelogService servicelogService;

    /**
     * 获取动作
     * Controller下的类的所有的方法
     */
    @Pointcut(ExpGetResultDataPonit)
    public void serviceLogCell() {
    }

    @Around("serviceLogCell()")
    public Object queryListCellLog(ProceedingJoinPoint pjd) throws Throwable {
        Object result = null;
//        ProceedingJoinPoint pjd = (ProceedingJoinPoint) joinPoint;
        try {
            Object[] args = pjd.getArgs();
            result = pjd.proceed(args);

            //添加servicelog日志
            insertServiceLog(pjd, result);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public boolean insertServiceLog(ProceedingJoinPoint pjd, Object object) throws Throwable {

        RespData respData;
        boolean result = false;
        boolean exist = false;

        if (object != null) {
            respData = (RespData) object;

        } else {
            return false;
        }
        //非Servlet类中初始化HttpServletRequest
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        //获取Ip
        String accessIp = request.getHeader("x-forwarded-for");
        if (accessIp == null || accessIp.length() == 0) {
            accessIp = request.getRemoteAddr();
        }

        Admin admin = (Admin) session.getAttribute("admin");
        //获取访问路径
        String accessPath = request.getRequestURI();
        //获取访问方法
        String accessMethod = pjd.getSignature().getName();
        //获取访问参数
        Object[] accessParams = pjd.getArgs();

        //获取操作类型
        String operateType = "";

        //反射获取原方法
        Signature signature = pjd.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();              //  代理方法
        Method realMethod = pjd.getTarget().getClass().getDeclaredMethod(signature.getName(), targetMethod.getParameterTypes());

        if (realMethod.isAnnotationPresent(OperateType.class)) {
            OperateType operate = realMethod.getAnnotation(OperateType.class);
            operateType = operate.oprateTypeName();

        }

        Servicelog servicelog = new Servicelog();

        if (admin == null) {
            servicelog.setUserName("Others");
            servicelog.setLoginName("Others");
            servicelog.setRoleName("");
        } else {
            servicelog.setUserName(admin.getUserName());
            servicelog.setLoginName(admin.getLoginName());
            if(admin.getLoginName().equals("admin")){
                servicelog.setRoleName("管理员");
            }else{
                servicelog.setRoleName(admin.getRole().getName());
            }
        }
        servicelog.setAccessIp(accessIp);
        servicelog.setAccessPath(accessPath);
        servicelog.setAccessMethod(accessMethod);
        servicelog.setAccessTime(DateTool.getCurrentDate());
        if (StringUtils.isBlank(operateType) ) {
            exist = true;
        } else {
            servicelog.setOperateType(operateType);
        }
        if (accessParams != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            for (Object obj:accessParams) {
                if(obj instanceof HttpServletRequest) continue;
                sb.append(JSONObject.toJSONString(obj));
            }
            sb.append("]");
            servicelog.setAccessParams(sb.toString().replaceAll("\\\\",""));
        }

        servicelog.setStateMsg(respData.getReason());
        servicelog.setCreateTime(DateTool.getCurrentDate());
        if (!exist) {
            result = servicelogService.save(servicelog);
        }

        return result;


    }


}
