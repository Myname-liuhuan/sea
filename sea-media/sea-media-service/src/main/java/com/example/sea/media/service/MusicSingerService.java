package com.example.sea.media.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.media.entity.MusicSingerPO;
import com.example.sea.media.api.vo.MusicSingerVO;

import java.util.List;

public interface MusicSingerService {

    CommonResult<List<MusicSingerVO>> getList(MusicSingerPO musicSinger);

    CommonResult<Integer> saveMusicSinger(MusicSingerPO musicSinger);

    CommonResult<Page<MusicSingerVO>> pageList(MusicSingerPO musicSinger, Integer pageNum, Integer pageSize);

    CommonResult<Integer> logicalDeleteById(Long id);

    CommonResult<Integer> logicalBatchDeleteByIds(List<MusicSingerPO> list);

}
