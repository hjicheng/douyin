package com.hjc.dy.controller.front;

import com.hjc.dy.controller.BaseController;
import com.hjc.dy.entity.Bgm;
import com.hjc.dy.entity.Comments;
import com.hjc.dy.entity.Users;
import com.hjc.dy.entity.Videos;
import com.hjc.dy.mapper.SearchRecordsMapper;
import com.hjc.dy.service.front.BgmService;
import com.hjc.dy.service.front.VideoService;
import com.hjc.dy.util.enums.VideoStatusEnum;
import com.hjc.dy.util.utils.DyJSONResult;
import com.hjc.dy.util.utils.FetchVideoCover;
import com.hjc.dy.util.utils.MergeVideoMp3;
import com.hjc.dy.util.utils.PagedResult;
import io.swagger.annotations.*;
import io.swagger.models.auth.In;
import javafx.scene.chart.ValueAxis;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: hjc
 * @Date: 2018/11/24 21:58
 * @Version 1.0
 */
@RestController
@Api(value = "视频业务的接口", tags = {"视频业务的controller"})
@RequestMapping("/video")
public class VideoController extends BaseController {

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "上传视频", notes = "上传视频接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "视频播放事件", required = true, dataType = "double", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频长度", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "int", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "背景音乐描述", required = false, dataType = "String", paramType = "form")
    })
    public DyJSONResult uploadImage(String userId, @ApiParam(value = "短视频", required = true) MultipartFile file,
                                    String bgmId, double videoSeconds, int videoHeight, int videoWidth, String desc) throws Exception {
        if (StringUtils.isBlank(userId)) {
            return DyJSONResult.errorMsg("用户不存在...");
        }
        // 文件保存的空间
        String fileSpace = "D:/dy_image";
        // 文件保存的命名空间
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalVideoPath = "";
        try {
            if (file != null) {

                String fileName = file.getOriginalFilename();
                // abc.mp4
                String arrayFilenameItem[] = fileName.split("\\.");
                String fileNamePrefix = "";
                for (int i = 0; i < arrayFilenameItem.length - 1; i++) {
                    fileNamePrefix += arrayFilenameItem[i];
                }
                // fix bug: 解决小程序端OK，PC端不OK的bug，原因：PC端和小程序端对临时视频的命名不同

                if (StringUtils.isNotBlank(fileName)) {

                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return DyJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DyJSONResult.errorMsg("上传出错...");
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception r) {
                r.printStackTrace();
            }
        }

        // 判断bgmId是否为空，如果不为空，
        // 那就查询bgm的信息，并且合并视频，生产新的视频
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.getBgmById(bgmId);
            String mp3InputPath = FILE_SPACE + bgm.getPath();

            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalVideoPath = FILE_SPACE + uploadPathDB;
            tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
        }
        System.out.println("uploadPathDB=" + uploadPathDB);
        System.out.println("finalVideoPath=" + finalVideoPath);

        // 对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
        videoInfo.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

        // 保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());

        videoService.save(video);
        return DyJSONResult.ok();
    }


    /**
     * 分页查询视频列表
     * @param videos
     * @param isSaveRecord 1 需要保存 0 不需要保存
     * @param page
     * @return
     */
    @PostMapping("/showAll")
    public DyJSONResult videosList(@RequestBody Videos videos,Integer isSaveRecord,Integer page){

        if (page == null){
            page = 1;
        }
        PagedResult result = videoService.getAllVideos(videos,isSaveRecord,page,PAGE_SIZE);
        return DyJSONResult.ok(result);

    }

    @PostMapping("/hot")
    public DyJSONResult hot(){
        return DyJSONResult.ok(videoService.selectHots());

    }
    /**
     * @Description: 我关注的人发的视频
     */
    @PostMapping("/showMyFollow")
    public DyJSONResult showMyFollow(String userId, Integer page) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return DyJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        int pageSize = 6;

        PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);

        return DyJSONResult.ok(videosList);
    }

    /**
     * @Description: 我收藏(点赞)过的视频列表
     */
    @PostMapping("/showMyLike")
    public DyJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return DyJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 6;
        }

        PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);

        return DyJSONResult.ok(videosList);
    }

    @PostMapping(value="/userLike")
    public DyJSONResult userLike(String userId, String videoId, String videoCreaterId)
            throws Exception {
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return DyJSONResult.ok();
    }

    @PostMapping(value="/userUnLike")
    public DyJSONResult userUnLike(String userId, String videoId, String videoCreaterId) throws Exception {
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return DyJSONResult.ok();
    }

    @PostMapping("/saveComment")
    public DyJSONResult saveComment(@RequestBody Comments comment,
                                       String fatherCommentId, String toUserId) throws Exception {

        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);

        videoService.saveComment(comment);
        return DyJSONResult.ok();
    }

    @PostMapping("/getVideoComments")
    public DyJSONResult getVideoComments(String videoId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(videoId)) {
            return DyJSONResult.ok();
        }

        // 分页查询视频列表，时间顺序倒序排序
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedResult list = videoService.getAllComments(videoId, page, pageSize);

        return DyJSONResult.ok(list);
    }

}
