package com.xiaohao.hojbackendjudgeservice.judge.codesandbox;


import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeRequest;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CodeSandboxProxy implements CodeSandbox {
    private CodeSandbox codeSandbox;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代理代码沙箱请求信息：{}", executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代理代码沙箱结束信息：{}", executeCodeResponse);
        return executeCodeResponse;
    }
}
