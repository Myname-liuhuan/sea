package com.example.sea.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.sea.common.core.result.CommonResult;
import com.example.sea.workflow.api.vo.WorkflowDefinitionVO;
import com.example.sea.workflow.converter.WorkflowDefinitionConverter;
import com.example.sea.workflow.dao.WorkflowDefinitionMapper;
import com.example.sea.workflow.entity.WorkflowDefinitionPO;
import com.example.sea.workflow.service.IWorkflowDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程定义查询实现。
 *
 * @author liuhuan
 * @date 2026-07-04
 */
@Service
@RequiredArgsConstructor
public class WorkflowDefinitionServiceImpl implements IWorkflowDefinitionService {

    private final WorkflowDefinitionMapper definitionMapper;
    private final WorkflowDefinitionConverter converter;

    @Override
    public CommonResult<List<WorkflowDefinitionVO>> listByBusinessType(String businessType) {
        LambdaQueryWrapper<WorkflowDefinitionPO> wrapper = Wrappers.<WorkflowDefinitionPO>lambdaQuery()
                .eq(WorkflowDefinitionPO::getBusinessType, businessType)
                .eq(WorkflowDefinitionPO::getEnabled, 1)
                .orderByDesc(WorkflowDefinitionPO::getVersion);
        List<WorkflowDefinitionPO> list = definitionMapper.selectList(wrapper);
        return CommonResult.success(list.stream().map(converter::entityToVo).collect(Collectors.toList()));
    }
}
