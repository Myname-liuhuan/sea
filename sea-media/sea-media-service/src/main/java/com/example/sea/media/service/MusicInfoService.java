package com.example.sea.media.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.media.entity.MusicInfoPO;
import com.example.sea.media.api.vo.MusicInfoVO;
import com.example.sea.media.api.vo.MusicInfoVO2;

import java.util.List;


public interface MusicInfoService {
    CommonResult<Integer> saveMusicInfo(MusicInfoPO musicInfo);

    CommonResult<Page<MusicInfoVO>> pageList(MusicInfoPO musicInfo, Integer pageNum, Integer pageSize);

    CommonResult<Page<MusicInfoVO2>> pageListJoinSong(MusicInfoPO musicInfo, Integer pageNum, Integer pageSize);

    CommonResult<Integer> deleteById(Long id);
    CommonResult<Integer> logicalDeleteById(Long id);

    CommonResult<Integer> logicalBatchDeleteByIds(List<MusicInfoPO> list);




}
