package com.example.sea.media.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.constants.SystemConstant;
import com.example.sea.common.result.CommonResult;
import com.example.sea.media.dao.MusicSingerMapper;
import com.example.sea.media.entity.MusicSinger;
import com.example.sea.media.interfaces.vo.MusicSingerVO;
import com.example.sea.media.service.MusicSingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicSingerServiceImpl implements MusicSingerService {

    @Autowired
    MusicSingerMapper musicSingerMapper;

    @Override
    public CommonResult<List<MusicSingerVO>> getList(MusicSinger musicSinger) {
        return CommonResult.success(musicSingerMapper.getList(musicSinger));
    }

    @Override
    public CommonResult<Integer> saveMusicSinger(MusicSinger musicSinger) {
        if (musicSinger.getId() == null) {
            return CommonResult.success(musicSingerMapper.insert(musicSinger));
        }
        return CommonResult.success(musicSingerMapper.updateById(musicSinger));
    }

    /**
     * 分页查询
     */
    @Override
    public CommonResult<Page<MusicSingerVO>> pageList(MusicSinger musicSinger, Integer pageNum, Integer pageSize) {
        List<MusicSingerVO> records =  musicSingerMapper.pageList(musicSinger, (pageNum - 1) * pageSize, pageSize);
        int total = musicSingerMapper.getTotal();

        Page<MusicSingerVO> voPage = new Page<>();
        voPage.setRecords(records);
        voPage.setTotal(total);
        voPage.setCurrent(pageNum);
        voPage.setSize(pageSize);

        return CommonResult.success(voPage);
    }

    /**
     * 通过id逻辑删除
     */
    @Override
    public CommonResult<Integer> logicalDeleteById(Long id) {
        MusicSinger musicSinger = new MusicSinger();
        musicSinger.setId(id);
        musicSinger.setDelFlag(1);
        return CommonResult.success(musicSingerMapper.updateById(musicSinger));
    }

    @Override
    public CommonResult<Integer> logicalBatchDeleteByIds(List<MusicSinger> list) {
        //验证ids空
        if (list == null || list.size() == 0) {
            return CommonResult.failed("选择行不能为空");
        }
        //将list.delFlag设置为删除状态
        list.forEach(record -> {
            record.setDelFlag(SystemConstant.DEL_FLAG_YES);
        });
        
        int count = musicSingerMapper.logicalBatchDeleteByIds(list);
        return CommonResult.success(count);
    }
    
}
