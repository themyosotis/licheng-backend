package cn.ujn.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("cn.ujn.usercenter.mapper")
@SpringBootApplication
@EnableScheduling // 开启任务调度，可以使用定时任务等
public class UsercenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsercenterApplication.class, args);
    }

}
