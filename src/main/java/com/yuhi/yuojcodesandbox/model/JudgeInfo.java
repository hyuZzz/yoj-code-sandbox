package com.yuhi.yuojcodesandbox.model;

import lombok.Data;

/**
 * 题目配置
 */
@Data
public class JudgeInfo {

//    /**
//     * 时间限制（ms）
//     */
//    private Long timeLimit;
//
//    /**
//     * 内存限制（KB）
//     */
//    private Long memoryLimit;
//
//    /**
//     * 堆栈限制（KB）
//     */
//    private Long stackLimit;

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间（KB）
     */
    private Long time;


}
