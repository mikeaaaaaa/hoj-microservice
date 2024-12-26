package com.xiaohao.hojbackendjudgeservice.judge.codesandbox;


import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.xiaohao.hojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂,根据类型创建代码沙箱
 */
public class CodeSandboxFactory {
    /**
     * 创建代码沙箱
     * @param type
     * @return
     */
    public static CodeSandbox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
