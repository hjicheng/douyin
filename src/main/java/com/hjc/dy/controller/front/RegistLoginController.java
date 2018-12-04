package com.hjc.dy.controller.front;

import com.hjc.dy.controller.BaseController;
import com.hjc.dy.entity.Users;
import com.hjc.dy.service.front.UserService;
import com.hjc.dy.util.utils.DyJSONResult;
import com.hjc.dy.util.utils.MD5Utils;
import com.hjc.dy.vo.UsersVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.scripts.JD;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Author: hjc
 * @Date: 2018/11/24 21:58
 * @Version 1.0
 */
@RestController
@Api(value = "用户注册登录的接口", tags = {"注册和登录的controller"})
public class RegistLoginController extends BaseController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册接口")
    @PostMapping("/regist")
    public DyJSONResult regist(@RequestBody Users user) throws Exception {
        // 1.判断用户名和密码不为空
        if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getUsername())) {
            return DyJSONResult.errorMsg("用户名和密码为空");
        }
        // 2.判断用户名和密码是否存在
        boolean usernameIsExist = userService.checkUsername(user.getUsername());
        // 3.保存用户注册信息
        if (!usernameIsExist) {
            user.setNickname(user.getUsername());
            user.setFansCounts(0);
            user.setFollowCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFaceImage("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userService.saveUser(user);
        } else {
            return DyJSONResult.errorMsg("用户已存在，请登录");
        }
        user.setPassword("");
        String uniqneToken = UUID.randomUUID().toString();
        redisOperator.set(USER_REDIS_SESSION + ":" + user.getId(), uniqneToken, 1000 * 30 * 60);

        UsersVo usersVo = setUserRedisSessionToken(user);
        return DyJSONResult.ok(usersVo);
    }

    public UsersVo setUserRedisSessionToken(Users user) {
        String uniqueToken = UUID.randomUUID().toString();
        redisOperator.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000 * 30 * 60);
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(user, usersVo);
        usersVo.setUserToken(uniqueToken);
        return usersVo;
    }

    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @PostMapping("/login")
    public DyJSONResult login(@RequestBody Users user) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();

        if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getUsername())) {
            return DyJSONResult.errorMsg("用户名和密码为空");
        }
        // 判断用户是否存在
        Users userResult = userService.login(username, MD5Utils.getMD5Str(password));
        if (userResult != null) {
            user.setPassword("");
            String uniqneToken = UUID.randomUUID().toString();
            redisOperator.set(USER_REDIS_SESSION + ":" + user.getId(), uniqneToken, 1000 * 30 * 60);
            UsersVo usersVo = setUserRedisSessionToken(userResult);
            return DyJSONResult.ok(usersVo);
        } else {
            return DyJSONResult.errorMsg("用户名或密码不正确，请重试......");
        }
    }

    @ApiOperation(value = "用户注销", notes = "用户注销接口")
    @PostMapping("/logout")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    public DyJSONResult login(String userId) throws Exception {
        redisOperator.del(USER_REDIS_SESSION + ":" + userId);
        return DyJSONResult.ok("注销成功");
    }
}
