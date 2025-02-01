package com.example.sea.media.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.result.CommonResult;
import com.example.sea.media.entity.MusicInfo;
import com.example.sea.media.interfaces.vo.MusicInfoVO;
import com.example.sea.media.interfaces.vo.MusicInfoVO2;

import java.util.List;


public interface MusicInfoService {
    CommonResult<Integer> saveMusicInfo(MusicInfo musicInfo);

    CommonResult<Page<MusicInfoVO>> pageList(MusicInfo musicInfo, Integer pageNum, Integer pageSize);

    CommonResult<Page<MusicInfoVO2>> pageListJoinSong(MusicInfo musicInfo, Integer pageNum, Integer pageSize);

    CommonResult<Integer> deleteById(Long id);
    CommonResult<Integer> logicalDeleteById(Long id);

    CommonResult<Integer> logicalBatchDeleteByIds(List<MusicInfo> list);




}
