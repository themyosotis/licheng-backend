package cn.ujn.licheng.job;

import cn.ujn.licheng.model.domain.User;
import cn.ujn.licheng.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热
 *
 * @author XinCheng
 * date 2024-05-31
 */
@Component
@Slf4j
public class PreCacheJob {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);


    //每天执行，预热推荐用户
    @Scheduled(cron = "0 13 14 * * ? ")
    public void doCacheRecommendUser() {

        RLock lock = redissonClient.getLock("licheng:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            // lock.tryLock(等待时间,多长时间释放锁,TimeUnit.MILLISECONDS)
            // 等待时间为0 即没获取到锁直接不执行任务，返回false
            // 等待时间非0，即未获取到锁等待这些时间，若到时间仍未获取到锁，不执行，返回false
            if (lock.tryLock(0, 10000L, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService
                            .page(new Page<>(1, 20), queryWrapper);

                    String redisKey = String.format("licheng:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写缓存
                    try {
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }

            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 只能释放自己的锁, 不要放到try里，如果报错了不会释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("unLock: " + Thread.currentThread().getId());
            }
        }


    }
}
