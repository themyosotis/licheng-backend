package cn.ujn.licheng.model.domain.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * 用户注册请求体
 *
 * @author XinCheng
 * date 2024-04-06
 */
@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8341989325582366257L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
