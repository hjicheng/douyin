package com.hjc.dy.controller.front;

import com.hjc.dy.controller.BaseController;
import com.hjc.dy.entity.Users;
import com.hjc.dy.entity.UsersReport;
import com.hjc.dy.service.front.UserService;
import com.hjc.dy.util.utils.DyJSONResult;
import com.hjc.dy.util.utils.MD5Utils;
import com.hjc.dy.vo.PublisherVideo;
import com.hjc.dy.vo.UsersVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @Author: hjc
 * @Date: 2018/11/24 21:58
 * @Version 1.0
 */
@RestController
@Api(value = "用户个人页面的接口", tags = {"用户个人页面的controller"})
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像接口")
    @PostMapping("/uploadImage")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    public DyJSONResult uploadImage(String userId, @RequestParam("file") MultipartFile[] files) {

        if (StringUtils.isBlank(userId)) {
            return DyJSONResult.errorMsg("用户不存在...");
        }
        // 文件保存的空间
        String fileSpace = "D:/dy_image";
        // 保存数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {
                String fileName = files[0].getOriginalFilename();
                if (!StringUtils.isBlank(fileName)) {
                    // 文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return DyJSONResult.errorMsg("上传失败.....");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException i){
                    i.printStackTrace();
                }
            }
        }
        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(uploadPathDB);
        if (userService.uploadUser(users) > 0){
            return DyJSONResult.ok(users);
        } else {
            return DyJSONResult.errorMsg("上传失败");
        }

    }


    @ApiOperation(value = "用户信息查询", notes = "用户信息查询接口")
    @PostMapping("/query")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    public DyJSONResult login(String userId){
        if (StringUtils.isBlank(userId)) {
            return DyJSONResult.errorMsg("用户不存在...");
        }
        Users user = userService.getUserById(userId);
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(user, usersVo);
        return DyJSONResult.ok(usersVo);
    }

    @PostMapping("/queryPublisher")
    public DyJSONResult queryPublisher(String loginUserId, String videoId,
                                          String publishUserId) throws Exception {

        if (StringUtils.isBlank(publishUserId)) {
            return DyJSONResult.errorMsg("");
        }

        // 1. 查询视频发布者的信息
        Users userInfo = userService.getUserById(publishUserId);
        UsersVo publisher = new UsersVo();
        BeanUtils.copyProperties(userInfo, publisher);

        // 2. 查询当前登录者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return DyJSONResult.ok(bean);
    }
    @PostMapping("/beyourfans")
    public DyJSONResult beyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return DyJSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId, fanId);

        return DyJSONResult.ok("关注成功...");
    }

    @PostMapping("/dontbeyourfans")
    public DyJSONResult dontbeyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return DyJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId, fanId);

        return DyJSONResult.ok("取消关注成功...");
    }

    @PostMapping("/reportUser")
    public DyJSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception {

        // 保存举报信息
        userService.reportUser(usersReport);

        return DyJSONResult.errorMsg("举报成功...有你平台变得更美好...");
    }
}
