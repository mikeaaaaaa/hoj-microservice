package com.xiaohao.hojbackendjudgeservice.judge.codesandbox.strategy;

/**
 * 上下文信息，用于传递判题信息
 */


import com.xiaohao.hojbackendmodel.model.dto.question.JudgeCase;
import com.xiaohao.hojbackendmodel.model.dto.question.JudgeInfo;
import com.xiaohao.hojbackendmodel.model.entity.Question;
import com.xiaohao.hojbackendmodel.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {
    private JudgeInfo judgeInfo;
    private List<String> inputList;
    private List<String> outputList;
    private List<JudgeCase>judgeCaseList;
    private Question question;
    private QuestionSubmit questionSubmit;
}
