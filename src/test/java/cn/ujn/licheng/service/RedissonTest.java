package cn.ujn.licheng.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author XinCheng
 * date 2024-06-01
 */
@SpringBootTest
public class RedissonTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void test() {
        // list 数据存在本地 JVM 中
        List<String> list = new ArrayList<>();
        list.add("xincheng");
        System.out.println("list:" + list.get(0));
        list.remove(0);

        // 数据存在 redis 的内存中
        RList<String> rList = redissonClient.getList("test-list");
        rList.add("xincheng");
        System.out.println("rList:" + rList.get(0));
        rList.remove(0);

        // map
        HashMap<String, Integer> map = new HashMap<>();
        map.put("xincheng",10);
        System.out.println(map.get("xincheng"));

        RMap<Object, Integer> map1 = redissonClient.getMap("test-map");
        map1.put("xincheng",20);
        System.out.println(map1.get("xincheng"));
        map1.remove("xincheng");
        // set

        // stack

    }

    @Test
    void testWatchDog(){

        RLock lock = redissonClient.getLock("licheng:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            // lock.tryLock(等待时间,多长时间释放锁,TimeUnit.MILLISECONDS)
            // 等待时间为0 即没获取到锁直接不执行任务，返回false
            // 等待时间非0，即未获取到锁等待这些时间，若到时间仍未获取到锁，不执行，返回false
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Thread.sleep(300000);
                System.out.println("getLock: " + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己的锁, 不要放到try里，如果报错了不会释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("unLock: " + Thread.currentThread().getId());
            }
        }
    }
}
