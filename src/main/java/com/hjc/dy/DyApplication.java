package com.hjc.dy;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.hjc.dy.mapper")
@ComponentScan(basePackages = "com.hjc")
public class DyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DyApplication.class, args);
    }
}
