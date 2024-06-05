package cn.ujn.licheng.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页请求参数
 *
 * @author XinCheng
 * date 2024-06-04
 */

@Data
public class PageRequest implements Serializable {


    @Serial
    private static final long serialVersionUID = 3058278052333057567L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;
}
