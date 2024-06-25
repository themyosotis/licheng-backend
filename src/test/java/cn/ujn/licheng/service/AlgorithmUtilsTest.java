package cn.ujn.licheng.service;

import cn.ujn.licheng.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 算法工具类测试
 *
 * @author XinCheng
 * date 2024-06-21
 */
@SpringBootTest
public class AlgorithmUtilsTest {

    @Test
    void testCompareStrings() {
        String str1 = "心酲是狗";
        String str2 = "心酲不是狗";
        String str3 = "心酲是猫不是狗";
        String str4 = "心酲是猫";

        // 1
        int score1 = AlgorithmUtils.minDistance(str1, str2);
        // 3
        int score2 = AlgorithmUtils.minDistance(str1, str3);
        // 1
        int score3 = AlgorithmUtils.minDistance(str1, str4);
        System.out.println(score1);
        System.out.println(score2);
        System.out.println(score3);
    }

    @Test
    void testCompareTags() {

        List<String> tagList1 = Arrays.asList("Java", "大一", "男");
        List<String> tagList2 = Arrays.asList("Java", "大一", "女");
        List<String> tagList3 = Arrays.asList("Python", "大二", "女");

        // 1
        int score1 = AlgorithmUtils.minDistance(tagList1, tagList2);
        // 3
        int score2 = AlgorithmUtils.minDistance(tagList1, tagList3);

        System.out.println(score1);
        System.out.println(score2);
    }
}
