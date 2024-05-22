package com.yuhi.sandbox.core;

import com.yuhi.sandbox.model.CodeSandboxCmd;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


/**
 * java本机代码沙箱
 *
 * @author YUUU
 * @since 2023/08/21
 */
@Slf4j
public class JavaNativeCodeSandbox extends CodeSandboxTemplate {
    private static final String PREFIX = File.separator + "java";

    private static final String GLOBAL_CODE_DIR_PATH = File.separator + "tempCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = File.separator + "Main.java";
//    private static final String SECURITY_MANAGER_PATH = "D:\\YOJ\\yoj-code-sandbox\\src\\main\\resources\\security";
//
//    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";


    public JavaNativeCodeSandbox() {
        super.prefix = PREFIX;
        super.globalCodeDirPath = GLOBAL_CODE_DIR_PATH;
        super.globalCodeFileName = GLOBAL_JAVA_CLASS_NAME;
    }

    @Override
    public CodeSandboxCmd getCmd(String userCodeParentPath, String userCodePath) {
        return CodeSandboxCmd
                .builder()
                .compileCmd(String.format("javac -encoding utf-8 %s", userCodePath))
                .runCmd(String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", userCodeParentPath))
//                .runCmd(String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp \"%s\" -Djava.security.manager=MySecurityManager.java Main", userCodeParentPath))
//                .runCmd(String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main", userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME))
//                .runCmd("java -Xmx256m -Dfile.encoding=UTF-8 -cp " + userCodeParentPath + ";" + SECURITY_MANAGER_PATH + " -Djava.security.manager=" + SECURITY_MANAGER_CLASS_NAME + " Main")

                .build();
    }
}
