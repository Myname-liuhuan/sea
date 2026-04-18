package com.example.sea.media.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sea.media.entity.MusicSingerPO;
import com.example.sea.media.api.vo.MusicSingerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicSingerMapper extends BaseMapper<MusicSingerPO> {

    List<MusicSingerVO> getList(MusicSingerPO musicSinger);

    List<MusicSingerVO> pageList(MusicSingerPO musicSinger, Integer offset, Integer pageSize);

    @Select("SELECT FOUND_ROWS()")
    Integer getTotal();

    Integer logicalBatchDeleteByIds(List<MusicSingerPO> list);

}
