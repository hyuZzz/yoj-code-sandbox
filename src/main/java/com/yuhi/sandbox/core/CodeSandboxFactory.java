package com.yuhi.sandbox.core;

import com.yuhi.sandbox.model.enums.QuestionSubmitLanguageEnum;

public class CodeSandboxFactory {
    public static CodeSandboxTemplate getInstance(QuestionSubmitLanguageEnum language) {
        if (language == QuestionSubmitLanguageEnum.java) {
            return new JavaNativeCodeSandbox();
        } else if (language == QuestionSubmitLanguageEnum.cpp) {
            return new CppNativeCodeSandbox();
        } else {
            throw new RuntimeException("暂不支持");
        }
    }
}
