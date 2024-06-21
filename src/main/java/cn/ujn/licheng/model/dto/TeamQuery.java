package cn.ujn.licheng.model.dto;

import cn.ujn.licheng.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * 队伍查询封装类
 *
 * @author XinCheng
 * date 2024-06-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    @Serial
    private static final long serialVersionUID = -1239023906109559505L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * id 列表
     */
    private List<Long> idList;


    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;


}
