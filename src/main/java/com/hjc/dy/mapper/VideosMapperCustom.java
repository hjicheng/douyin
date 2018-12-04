package com.hjc.dy.mapper;

import com.hjc.dy.entity.Videos;
import com.hjc.dy.util.utils.MyMapper;
import com.hjc.dy.vo.VideosVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideosMapperCustom extends MyMapper<Videos> {

    public List<VideosVo> videoVoList(@Param("videoDesc") String desc, @Param("userId") String userId);

    /**
     * @Description: 查询关注的视频
     */
    public List<VideosVo> queryMyFollowVideos(String userId);

    /**
     * @Description: 查询点赞视频
     */
    public List<VideosVo> queryMyLikeVideos(@Param("userId") String userId);

    /**
     * @Description: 对视频喜欢的数量进行累加
     */
    public void addVideoLikeCount(String videoId);

    /**
     * @Description: 对视频喜欢的数量进行累减
     */
    public void reduceVideoLikeCount(String videoId);
}