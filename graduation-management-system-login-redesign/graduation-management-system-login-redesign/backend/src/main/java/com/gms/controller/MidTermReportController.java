package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.MidTermReport;
import com.gms.entity.ThesisProposal;
import com.gms.mapper.MidTermReportMapper;
import com.gms.mapper.ThesisProposalMapper;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/midterm-reports")
public class MidTermReportController {
    private final MidTermReportMapper mapper;
    private final ThesisProposalMapper thesisProposalMapper;

    public MidTermReportController(MidTermReportMapper mapper,
                                   ThesisProposalMapper thesisProposalMapper) {
        this.mapper = mapper;
        this.thesisProposalMapper = thesisProposalMapper;
    }

    @GetMapping
    public ApiResponse<List<MidTermReport>> list(@RequestParam(required = false) Integer thesisId,
                                                 @RequestParam(required = false) String status) {
        QueryWrapper<MidTermReport> qw = new QueryWrapper<>();
        if (thesisId != null) {
            qw.eq("Thesis_proposal_Tp_id", thesisId);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq("Mtr_status", status);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @GetMapping("/{id}")
    public ApiResponse<MidTermReport> get(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.selectById(id));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody MidTermReport item) {
        if (item.getThesis_proposal_Tp_id() == null) {
            return ApiResponse.error("中期汇报提交失败：未选择开题报告。");
        }
        ThesisProposal thesis = thesisProposalMapper.selectById(item.getThesis_proposal_Tp_id());
        if (thesis == null) {
            return ApiResponse.error("中期汇报提交失败：开题报告不存在。");
        }
        if (!"已通过".equals(thesis.getTp_status())) {
            return ApiResponse.error("中期汇报提交失败：仅可基于已通过的开题报告提交。");
        }
        QueryWrapper<MidTermReport> qw = new QueryWrapper<>();
        qw.eq("Thesis_proposal_Tp_id", item.getThesis_proposal_Tp_id());
        List<MidTermReport> existing = mapper.selectList(qw);
        if (!existing.isEmpty()) {
            MidTermReport first = existing.get(0);
            if (!"未通过".equals(first.getMtr_status())) {
                return ApiResponse.error("中期汇报提交失败：该开题报告已有待处理或已通过的中期汇报。");
            }
            item.setMtr_id(first.getMtr_id());
            item.setMtr_status("已提交");
            mapper.updateById(item);
            return ApiResponse.ok(first.getMtr_id());
        }
        item.setMtr_status("已提交");
        mapper.insert(item);
        return ApiResponse.ok(item.getMtr_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody MidTermReport item) {
        if (item.getChecker_id() != null && !item.getChecker_id().isEmpty()) {
            item.setCheck_time(LocalDateTime.now());
        }
        item.setMtr_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.deleteById(id) > 0);
    }
}
