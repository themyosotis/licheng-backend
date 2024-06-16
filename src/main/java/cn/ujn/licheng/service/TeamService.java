package cn.ujn.licheng.service;

import cn.ujn.licheng.model.domain.Team;
import cn.ujn.licheng.model.domain.User;
import cn.ujn.licheng.model.dto.TeamQuery;
import cn.ujn.licheng.model.request.TeamJoinRequest;
import cn.ujn.licheng.model.request.TeamQuitRequest;
import cn.ujn.licheng.model.request.TeamUpdateRequest;
import cn.ujn.licheng.model.vo.TeamUserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 搜索队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（解散）队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id,User loginUser);
}
