package cn.ujn.licheng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("cn.ujn.licheng.mapper")
@SpringBootApplication
@EnableScheduling // 开启任务调度，可以使用定时任务等
public class LichengApplication {

    public static void main(String[] args) {
        SpringApplication.run(LichengApplication.class, args);
    }

}
