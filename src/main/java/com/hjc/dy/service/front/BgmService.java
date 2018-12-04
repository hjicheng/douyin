package com.hjc.dy.service.front;

import com.hjc.dy.entity.Bgm;
import com.hjc.dy.entity.Users;
import com.hjc.dy.idworker.Sid;
import com.hjc.dy.mapper.BgmMapper;
import com.hjc.dy.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: hjc
 * @Date: 2018/11/24 22:04
 * @Version 1.0
 */
@Service
public class BgmService {

    @Autowired
    private BgmMapper bgmMapper;
    /**
     * 查询背景音乐类表
     * @return
     */
    public List<Bgm> getBgmList(){
        return bgmMapper.selectAll();
    }

    public Bgm getBgmById(String bgmId){
        Bgm bgm = new Bgm();
        bgm.setId(bgmId);
        return bgmMapper.selectByPrimaryKey(bgm);
    }

}
