package cn.ujn.licheng.service;

import cn.ujn.licheng.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author XinCheng
 * date 2024-05-21
 */
@SpringBootTest
public class InsertUsersTest {
    //    @Autowired
//    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    // CPU 密集型： 分配的核心线程数 = CPU - 1
    // IO 密集型： 分配的核心线程数可以大于 CPU 核心数
    private ExecutorService executorService =
            new ThreadPoolExecutor(60, 1000, 10000, TimeUnit.MINUTES
                    , new ArrayBlockingQueue<>(10000));

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 100000;

        List<User> userList = new ArrayList<>();
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
            userList.add(user);
        }
        //16 秒 10万条数据
        userService.saveBatch(userList, 50000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 100000;
        //分成10组，每组1万条

        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<User> userList = Collections.synchronizedList(new ArrayList<>());
            while (true) {
                j++;
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
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            //异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);//这里使用自己创建的线程池，非自带的
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        //16 秒 10万条数据

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
