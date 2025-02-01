package com.example.sea.media.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.result.CommonResult;
import com.example.sea.media.entity.MusicSinger;
import com.example.sea.media.interfaces.vo.MusicSingerVO;
import com.example.sea.media.service.MusicSingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/media/singer")
public class MusicSingerController {
    
    @Autowired
    MusicSingerService musicSingerService;
   
    /**
     * 获取所有歌手信息
     * @return
     */
    @GetMapping("/getList")
    public CommonResult<List<MusicSingerVO>> getList(MusicSinger musicSinger) {
        return musicSingerService.getList(musicSinger);
    }

    @PostMapping("/saveMusicSinger")
    public CommonResult<Integer> saveMusicSinger(@RequestBody MusicSinger musicSinger){
        return musicSingerService.saveMusicSinger(musicSinger);
    }

    @GetMapping("/pageList")
    public CommonResult<Page<MusicSingerVO>> pageList(MusicSinger musicSinger, Integer pageNum, Integer pageSize){
        return musicSingerService.pageList(musicSinger, pageNum == null? 1 :pageNum, pageSize == null? 10 : pageSize);
    }

    @PostMapping("/logicalDeleteById")
    public CommonResult<Integer> logicalDeleteById(@RequestBody Long id){
        return musicSingerService.logicalDeleteById(id);
    }

     public CommonResult<Integer> logicalBatchDeleteByIds(@RequestBody List<MusicSinger> list){
        return musicSingerService.logicalBatchDeleteByIds(list);
     }
    
}
