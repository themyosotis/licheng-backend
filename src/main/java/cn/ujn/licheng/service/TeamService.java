package cn.ujn.licheng.service;

import cn.ujn.licheng.model.domain.Team;
import cn.ujn.licheng.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 26532
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-06-04 17:04:18
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);
}
