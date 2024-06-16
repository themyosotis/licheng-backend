package cn.ujn.licheng.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录请求体
 *
 * @author XinCheng
 * date 2024-04-06
 */
@Data
public class TeamJoinRequest implements Serializable {


    @Serial
    private static final long serialVersionUID = -7458107521115968303L;

    /**
     * id
     */
    private Long teamId;


    /**
     * 密码
     */
    private String password;


}
