package com.yuhi.sandbox.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.yuhi.sandbox.model.*;
import com.yuhi.sandbox.utils.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 代码沙箱模板
 * 注意每个实现类必须自定义代码存放路径，参考{@link JavaNativeCodeSandbox}
 *
 * @author YUUU
 * @since 2023/09/01
 */
@Slf4j
public abstract class CodeSandboxTemplate implements CodeSandbox {

    String prefix;

    String globalCodeDirPath;

    String globalCodeFileName;

    /**
     * 超时时间，超过5秒则结束
     */
    public static final Long DEFAULT_TIME_OUT = 5000L;

    private static final List<String> blackList = Arrays.asList("Files", "exec","dir");
    private static final WordTree WORD_TREE;

    static {
        // 初始化字典树
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }


    /**
     * 每个实现类必须实现编译以及运行的cmd
     *
     * @param userCodeParentPath 代码所在的父目录
     * @param userCodePath       代码所在目录
     * @return {@link CodeSandboxCmd}
     */
    abstract CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath);

    /**
     * 保存代码到文件中，注意这里需要实现，不同编程语言要放到不同文件夹中
     * 保存到文件中的格式应为: UUID/代码文件，后面删除代码文件需要将代码文件的父文件删除
     *
     * @param code 代码
     * @return {@link File}
     */
    private File saveCodeToFile(String code) {
        String globalCodePath = System.getProperty("user.dir") + globalCodeDirPath;
        if (!FileUtil.exist(globalCodePath)) {
            FileUtil.mkdir(globalCodePath);
        }
        // 存放用户代码
        String userCodeParentPath = globalCodePath + prefix + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + globalCodeFileName;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    /**
     * 编译代码，注意编译代码要返回编译的信息
     *
     * @param compileCmd 编译命令
     * @return {@link ExecuteMessage}
     * @throws IOException IOException
     */
    private ExecuteMessage compileCode(String compileCmd) throws IOException {
        Process compileProcess = Runtime.getRuntime().exec(compileCmd);
        return ProcessUtil.handleProcessMessage(compileProcess, "编译");
    }


    /**
     * 运行代码
     *
     * @param inputList 输入用例
     * @param runCmd    运行的cmd
     * @return {@link List}<{@link ExecuteMessage}>
     * @throws RuntimeException RuntimeException
     */
    private List<ExecuteMessage> runCode(List<String> inputList, String runCmd) throws RuntimeException {
        List<ExecuteMessage> executeMessageList = new LinkedList<>(); // 创建一个空的执行消息列表
        System.out.println(inputList);
        for (String input : inputList) {//遍历输入列表中的每个输入、进程对象、计时器线程对象
            Process runProcess;
            Thread computeTimeThread;

            try {
                runProcess = Runtime.getRuntime().exec(runCmd);
                computeTimeThread = new Thread(() -> {
                    try {
                        Thread.sleep(DEFAULT_TIME_OUT);//设置运行代码最长时间，若达到此事件代码未运行完毕则中断服务
                        if (runProcess.isAlive()) {
                            log.info("超时了，中断");
                            runProcess.destroy();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                // 启动计时器线程
                computeTimeThread.start();
                StopWatch stopWatch = new StopWatch();
                // 开始计时
                stopWatch.start();
                // 处理进程的交互操作，并返回执行消息
                ExecuteMessage executeMessage = ProcessUtil.handleProcessInteraction(runProcess, input, "运行");
                // 停止计时
                stopWatch.stop();
                // 停止计时器线程
                computeTimeThread.stop();
                // 设置执行时间到执行消息对象
                executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
                // 将执行消息对象添加到执行消息列表中
                executeMessageList.add(executeMessage);
            } catch (IOException e) {
                // 返回执行消息列表
                throw new RuntimeException(e);
            }

        }
        return executeMessageList;
    }


    @Override
    public final ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();

        // 校验代码中是否包含黑名单中的命令
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("包含禁止词：" + foundWord.getFoundWord());
            return ExecuteCodeResponse
                    .builder()
                    .status(2)
                    .message("包含禁止词")
                    .build();
        }

        // 保存代码
        File userCodeFile = saveCodeToFile(code);

        String userCodePath = userCodeFile.getAbsolutePath();
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        CodeSandboxCmd cmdFromLanguage = getCmd(userCodeParentPath, userCodePath);
        String compileCmd = cmdFromLanguage.getCompileCmd();
        String runCmd = cmdFromLanguage.getRunCmd();
        // 编译代码
        try {
            ExecuteMessage executeMessage = compileCode(compileCmd);
            if (executeMessage.getExitCode() != 0) {
                FileUtil.del(userCodeParentPath);
                return ExecuteCodeResponse
                        .builder()
                        .status(2)
                        .message("含有违禁词")
                        .build();
            }
        } catch (IOException e) {
            FileUtil.del(userCodeParentPath);
            return errorResponse(e);
        }

        // 执行代码
        try {
            List<ExecuteMessage> executeMessageList = runCode(inputList, runCmd);
            // 返回处理结果
            ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
            executeCodeResponse.setStatus(1);
            JudgeInfo judgeInfo = new JudgeInfo();
            executeCodeResponse.setJudgeInfo(judgeInfo);
            List<String> outputList = new LinkedList<>();
            long maxTime = 0;

            for (ExecuteMessage executeMessage : executeMessageList) {
                if (ObjectUtil.equal(0, executeMessage.getExitCode())) {
                    outputList.add(executeMessage.getMessage());
                } else {
                    executeCodeResponse.setMessage(executeMessage.getErrorMessage());
                    executeCodeResponse.setStatus(3);
                    break;
                }
                maxTime = Math.max(maxTime, executeMessage.getTime());
            }
            judgeInfo.setMessage(executeMessageList.toString());
            judgeInfo.setTime(maxTime);
            executeCodeResponse.setOutputList(outputList);

            FileUtil.del(userCodeParentPath);

            System.out.println("运行成功  EexecuteCodeResponse"+executeCodeResponse);
            return executeCodeResponse;
        } catch (RuntimeException e) {
            FileUtil.del(userCodeParentPath);
            return errorResponse(e);
        }
    }

    final ExecuteCodeResponse errorResponse(Throwable e) {
        return ExecuteCodeResponse
                .builder()
                .outputList(new ArrayList<>())
                .message(e.getMessage())
                .judgeInfo(new JudgeInfo())
                .status(2)
                .build();
    }
}
