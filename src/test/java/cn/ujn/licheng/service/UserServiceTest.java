package cn.ujn.licheng.service;

import java.util.Arrays;
import java.util.List;

import cn.ujn.licheng.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务测试
 *
 * @author XinCheng
 * date 2024-04-06
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("dogXincheng");
        user.setUserAccount("123");
        user.setAvatarUrl("https://cdn.nlark.com/yuque/0/2024/png/42478583/1705908094163-avatar/878211d7-70fe-4494-b1d7-75a816b94145.png?x-oss-process=image%2Fresize%2Cm_fill%2Cw_88%2Ch_88%2Fformat%2Cpng");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");


        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }


    @Test
    void userRegister() {
        String userAccount = "xincheng";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "xin";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "xincheng";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "xin cheng";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "dogXincheng";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
        userAccount = "xincheng";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);
    }

    @Test
    public void testSearchUsersByTags(){
        List<String> tagNameList= Arrays.asList("java","python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assertions.assertNotNull(userList);
    }

}