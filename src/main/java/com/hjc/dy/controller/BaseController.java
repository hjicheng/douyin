package com.hjc.dy.controller;

import com.hjc.dy.util.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: hjc
 * @Date: 2018/11/25 13:19
 * @Version 1.0
 */
@RestController
public class BaseController {

    @Autowired
    public
    RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user-redis-success";

    // 文件保存的命名空间
    public static final String FILE_SPACE = "D:\\dy_image";

    // ffmpeg所在目录
    public static final String FFMPEG_EXE = "D:\\sofo\\ffmpeg\\bin\\ffmpeg.exe";

    // 每页分页的记录数
    public static final Integer PAGE_SIZE = 5;
}
