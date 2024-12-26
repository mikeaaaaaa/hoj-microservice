package com.xiaohao.hojbackendjudgeservice.judge;


import com.xiaohao.hojbackendmodel.model.entity.QuestionSubmit;

public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}
