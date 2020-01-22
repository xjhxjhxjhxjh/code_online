package com.xjh.lexer.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xjhxjhxjh
 * @date 2019/12/20 13:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorWord {
    /**
     * 错误编号
     */
    int id;
    /**
     * 错误信息
     */
    String info;
    /**
     * 错误行数
     */
    int line;
    /**
     * 错误单词
     */
    LexerResult lexerResult;
}

