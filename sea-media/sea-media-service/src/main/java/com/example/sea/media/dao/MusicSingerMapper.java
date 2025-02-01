package com.example.sea.media.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.media.entity.MusicSinger;
import com.example.sea.media.interfaces.vo.MusicSingerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicSingerMapper extends BaseMapper<MusicSinger> {

    List<MusicSingerVO> getList(MusicSinger musicSinger);

    List<MusicSingerVO> pageList(MusicSinger musicSinger, Integer offset, Integer pageSize);

    @Select("SELECT FOUND_ROWS()")
    Integer getTotal();

    Integer logicalBatchDeleteByIds(List<MusicSinger> list);
    
}
