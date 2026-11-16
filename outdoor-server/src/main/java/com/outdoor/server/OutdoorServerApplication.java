package com.outdoor.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.outdoor.server.mapper")
public class OutdoorServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OutdoorServerApplication.class, args);
    }
}
