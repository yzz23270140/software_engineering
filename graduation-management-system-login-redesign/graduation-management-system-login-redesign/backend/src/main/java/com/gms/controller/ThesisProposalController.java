package com.gms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gms.common.ApiResponse;
import com.gms.entity.ThesisProposal;
import com.gms.entity.TopicSelectionApplication;
import com.gms.mapper.TopicSelectionApplicationMapper;
import com.gms.mapper.ThesisProposalMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/thesis-proposals")
public class ThesisProposalController {
    private final ThesisProposalMapper mapper;
    private final TopicSelectionApplicationMapper applicationMapper;

    public ThesisProposalController(ThesisProposalMapper mapper,
                                    TopicSelectionApplicationMapper applicationMapper) {
        this.mapper = mapper;
        this.applicationMapper = applicationMapper;
    }

    @GetMapping
    public ApiResponse<List<ThesisProposal>> list(@RequestParam(required = false) Integer applicationId,
                                                  @RequestParam(required = false) String status) {
        QueryWrapper<ThesisProposal> qw = new QueryWrapper<>();
        if (applicationId != null) {
            qw.eq("Topic_selection_application_id", applicationId);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq("Tp_status", status);
        }
        return ApiResponse.ok(mapper.selectList(qw));
    }

    @GetMapping("/{id}")
    public ApiResponse<ThesisProposal> get(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.selectById(id));
    }

    @PostMapping
    public ApiResponse<Integer> create(@RequestBody ThesisProposal item) {
        if (item.getTopic_selection_application_id() == null) {
            return ApiResponse.error("开题报告提交失败：未找到选题申请ID。");
        }
        TopicSelectionApplication application = applicationMapper.selectById(item.getTopic_selection_application_id());
        if (application == null) {
            return ApiResponse.error("开题报告提交失败：选题申请不存在。");
        }
        String studentId = application.getStudent_Stu_id();
        if (hasApprovedProposal(studentId, null)) {
            return ApiResponse.error("开题报告提交失败：该学生已有通过的开题报告。");
        }
        if (hasActiveProposalForApplication(item.getTopic_selection_application_id())) {
            return ApiResponse.error("开题报告提交失败：该选题已有待处理或已通过的开题报告。");
        }
        mapper.insert(item);
        return ApiResponse.ok(item.getTp_id());
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Integer id, @RequestBody ThesisProposal item) {
        ThesisProposal existing = mapper.selectById(id);
        if (existing == null) {
            return ApiResponse.error("开题报告不存在。");
        }
        if (Objects.equals(item.getTp_status(), "已通过")) {
            TopicSelectionApplication application = applicationMapper.selectById(existing.getTopic_selection_application_id());
            if (application == null) {
                return ApiResponse.error("开题报告审核失败：选题申请不存在。");
            }
            String studentId = application.getStudent_Stu_id();
            if (hasApprovedProposal(studentId, id)) {
                return ApiResponse.error("审核失败：该学生已有通过的开题报告。");
            }
        }
        item.setTp_id(id);
        return ApiResponse.ok(mapper.updateById(item) > 0);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.deleteById(id) > 0);
    }

    private boolean hasActiveProposalForApplication(Integer applicationId) {
        QueryWrapper<ThesisProposal> qw = new QueryWrapper<>();
        qw.eq("Topic_selection_application_id", applicationId);
        List<ThesisProposal> rows = mapper.selectList(qw);
        for (ThesisProposal row : rows) {
            if (!"未通过".equals(row.getTp_status())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasApprovedProposal(String studentId, Integer excludeTpId) {
        if (studentId == null || studentId.isEmpty()) {
            return false;
        }
        QueryWrapper<TopicSelectionApplication> appQw = new QueryWrapper<>();
        appQw.eq("Student_Stu_id", studentId);
        List<TopicSelectionApplication> apps = applicationMapper.selectList(appQw);
        if (apps.isEmpty()) {
            return false;
        }
        List<Integer> appIds = apps.stream()
            .map(TopicSelectionApplication::getApplication_id)
            .collect(Collectors.toList());
        QueryWrapper<ThesisProposal> tpQw = new QueryWrapper<>();
        tpQw.eq("Tp_status", "已通过");
        tpQw.in("Topic_selection_application_id", appIds);
        if (excludeTpId != null) {
            tpQw.ne("Tp_id", excludeTpId);
        }
        return mapper.selectCount(tpQw) > 0;
    }
}
