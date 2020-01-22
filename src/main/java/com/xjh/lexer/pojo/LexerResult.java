package com.xjh.lexer.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 词法分析结果
 *
 * @author xjhxjhxjh
 * @date 2019/12/20 14:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LexerResult {
    /**
     * 单词序号
     */
    int id;
    /**
     * 单词的值
     */
    String value;
    /**
     * 单词类型
     */
    String type;
    /**
     * 单词所在行
     */
    int line;
    /**
     * 单词是否合法
     */
    boolean flag = true;
}