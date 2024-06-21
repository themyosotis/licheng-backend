package cn.ujn.licheng.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用删除请求参数
 *
 * @author XinCheng
 * date 2024-06-04
 */

@Data
public class DeleteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2079567630595703788L;

    private long id;
}
