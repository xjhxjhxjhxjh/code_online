package com.xjh.lexer;

/**
 * 去除注释和空格
 *
 * @author xjhxjhxjh
 * @date 2019/12/20 9:25
 */
public class PreTreatment {

    public static String doPreTreatment(String code) {
        if (code == null || code.length() == 0){
            return "";
        }
        // 将\r\n转为\n
        code = code.replaceAll("\r\n", "\n");
        StringBuilder sb = removeNotes(code, new StringBuilder());
        code = removeBlanks(sb);
        return code;
    }

    /**
     * 去除行首空格
     * @param sb
     * @return
     */
    private static String removeBlanks(StringBuilder sb) {
        char[] charArray = sb.toString().toCharArray();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '\n') {
                code.append(charArray[i]);
                i++;
                for (; i < charArray.length; i++) {
                    if (charArray[i] != ' ') {
                        break;
                    }
                }
            }
            if (i < charArray.length) {
                code.append(charArray[i]);
            }
        }
        return code.toString();
    }

    private static StringBuilder removeNotes(String code, StringBuilder sb) {
        char[] codeArray = code.toCharArray();
        for (int i = 0; i < codeArray.length; i++) {
            // 去除//注释
            if (codeArray[i] == '/' && i + 1 < codeArray.length && codeArray[i + 1] == '/') {
                for (; i < codeArray.length; i++) {
                    if (codeArray[i] == '\n') {
                        break;
                    }
                }
            }
            // 去除/* */注释
            if (codeArray[i] == '/' && i + 1 < codeArray.length && codeArray[i + 1] == '*') {
                for (; i < codeArray.length; i++) {
                    if (codeArray[i] == '\n') {
                        sb.append('\n');
                    }
                    if (codeArray[i] == '/' && codeArray[i - 1] == '*') {
                        i++;
                        break;
                    }
                }
            }
            sb.append(codeArray[i]);
        }
        return sb;
    }
}
