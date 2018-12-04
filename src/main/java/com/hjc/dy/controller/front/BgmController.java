package com.hjc.dy.controller.front;

import com.hjc.dy.controller.BaseController;
import com.hjc.dy.entity.Users;
import com.hjc.dy.service.front.BgmService;
import com.hjc.dy.service.front.UserService;
import com.hjc.dy.util.utils.DyJSONResult;
import com.hjc.dy.vo.UsersVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: hjc
 * @Date: 2018/11/24 21:58
 * @Version 1.0
 */
@RestController
@Api(value = "背景音乐的接口", tags = {"背景音乐的controller"})
@RequestMapping("/bgm")
public class BgmController extends BaseController {

    @Autowired
    private BgmService bgmService;

    @PostMapping("/list")
    @ApiOperation(value = "获取背景音乐", notes = "获取背景音乐接口")
    public DyJSONResult list(){
        return DyJSONResult.ok(bgmService.getBgmList());
    }

}
