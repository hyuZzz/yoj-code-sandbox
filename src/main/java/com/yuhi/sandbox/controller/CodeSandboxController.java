package com.yuhi.sandbox.controller;

import com.yuhi.sandbox.core.CodeSandboxFactory;
import com.yuhi.sandbox.core.CodeSandboxTemplate;
import com.yuhi.sandbox.model.ExecuteCodeRequest;
import com.yuhi.sandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CodeSandboxController {

    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest) {
        CodeSandboxTemplate sandboxTemplate = CodeSandboxFactory.getInstance(executeCodeRequest.getLanguage());
        return sandboxTemplate.executeCode(executeCodeRequest);
    }
}
