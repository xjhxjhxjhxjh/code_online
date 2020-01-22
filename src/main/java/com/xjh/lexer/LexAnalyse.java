package com.xjh.lexer;

import com.xjh.lexer.pojo.ErrorWord;
import com.xjh.lexer.pojo.LexerResult;
import com.xjh.lexer.pojo.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xjhxjhxjh
 * @date 2019/12/20 14:29
 */
public class LexAnalyse {

    /**
     * 当前位置
     */
    private static int index;
    /**
     * 当前行
     */
    private static int line;
    /**
     * code长度
     */
    private static int len;
    /**
     * code内容
     */
    private static char[] data;
    /**
     * 统计单词个数
     */
    private static int resultNum;
    /**
     * 错误个数
     */
    private static int errorNum;
    private static boolean ifError;
    private static LexerResult result;
    private static ErrorWord errorWord;
    /**
     * 单词表
     */
    private static List<LexerResult> resultList;
    /**
     * 错误信息表
     */
    private static List<ErrorWord> errorList;

    /**
     * 完成词法分析
     *
     * @param code
     * @return
     */
    public static Map<String, List> doLexer(String code) {
        code = PreTreatment.doPreTreatment(code);
        init(code);
        match0();
        Map<String, List> map = new HashMap<>();
        map.put("errorList", errorList);
        map.put("resultList", resultList);
        return map;
    }

    /**
     * 错误处理
     * @param errorInfo
     * @param str
     */
    private static void doError(String errorInfo, String str) {
        errorNum++;
        ifError = true;
        while (index < len) {
            if (data[index] == ' ' || data[index] == '\t' || data[index] == '\r' || data[index] == '\n') {
                break;
            }
            str += data[index++];
        }
        resultNum++;
        result = new LexerResult(resultNum, str, Type.ERROR, line, false);
        resultList.add(result);
        errorWord = new ErrorWord(errorNum, errorInfo, line, result);
        errorList.add(errorWord);
        match0();
    }

    /**
     * 处理换行
     * @return
     */
    private static boolean doChangeLine() {
        if (data[index] == '\n') {
            line++;
            index++;
            return true;
        }
        return false;
    }

    private static void match0() {
        if (index >= len) {
            return;
        }
        while (index < len && doChangeLine()) {

        }
        // 遇到空白符号就回到match0
        while (index < len && (data[index] == ' ' || data[index] == '\t' || data[index] == '\r')) {
            index++;
        }
        if (index >= len) {
            return;
        }
        // 标识符或关键字，跳到match1
        if (('a' <= data[index] && data[index] <= 'z')
                || ('A' <= data[index] && data[index] <= 'Z')
                || data[index] == '_') {
            match1();
        }
        // {,},[,],(,),\,,;跳到match2,分隔符
        else if ('{' == data[index] || data[index] == '}' || data[index] == '['
                || data[index] == ']' || data[index] == '(' || data[index] == ')'
                || data[index] == ',' || data[index] == '.' || data[index] == ';') {
            match2();
        }
        // 字符常量
        else if ('\'' == data[index]) {
            match3();
        }
        // 字符串常量
        else if ('\"' == data[index]) {
            match4();
        }
        // 数字
        else if ('0' <= data[index] && data[index] <= '9') {
            match6();
        }
        // + - & /
        else if ('+' == data[index] || '-' == data[index] || '&' == data[index] || '/' == data[index]) {
            match7();
        }
        // * / % = ! ^ > <
        else if ('*' == data[index] || '/' == data[index] || '^' == data[index] || '%' == data[index] ||
                '!' == data[index] || '=' == data[index] || '>' == data[index] || '<' == data[index]) {
            match8();
        }
        // & ? . #
        else if ('?' == data[index] || '&' == data[index] || '.' == data[index] || '#' == data[index] || '|' == data[index]) {
            match9();
        } else {
            doError("不是合法的字符", "");
        }
    }

    /**
     * 标识符和关键字
     */
    private static void match1() {
        if (index >= len) {
            return;
        }
        // 暂时字符串；
        String tempStr = "";
        tempStr += data[index];
        index++;
        while (index < len && (('a' <= data[index] && data[index] <= 'z')
                || ('A' <= data[index] && data[index] <= 'Z') || data[index] == '_'
                || data[index] == '_' || ('0' <= data[index] && data[index] <= '9'))) {
            tempStr += data[index];
            index++;
        }
        resultNum++;
        // 判断是否是关键字
        if (Type.isKey(tempStr)) {
            result = new LexerResult(resultNum, tempStr, Type.KEY, line, true);
            resultList.add(result);
        }
        // 标识符
        else {
            result = new LexerResult(resultNum, tempStr, Type.IDENTIFIER, line, true);
            resultList.add(result);
        }
        match0();
    }

    /**
     * {,},[,],(,),\,,;分隔符
     */
    private static void match2() {
        if (index >= len) {
            return;
        }
        String tempStr = "";
        tempStr += data[index];
        index++;
        resultNum++;
        result = new LexerResult(resultNum, tempStr, Type.SEPARATOR, line, true);
        resultList.add(result);
        match0();
    }

    /**
     * 字符常量处理
     */
    private static void match3() {
        if (index >= len) {
            return;
        }
        String tempStr = "";
        tempStr += data[index];
        index++;
        // 单个非\和'的字符
        if (data[index] != '\\' && data[index] != '\'') {
            tempStr += data[index];
            index++;
            // 单引号，字符接收
            if (data[index] == '\'') {
                tempStr += data[index];
                resultNum++;
                result = new LexerResult(resultNum, tempStr, Type.CONST_CHAR, line, true);
                resultList.add(result);
                index++;
            } else {
                doError("这不是一个合法的字符常量", tempStr);
            }

        }// 转义符号开始
        else if (data[index] == '\\') {
            index++;
            // 转义 a，b，f，n,r,t,v,\,',",?,0
            if (data[index] == 'a' || data[index] == 'b' || data[index] == 'f'
                    || data[index] == 'n' || data[index] == 'r'
                    || data[index] == 't' || data[index] == 'v'
                    || data[index] == '\\' || data[index] == '\''
                    || data[index] == '\"' || data[index] == '?'
                    || data[index] == '0') {
                tempStr += data[index];
                index++;
                if (data[index] == '\'') {
                    tempStr += data[index];
                    index++;
                    resultNum++;
                    result = new LexerResult(resultNum, tempStr, Type.ESCAPE_CHAR, line, true);
                    resultList.add(result);
                }
            } else {
                doError("这不是一个合法的转义字符常量", tempStr);
            }
        } else {
            doError("这不是一个合法的字符常量", tempStr);

        }
        match0();
    }

    /**
     * 字符串常量
     */
    private static void match4() {
        if (index >= len) {
            return;
        }
        String tempStr = "";
        tempStr += data[index];
        index++;
        while (true) {
            // 字符串中换行，错误
            if (doChangeLine()) {
                doError("字符串中不允许换行", tempStr);
                break;
            }
            if (data[index] != '\\' && data[index] != '\"') {
                tempStr += data[index];
                index++;
            } else if (data[index] == '\\') {
                tempStr += data[index];
                index++;
                tempStr += data[index];
                index++;

            } else if (data[index] == '\"') {
                tempStr += data[index];
                index++;
                resultNum++;
                result = new LexerResult(resultNum, tempStr, Type.CONST_STR, line, true);
                resultList.add(result);
                break;
            }
            // 字符串没结尾
            if (index >= len) {
                doError("缺少一个结尾的\"", tempStr);
                break;
            }
        }
        match0();
    }
    /**
     * 数字常量,10进制 整形及浮点数，科学计数法
     */
    private static void match6() {
        if (index >= len) {
            return;
        }
        // 暂时字符串；
        String tempStr = "";
        tempStr += data[index];
        index++;
        while (index < len && '0' <= data[index] && data[index] <= '9') {
            tempStr += data[index];
            index++;
        }
        if (data[index] == '.' || data[index] == 'e' || data[index] == 'E') {
            tempStr += data[index];
            index++;
            if ('0' <= data[index] && data[index] <= '9') {
                tempStr += data[index];
                index++;
                if (index >= len) {
                    return;
                }
                while ('0' <= data[index] && data[index] <= '9') {
                    tempStr += data[index];
                    index++;
                }
                // 10进制浮点数接收
                resultNum++;
                result = new LexerResult(resultNum, tempStr, Type.NUM_FLOAT, line, true);
                resultList.add(result);
            } else {
                doError("这不是一个合法的数字常量", tempStr);
            }
        }
        // 长整形接收
        else if (data[index] == 'L') {
            tempStr += data[index];
            index++;
            if (index >= len) {
                return;
            }
            resultNum++;
            result = new LexerResult(resultNum, tempStr, Type.INT_10_LONG, line, true);
            resultList.add(result);
        } else {
            // 10进制整形接收
            resultNum++;
            result = new LexerResult(resultNum, tempStr, Type.INT_10, line, true);
            resultList.add(result);
        }
        match0();
    }

    /**
     * ++, +=, --, -=, +, -, /, /=, &, &=
     */
    private static void match7() {
        if (index >= len) {
            return;
        }
        String tempStr = "";
        tempStr += data[index];
        index++;
        if (index < len && (data[index] == data[index - 1] || data[index] == '=')) {
            tempStr += data[index];
            index++;
            // ++, +=接收
            resultNum++;
            result = new LexerResult(resultNum, tempStr, Type.OPERATOR, line, true);
            resultList.add(result);
        } else {
            // +接收
            resultNum++;
            result = new LexerResult(resultNum, tempStr, Type.OPERATOR, line, true);
            resultList.add(result);
        }
        match0();
    }

    /**
     * *, *=, /, /=, %, %=, =, ==, !, !=, ^, ^=
     */
    private static void match8() {
        if (index >= len) {
            return;
        }
        String tempStr = "";
        tempStr += data[index];
        index++;
        if (data[index] == '=') {
            tempStr += data[index];
            index++;
            resultNum++;
            result = new LexerResult(resultNum, tempStr, Type.OPERATOR, line, true);
            resultList.add(result);
        } else {
            resultNum++;
            result = new LexerResult(resultNum, tempStr, Type.OPERATOR, line, true);
            resultList.add(result);
        }
        match0();
    }

    /**
     * . ? ~
     */
    private static void match9() {
        if (index >= len) {
            return;
        }
        String tempStr = "";
        tempStr += data[index];
        index++;
        resultNum++;
        result = new LexerResult(resultNum, tempStr, Type.OPERATOR, line, true);
        resultList.add(result);
        match0();
    }

    /**
     * 初始化
     *
     * @param code
     */
    private static void init(String code) {
        ifError = false;
        index = 0;
        line = 1;
        data = code.toCharArray();
        len = data.length;
        resultNum = 0;
        errorNum = 0;
        errorList = new ArrayList<>();
        resultList = new ArrayList<>();
    }
}