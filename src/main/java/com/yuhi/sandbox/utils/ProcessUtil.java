package com.yuhi.sandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.yuhi.sandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 进程处理工具类
 *
 * @author YUUU
 * @since 2023/09/25
 */
@Slf4j
public class ProcessUtil {

    /**
     * 运行进行
     *
     * @param runProcess    运行进程
     * @param operationName 操作名称
     * @return {@link ExecuteMessage}
     */
    public static ExecuteMessage handleProcessMessage(Process runProcess, String operationName) {
        int exitCode;
        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();
        try {
            exitCode = runProcess.waitFor();
            // 正常退出
            if (exitCode == 0) {
                System.out.println(operationName + "成功");
            } else {
                // 异常退出
                System.out.println(operationName + "失败，错误码：" + exitCode);
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                String errorRunOutputLine;
                while ((errorRunOutputLine = errorBufferedReader.readLine()) != null) {
                    errorOutput.append(errorRunOutputLine);
                }
                log.error("错误输出为：{}", errorOutput);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String runOutputLine;
            while ((runOutputLine = bufferedReader.readLine()) != null) {
                output.append(runOutputLine);
            }
            if (StrUtil.isNotBlank(output)) {
                log.info("正常输出：{}", output);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ExecuteMessage.builder()
                .exitCode(exitCode)
                .message(output.toString())
                .errorMessage(errorOutput.toString())
                .build();
    }


    public static ExecuteMessage handleProcessInteraction(Process runProcess, String input, String operationName) {
        OutputStream outputStream = runProcess.getOutputStream();
        try {
            outputStream.write((input + "\n").getBytes());
            outputStream.flush();
            outputStream.close();
            return handleProcessMessage(runProcess, operationName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("关闭输入流失败");
            }
        }
    }

}
