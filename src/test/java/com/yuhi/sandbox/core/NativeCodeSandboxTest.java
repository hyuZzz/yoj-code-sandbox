package com.yuhi.sandbox.core;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.yuhi.sandbox.model.ExecuteCodeRequest;
import com.yuhi.sandbox.model.ExecuteCodeResponse;
import com.yuhi.sandbox.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class NativeCodeSandboxTest {
    @Test
    void testCpp() {
        CodeSandboxTemplate codeSandbox = CodeSandboxFactory.getInstance(QuestionSubmitLanguageEnum.cpp);
        String code = ResourceUtil.readStr("testcode/main.cpp", StandardCharsets.UTF_8);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest
                .builder()
                .inputList(Arrays.asList("1 2", "3 4"))
                .code(code)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(JSONUtil.toJsonStr(executeCodeResponse));
    }

}
