package com.example.sea.media.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.media.entity.MusicInfo;
import com.example.sea.media.interfaces.vo.MusicInfoVO2;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface MusicInfoMapper extends BaseMapper<MusicInfo> {
    List<MusicInfoVO2> pageListJoinSong(@Param("musicInfo") MusicInfo musicInfo, Integer offset, Integer pageSize);

    @Select("SELECT FOUND_ROWS()")
    Integer getTotal();

    Integer logicalBatchDeleteByIds(List<MusicInfo> list);
    
}
