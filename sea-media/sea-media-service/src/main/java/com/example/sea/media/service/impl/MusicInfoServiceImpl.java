package com.example.sea.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.constants.SystemConstant;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.media.dao.MusicInfoMapper;
import com.example.sea.media.entity.MusicInfo;
import com.example.sea.media.interfaces.vo.MusicInfoVO;
import com.example.sea.media.interfaces.vo.MusicInfoVO2;
import com.example.sea.media.service.MusicInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicInfoServiceImpl implements MusicInfoService {

    @Autowired
    MusicInfoMapper musicInfoMapper;

    /**
     * 保存音乐信息
     */
    @Override
    public CommonResult<Integer> saveMusicInfo(MusicInfo musicInfo) {
        if (musicInfo.getId() == null) {
            return CommonResult.success(musicInfoMapper.insert(musicInfo));
        } else {
            return CommonResult.success(musicInfoMapper.updateById(musicInfo));
        }
    }

    /**
     * 分页查询音乐信息
     */
    @Override
    public CommonResult<Page<MusicInfoVO>> pageList(MusicInfo musicInfo, Integer pageNum, Integer pageSize) {
        Page<MusicInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MusicInfo> queryWrapper = new LambdaQueryWrapper<>();
        //如果有条件可以在这里queryWrapper中方法添加
        queryWrapper.eq(MusicInfo::getDelFlag, SystemConstant.DEL_FLAG_NO);
                    
        page = musicInfoMapper.selectPage(page, queryWrapper);

        //封装为VO
        List<MusicInfoVO> voList = page.getRecords().stream().map(record -> {
            MusicInfoVO vo = new MusicInfoVO();
            BeanUtils.copyProperties(record, vo);
            return vo;
        }).collect(Collectors.toList());

        Page<MusicInfoVO> voPage = new Page<>();
        BeanUtils.copyProperties(page, voPage);
        voPage.setRecords(voList);
        return CommonResult.success(voPage);
    }

    /**
     * 分页查询音乐信息,且关联歌手表获取歌手姓名
     */
    @Transactional
    @Override   
    public CommonResult<Page<MusicInfoVO2>> pageListJoinSong(MusicInfo musicInfo, Integer pageNum, Integer pageSize) {
        List<MusicInfoVO2> records =  musicInfoMapper.pageListJoinSong(musicInfo, (pageNum -1) * pageSize, pageSize);
        int total = musicInfoMapper.getTotal();

        //封装page
        Page<MusicInfoVO2> voPage = new Page<>();
        voPage.setRecords(records);
        voPage.setTotal(total);
        voPage.setCurrent(pageNum);
        voPage.setSize(pageSize);
        //page类里面会自动运算不需要再赋值
        // voPage.setPages(total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
        
        return CommonResult.success(voPage);
    }

    /**
     * 物理删除音乐信息
     */
    @Override
    public CommonResult<Integer> deleteById(Long id) {
        int count  = musicInfoMapper.deleteById(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("删除失败");
    }

    /**
     * 逻辑删除音乐信息
     */
    @Override
    public CommonResult<Integer> logicalDeleteById(Long id) {
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setId(id);
        musicInfo.setDelFlag(SystemConstant.DEL_FLAG_YES);
        return CommonResult.success(musicInfoMapper.updateById(musicInfo));
    }

    /**
     * 批量逻辑删除音乐信息
     */
    @Override
    public CommonResult<Integer> logicalBatchDeleteByIds(List<MusicInfo> list) {
        //验证ids空
        if (list == null || list.size() == 0) {
            return CommonResult.failed("选择行不能为空");
        }
        //将list.delFlag设置为删除状态
        list.forEach(record -> {
            record.setDelFlag(SystemConstant.DEL_FLAG_YES);
        });
        
        int count = musicInfoMapper.logicalBatchDeleteByIds(list);
        return CommonResult.success(count);
    }
      

     
    
}
