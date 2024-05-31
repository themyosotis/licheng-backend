package cn.ujn.licheng.once;

import cn.ujn.licheng.mapper.UserMapper;
import cn.ujn.licheng.model.domain.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @author XinCheng
 * date 2024-05-21
 */
@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)//延迟5秒执行，间隔为Long的最大值，相当于一定时间内只执行一次
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 10000000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假心酲");
            user.setUserAccount("fakeXIncheng");
            user.setAvatarUrl("https://himg.bdimg.com/sys/portraitn/item/public.1.e137c1ac.yS1WqOXfSWEasOYJ2-0pvQ");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111111");

            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


}
