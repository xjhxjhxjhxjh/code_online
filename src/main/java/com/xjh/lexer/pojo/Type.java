package com.xjh.lexer.pojo;

import java.util.Arrays;
import java.util.List;

/**
 * C语言关键词
 *
 * @author xjhxjhxjh
 * @date 2019/12/20 14:19
 */
public class Type {
    public final static String KEY = "关键字";
    public final static String IDENTIFIER = "标识符";
    public final static String SEPARATOR = "分隔符";
    public final static String OPERATOR = "运算符";
    public final static String CONST_CHAR = "字符常量";
    public final static String ESCAPE_CHAR = "转义字符常量";
    public final static String CONST_STR = "字符串常量";
    public final static String BOOLEN = "布尔常量";
    public final static String INT_10 = "十进制整数";
    public final static String INT_10_LONG = "十进制长整型";
    public final static String NUM_FLOAT = "浮点数";
    public final static String ERROR = "错误";
    public static List<String> key = Arrays.asList("auto", "break", "case", "char", "continue", "default", "do",
            "double", "else", "float", "enum", "extern", "for", "goto", "if", "int",
            "long", "register", "return", "short", "signed", "sizeof", "static", "struct",
            "switch", "typedef", "union", "unsigned", "void", "volatile", "while", "scanf", "printf", "main", "bool");

    public static List<String> separator = Arrays.asList("(", ")", "{", "}", "[", "]", ";", ",");

    public static List<String> operator = Arrays.asList("+", "++", "+=", "-", "--", "-=",
            "*", "*=", "/", "/=", ">", ">=", ">>", ">>=", "<", "<<=", "=", "==", "!",
            "!=", "&", "&&", "|", "|=", "~", "#");

    public static boolean isKey(String word) {
        return key.contains(word);
    }

    public static boolean isOperator(String word) {
        return operator.contains(word);
    }

    public static boolean isSeparator(String word) {
        return separator.contains(word);
    }
}
