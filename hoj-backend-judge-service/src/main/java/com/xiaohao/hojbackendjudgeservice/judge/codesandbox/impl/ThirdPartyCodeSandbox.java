package com.xiaohao.hojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xiaohao.hojbackendcommon.common.ErrorCode;
import com.xiaohao.hojbackendcommon.exception.BusinessException;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeRequest;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://localhost:8090/executeCode";
        String data = JSONUtil.toJsonStr(executeCodeRequest);
        String responseBody = HttpUtil.createPost(url).body(data).execute().body();
        if(StringUtils.isBlank(responseBody)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"第三方代码沙箱返回结果为空");
        }
        return JSONUtil.toBean(responseBody,ExecuteCodeResponse.class);
    }
}
