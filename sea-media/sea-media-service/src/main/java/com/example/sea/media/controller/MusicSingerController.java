package com.example.sea.media.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.media.entity.MusicSinger;
import com.example.sea.media.interfaces.vo.MusicSingerVO;
import com.example.sea.media.service.MusicSingerService;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/media/singer")
@Tag(name = "歌手管理", description = "歌手信息相关操作接口")
@RequiredArgsConstructor
public class MusicSingerController {

    private final MusicSingerService musicSingerService;

    /**
     * 获取所有歌手信息
     * @return
     */
    @GetMapping("/getList")
    @Operation(summary = "获取歌手列表", description = "根据条件查询歌手信息列表")
    public CommonResult<List<MusicSingerVO>> getList(MusicSinger musicSinger) {
        return musicSingerService.getList(musicSinger);
    }

    @PostMapping("/saveMusicSinger")
    @Operation(summary = "保存歌手信息", description = "创建新的歌手信息记录")
    public CommonResult<Integer> saveMusicSinger(@RequestBody MusicSinger musicSinger){
        return musicSingerService.saveMusicSinger(musicSinger);
    }

    @GetMapping("/pageList")
    @Operation(summary = "分页查询歌手", description = "根据条件分页查询歌手信息")
    public CommonResult<Page<MusicSingerVO>> pageList(MusicSinger musicSinger, Integer pageNum, Integer pageSize){
        return musicSingerService.pageList(musicSinger, pageNum == null? 1 :pageNum, pageSize == null? 10 : pageSize);
    }

    @PostMapping("/logicalDeleteById")
    @Operation(summary = "逻辑删除歌手", description = "根据ID逻辑删除歌手信息")
    public CommonResult<Integer> logicalDeleteById(@RequestBody Long id){
        return musicSingerService.logicalDeleteById(id);
    }

    @PostMapping("/logicalBatchDeleteByIds")
    @Operation(summary = "批量逻辑删除歌手", description = "批量逻辑删除歌手信息")
    public CommonResult<Integer> logicalBatchDeleteByIds(@RequestBody List<MusicSinger> list){
        return musicSingerService.logicalBatchDeleteByIds(list);
     }
    
}
