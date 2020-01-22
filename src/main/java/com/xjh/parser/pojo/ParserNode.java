package com.xjh.parser.pojo;

import com.xjh.lexer.pojo.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 语义分析栈节点
 *
 * @author xjhxjhxjh
 * @date 2019/12/22 10:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParserNode {
    /**
     * 节点类型
     */
    private String type;
    /**
     * 节点名
     */
    private String name;
    /**
     * 节点值
     */
    private String value;

    public final static String NONE_TERMINAL = "非终结符";
    public final static String TERMINAL = "终结符";
    public final static String ACTION_SIGN = "动作符";
    public final static String END = "结束符";
    /**
     * 非终结符集合
     */
    public static List<String> nonTerminal = Arrays.asList("S", "A", "B", "C", "D", "E", "F", "G", "H", "V",
            "L", "M", "N", "O", "P", "Q", "T", "X", "Y", "Z", "R", "U", "Z'", "U'", "E'", "H'", "L'", "T'");
    /**
     * 动作符集合
     */
    public static List<String> actionSign = Arrays.asList("ADD", "ADD_SUB", "SUB", "DIV_MUL", "MUL",
            "DIV", "SINGLE", "SINGLE_OP", "ASS_R", "ASS_Q", "ASS_F", "ASS_U", "TRAN_LF", "EQ", "EQ_U",
            "COMPARE", "COMPARE_OP", "IF_FJ", "IF_BACKPATCH_FJ", "IF_RJ", "IF_BACKPATCH_RJ", "WHILE_FJ",
            "WHILE_BACKPATCH_FJ", "IF_RJ", "FOR_FJ", "FOR_RJ", "FOR_BACKPATCH_FJ");

    /**
     * 是否为非终结符
     *
     * @param node
     * @return
     */
    public static boolean isNonTerminal(ParserNode node) {
        return nonTerminal.contains(node.name);
    }

    /**
     * 是否为动作集
     *
     * @param node
     * @return
     */
    public static boolean isActionSign(ParserNode node) {
        return actionSign.contains(node.name);
    }

    /**
     * 是否为终结符
     *
     * @param node
     * @return
     */
    public static boolean isTerm(ParserNode node) {
        return Type.isKey(node.name) || Type.isOperator(node.name) || Type.isSeparator(node.name) ||
                "id".equals(node.name) || "num".equals(node.name) || "str".equals(node.name);
    }
}