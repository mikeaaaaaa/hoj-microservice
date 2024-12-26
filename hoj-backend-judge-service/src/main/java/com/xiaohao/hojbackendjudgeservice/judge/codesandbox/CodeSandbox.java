package com.xiaohao.hojbackendjudgeservice.judge.codesandbox;


import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeRequest;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
