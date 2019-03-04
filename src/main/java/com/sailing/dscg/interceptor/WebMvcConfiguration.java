package com.sailing.dscg.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @Description:
 * @Auther:史俊华
 * @Date:2018/8/1014
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET","POST","OPTIONS")
                .allowCredentials(true).maxAge(3600);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/safemanage/admin/getCode")
                .excludePathPatterns("/safemanage/admin/checkCode")
                .excludePathPatterns("/safemanage/admin/unAdmin")
                .excludePathPatterns("/safemanage/admin/loginOut")
                .excludePathPatterns("/safemanage/admin/adminlogin")
                .excludePathPatterns("/safemanage/admin/getHttps")
                .excludePathPatterns("/safemanage/admin/checkCert");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/js/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
