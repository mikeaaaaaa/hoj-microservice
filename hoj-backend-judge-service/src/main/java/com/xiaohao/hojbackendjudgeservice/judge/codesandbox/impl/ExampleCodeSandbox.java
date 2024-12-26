package com.xiaohao.hojbackendjudgeservice.judge.codesandbox.impl;


import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeRequest;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeResponse;
import com.xiaohao.hojbackendmodel.model.dto.question.JudgeInfo;
import com.xiaohao.hojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.xiaohao.hojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 示例代码沙箱
 */
/**
 * 示例代码沙箱
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeResponse executeCodeResponse=new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        JudgeInfo judgeInfo=new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPT.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }
}