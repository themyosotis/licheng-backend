package cn.ujn.licheng.model.domain.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author XinCheng
 * date 2024-04-06
 */
@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8341989325582366257L;
    public String userAccount;
    public String userPassword;

}
