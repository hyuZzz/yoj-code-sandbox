package com.yuhi.yuojcodesandbox;

import com.yuhi.yuojcodesandbox.model.ExecuteCodeRequest;
import com.yuhi.yuojcodesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}

