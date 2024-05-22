package com.yuhi.sandbox.core;


import com.yuhi.sandbox.model.ExecuteCodeRequest;
import com.yuhi.sandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱
 *
 * @author YUUU
 * @since 2023/08/15
 */
public interface CodeSandbox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest 执行代码请求
     * @return {@link ExecuteCodeResponse}
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
