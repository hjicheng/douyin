package com.hjc.dy.mapper;

import com.hjc.dy.entity.Comments;
import com.hjc.dy.util.utils.MyMapper;
import com.hjc.dy.vo.CommentsVo;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVo> queryComments(String videoId);
}