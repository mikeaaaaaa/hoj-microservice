package com.xiaohao.hojbackendjudgeservice.controller.inner;

import com.xiaohao.hojbackendjudgeservice.judge.JudgeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("inner")
public class JudgeControllerInner {
    @Resource
    private JudgeService judgeService;

    @PostMapping("/doJudge")
    public void doJudge(Long questionSubmitId) {
        judgeService.doJudge(questionSubmitId);
    }
}
