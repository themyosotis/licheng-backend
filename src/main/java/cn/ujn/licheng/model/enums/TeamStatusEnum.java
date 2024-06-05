package cn.ujn.licheng.model.enums;

import cn.ujn.licheng.service.TeamService;

/**
 * 队伍状态枚举
 *
 * @author XinCheng
 * date 2024-06-04
 */

public enum TeamStatusEnum {
    /**
     * 公开、私有、加密状态
     */
    PUBLIC(0, "公开"),
    PRIVATE(1, "私有"),
    SECRET(2, "加密");


    /**
     * 状态值
     */
    private int value;

    /**
     * 状态描述
     */
    private String text;

    public static TeamStatusEnum getEnumByValue(Integer value){
        if(value==null){
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if(teamStatusEnum.getValue()==value){
                return teamStatusEnum;
            }
        }
        return null;
    }

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
