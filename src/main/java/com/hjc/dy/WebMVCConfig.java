package com.hjc.dy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @Author: hjc
 * @Date: 2018/11/25 15:36
 * @Version 1.0
 */
@Configuration
public class WebMVCConfig extends WebMvcConfigurationSupport {

    /**
     * 配置静态资源
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").
                addResourceLocations("classpath:resources/").
                addResourceLocations("file:D:/dy_image/");
        super.addResourceHandlers(registry);
    }

}
