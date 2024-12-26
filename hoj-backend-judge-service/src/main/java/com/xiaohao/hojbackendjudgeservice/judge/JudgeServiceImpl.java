package com.xiaohao.hojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;

import com.xiaohao.hojbackendcommon.common.ErrorCode;
import com.xiaohao.hojbackendcommon.exception.BusinessException;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeRequest;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeResponse;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.strategy.JudgeContext;
import com.xiaohao.hojbackendmodel.model.dto.question.JudgeCase;
import com.xiaohao.hojbackendmodel.model.dto.question.JudgeInfo;
import com.xiaohao.hojbackendmodel.model.entity.Question;
import com.xiaohao.hojbackendmodel.model.entity.QuestionSubmit;
import com.xiaohao.hojbackendmodel.model.enums.ExecuteStatusEnum;
import com.xiaohao.hojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.xiaohao.hojbackendserviceclient.feignClient.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;
    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 传入提交的id，获取对应的题目信息
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 如果不为等待状态，抛出异常
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 更改题目状态为"判题中"，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 调用代码沙箱，获取执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        // 调用沙箱执行代码
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        // 这里需要优化一下，如果沙箱执行结果的status不是success，那么直接返回错误信息，不需要再执行判题策略
        if(Objects.equals(executeCodeResponse.getStatus(), ExecuteStatusEnum.FAILED.getValue())){
            // 修改数据库中的判题结果
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            // 设置判题失败
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(executeCodeResponse.getJudgeInfo()));
            update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
            return questionSubmitResult;
        }
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5.根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);


        // 调用判题管理器，获取判题结果
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        // 6.修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        // 设置判题成功
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }
}
