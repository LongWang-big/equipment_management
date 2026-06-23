package com.xxx.equipment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xxx.equipment.mapper")
public class EquipmentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EquipmentManagementApplication.class, args);
    }
}
