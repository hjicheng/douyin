package com.hjc.dy.mapper;

import com.hjc.dy.entity.SearchRecords;
import com.hjc.dy.util.utils.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
    List<String> selectHots();
}