package com.hjc.dy.service.front;

import com.hjc.dy.entity.Users;
import com.hjc.dy.entity.UsersFans;
import com.hjc.dy.entity.UsersLikeVideos;
import com.hjc.dy.entity.UsersReport;
import com.hjc.dy.idworker.Sid;
import com.hjc.dy.mapper.UsersFansMapper;
import com.hjc.dy.mapper.UsersLikeVideosMapper;
import com.hjc.dy.mapper.UsersMapper;
import com.hjc.dy.mapper.UsersReportMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @Author: hjc
 * @Date: 2018/11/24 22:04
 * @Version 1.0
 */
@Service
public class UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper userMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    /*
     * 判断用户名是否存在
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean checkUsername(String username) {
        Users users = new Users();
        users.setUsername(username);
        Users result = usersMapper.selectOne(users);
        return result == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(Users user) {
        String id = sid.nextShort();
        user.setId(id);
        usersMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users login(String username, String password) {

        Users users = new Users();
        users.setPassword(password);
        users.setUsername(username);
        Users result = usersMapper.selectOne(users);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public int uploadUser(Users user) {
        return usersMapper.updateByPrimaryKeySelective(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users getUserById(String userId) {
        Users users = new Users();
        users.setId(userId);
        Users result = usersMapper.selectOne(users);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isUserLikeVideo(String userId, String videoId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();

        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);

        if (list != null && list.size() >0) {
            return true;
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUserFanRelation(String userId, String fanId) {

        String relId = sid.nextShort();

        UsersFans userFan = new UsersFans();
        userFan.setId(relId);
        userFan.setUserId(userId);
        userFan.setFanId(fanId);

        usersFansMapper.insert(userFan);

        userMapper.addFansCount(userId);
        userMapper.addFollersCount(fanId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserFanRelation(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        usersFansMapper.deleteByExample(example);

        userMapper.reduceFansCount(userId);
        userMapper.reduceFollersCount(fanId);

    }

    public boolean queryIfFollow(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);

        if (list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void reportUser(UsersReport userReport) {

        String urId = sid.nextShort();
        userReport.setId(urId);
        userReport.setCreateDate(new Date());

        usersReportMapper.insert(userReport);
    }

}
