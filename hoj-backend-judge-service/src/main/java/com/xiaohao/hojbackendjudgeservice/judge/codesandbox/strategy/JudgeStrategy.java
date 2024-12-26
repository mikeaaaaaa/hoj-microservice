package com.xiaohao.hojbackendjudgeservice.judge.codesandbox.strategy;


import com.xiaohao.hojbackendmodel.model.dto.question.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
