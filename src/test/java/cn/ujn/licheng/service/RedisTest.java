package cn.ujn.licheng.service;

import cn.ujn.licheng.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author XinCheng
 * date 2024-05-31
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 增
        valueOperations.set("xinchengString", "dog");
        valueOperations.set("xinchengInt", 1);
        valueOperations.set("xinchengDouble", 1.5);
        User user = new User();
        user.setId(1L);
        user.setUsername("xincheng");
        valueOperations.set("xinchengUser", user);
        // 查
        Object xincheng = valueOperations.get("xinchengString");
        Assertions.assertTrue("dog".equals((String) xincheng));
        xincheng = valueOperations.get("xinchengInt");
        Assertions.assertTrue(1 == (Integer) xincheng);
        xincheng = valueOperations.get("xinchengDouble");
        Assertions.assertTrue(1.5 == (Double) xincheng);
        System.out.println(valueOperations.get("xinchengUser"));

        valueOperations.set("xinchengString", "dog");
        redisTemplate.delete("xinchengString");



    }
}
