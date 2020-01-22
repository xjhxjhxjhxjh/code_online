package com.xjh.parser.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 中间代码
 *
 * @author xjhxjhxjh
 * @date 2019/12/22 13:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntermediateCode {
    /**
     * 中间代码id
     */
    private int id;
    /**
     * 操作符
     */
    private String op;
    /**
     * 操作数一
     */
    private String arg1;
    /**
     * 操作数二
     */
    private String arg2;
    /**
     * 结果
     */
    private String result;
}