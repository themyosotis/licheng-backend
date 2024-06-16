package cn.ujn.licheng.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户退出队伍
 *
 * @author XinCheng
 * date 2024-04-06
 */
@Data
public class TeamQuitRequest implements Serializable {


    @Serial
    private static final long serialVersionUID = 7140083604890490914L;

    /**
     * id
     */
    private Long teamId;


}
