package com.example.sea.media.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.media.entity.MusicSinger;
import com.example.sea.media.interfaces.vo.MusicSingerVO;

import java.util.List;

public interface MusicSingerService {
    
    CommonResult<List<MusicSingerVO>> getList(MusicSinger musicSinger);

    CommonResult<Integer> saveMusicSinger(MusicSinger musicSinger);

    CommonResult<Page<MusicSingerVO>> pageList(MusicSinger musicSinger, Integer pageNum, Integer pageSize);

    CommonResult<Integer> logicalDeleteById(Long id);

    CommonResult<Integer> logicalBatchDeleteByIds(List<MusicSinger> list);

}
