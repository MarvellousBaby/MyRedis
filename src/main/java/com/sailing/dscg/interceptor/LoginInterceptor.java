package com.sailing.dscg.interceptor;

import com.sailing.dscg.common.CacheUtils;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.entity.safeManage.Admin;
import com.sailing.dscg.entity.safeManage.SafeConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        Admin admin = (Admin)session.getAttribute("admin");
//        if(admin!=null){
//            /**session超时*/
//            Boolean sessionTimeOut = sessionTimeOut(session);
//            if(sessionTimeOut){
//                session.removeAttribute("admin");
//                httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/safemanage/admin/unAdmin");
//                return false;
//            }
//            session.setAttribute("longTime",DateTool.getCurrentLongTime());
//            return true;
//        }else{
//            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/safemanage/admin/unAdmin");
//            return false;
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

    private Boolean sessionTimeOut(HttpSession session){
        long lastTime = (Long)session.getAttribute("longTime");
        long currTime = DateTool.getCurrentLongTime();
        SafeConfig  safeConfig = CacheUtils.get("safeConfig",SafeConfig.class);
        Integer sessionTimeout = safeConfig.getSessionTimeout();
        if(sessionTimeout==null)sessionTimeout=30;
        if((currTime-lastTime)>(sessionTimeout*60*1000)){
            return true;
        }else{
            return false;
        }
    }


}