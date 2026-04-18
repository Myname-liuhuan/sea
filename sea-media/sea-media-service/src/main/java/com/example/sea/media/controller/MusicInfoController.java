package com.example.sea.media.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.media.entity.MusicInfoPO;
import com.example.sea.media.api.vo.MusicInfoVO;
import com.example.sea.media.api.vo.MusicInfoVO2;
import com.example.sea.media.service.MusicInfoService;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/media/music")
@Tag(name = "音乐管理", description = "音乐信息相关操作接口")
@RequiredArgsConstructor
public class MusicInfoController {

    private final MusicInfoService musicInfoService;

    /**
     * 保存音乐信息
     * post请求一定记得要设置 @RequestBody
     * @param musicInfo
     * @return
     */
    @PostMapping("/saveMusicInfo")
    @Operation(summary = "保存音乐信息", description = "创建新的音乐信息记录")
    public CommonResult<Integer> saveMusicInfo(@RequestBody MusicInfoPO musicInfo){
        return musicInfoService.saveMusicInfo(musicInfo);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/pageList")
    @Operation(summary = "分页查询音乐", description = "根据条件分页查询音乐信息")
    public CommonResult<Page<MusicInfoVO>> pageList(MusicInfoPO musicInfo, Integer pageNum, Integer pageSize){
        return musicInfoService.pageList(musicInfo,pageNum == null? 1 :pageNum, pageSize == null? 10 : pageSize);
    }

    /**
     * 分页查询且关联歌手表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/pageListJoinSong")
    @Operation(summary = "分页查询音乐(含歌手)", description = "分页查询音乐信息并关联歌手表")
    public CommonResult<Page<MusicInfoVO2>> pageListJoinSong(MusicInfoPO musicInfo, Integer pageNum, Integer pageSize){
        return musicInfoService.pageListJoinSong(musicInfo, pageNum == null || pageNum <= 0? 1 :pageNum, pageSize == null? 10 : pageSize);
    }

    /**
     * 通过id逻辑删除数据
     * @param id
     * @return
     */
    @PostMapping("/logicalDeleteById")
    @Operation(summary = "逻辑删除音乐", description = "根据ID逻辑删除音乐信息")
    public CommonResult<Integer> logicalDeleteById(@RequestBody Long id){
        return musicInfoService.logicalDeleteById(id);
    }

    /**
     * 通过id逻辑删除数据
     * @param id
     * @return
     */
    @PostMapping("/logicalBatchDeleteByIds")
    @Operation(summary = "批量逻辑删除音乐", description = "批量逻辑删除音乐信息")
    public CommonResult<Integer> logicalBatchDeleteByIds(@RequestBody List<MusicInfoPO> list){
        return musicInfoService.logicalBatchDeleteByIds(list);
    }
}
