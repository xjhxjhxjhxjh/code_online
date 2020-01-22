package com.xjh.parser;

import com.xjh.lexer.pojo.ErrorWord;
import com.xjh.lexer.pojo.LexerResult;
import com.xjh.lexer.pojo.Type;
import com.xjh.parser.pojo.IntermediateCode;
import com.xjh.parser.pojo.ParserNode;

import java.util.*;

/**
 * @author xjhxjhxjh
 * @date 2019/12/22 9:38
 */
public class Parser {

    /**
     * 单词表
     */
    private static List<LexerResult> wordList;
    /**
     * 分析栈
     */
    private static Stack<ParserNode> analyseStack;
    /**
     * 语义栈
     */
    private static Stack<String> semanticStack;
    /**
     * 错误信息
     */
    private static List<ErrorWord> errorList;
    /**
     * 分析栈数据
     */
    private static StringBuilder sb;
    /**
     * 错误个数
     */
    private static int errorCount;
    /**
     * 是否出错
     */
    private static boolean graErrorFlag;
    private static ParserNode S, B, A, C, X, Y, R, Z, Z1, U, U1, E, E1, H, H1, G, M, D, L, L1, T, T1, F, O, P, Q,
            ADD_SUB, DIV_MUL, ADD, SUB, DIV, MUL, ASS_F, ASS_R, ASS_Q, ASS_U, TRAN_LF, SINGLE, SINGLE_OP, EQ,
            EQ_U1, COMPARE, COMPARE_OP, IF_FJ, IF_RJ, IF_backPatch_FJ, IF_backPatch_RJ, WHILE_FJ, WHILE_RJ,
            WHILE_backPatch_FJ, FOR_FJ, FOR_RJ, FOR_backPatch_FJ, V;
    /**
     * 栈顶元素
     */
    private static ParserNode top;
    /**
     * 待分析单词
     */
    private static LexerResult currentWord;
    /**
     * 四元式列表
     */
    private static List<IntermediateCode> intermediateCodesList;
    /**
     * 四元式个数
     */
    private static int intermediateCodeCount;
    private static int tempCount;
    private static String OP = null;
    private static String ARG1, ARG2, RES;
    private static ErrorWord error;
    private static Stack<Integer> if_fj, if_rj, while_fj, while_rj, for_fj, for_rj;
    private static Stack<String> for_op;

    /**
     * 进行语法分析
     * @param lexerResults
     */
    public static Map doParser(List<LexerResult> lexerResults) {
        init();
        wordList = lexerResults;
        HashMap<String, Object> map = new HashMap<>();
        // 记录步骤数
        int cnt = 0;
        error = null;
        analyseStack.add(0, S);
        analyseStack.add(1, new ParserNode(ParserNode.END, "#", null));
        semanticStack.add("#");
        while (!analyseStack.empty() && !wordList.isEmpty()) {
            // 死循环就退出
            if (cnt++ > 2000) {
                graErrorFlag = true;
                doError("运行失败");
                break;
            }
            top = analyseStack.get(0);
            currentWord = wordList.get(0);
            if ("#".equals(currentWord.getValue()) && "#".equals(top.getName())) {
                analyseStack.remove(0);
                wordList.remove(0);
            } else if ("#".equals(top.getName())) {
                analyseStack.remove(0);
                graErrorFlag = true;
                break;
                // 终极符时的处理
            } else if (ParserNode.isTerm(top)) {
                termOP(top.getName());
            } else if (ParserNode.isNonTerminal(top)) {
                nonTermOP(top.getName());
                // 栈顶是动作符号时的处理
            } else if (top.getType().equals(ParserNode.ACTION_SIGN)) {
                actionSignOP();
            }
            sb.append("步骤" + cnt + "    当前分析栈:    ");
            List<String> actionSign = ParserNode.actionSign;
            for (int i = 0; i < analyseStack.size(); i++) {
                int flag = 1;
                for (String s : actionSign) {
                    if (s.equals(analyseStack.get(i).getName())) {
                        flag = 0;
                        break;
                    }
                }
                if (flag == 1) {
                    sb.append(analyseStack.get(i).getName());
                }
            }
            sb.append("    余留符号串：    ");
            for (int j = 0; j < wordList.size(); j++) {
                sb.append(wordList.get(j).getValue());
            }
            sb.append("    语义栈:    ");
            for (int k = semanticStack.size() - 1; k >= 0; k--) {
                sb.append(semanticStack.get(k));
            }
        }
        sb.append("步骤" + cnt + "    当前分析栈:    # 余留符号串：    语义栈:    #");
        Iterator<IntermediateCode> iterator = intermediateCodesList.iterator();
        while (iterator.hasNext()){
            IntermediateCode next = iterator.next();
            System.out.println(next.getOp());
            if (next.getOp() == null){
                iterator.remove();
            }
        }
        map.put("intermediateCodesList", intermediateCodesList);
        map.put("errorList", errorList);
        map.put("info", sb.toString().replaceAll("步骤", "<br>步骤"));
        System.out.println(sb.toString().replaceAll("步骤", "\n步骤"));
        return map;
    }

    /**
     * 出错处理
     * param reason
     */
    private static void doError(String reason) {
        errorCount++;
        wordList.remove(0);
        error = new ErrorWord(errorCount, reason, currentWord.getLine(), currentWord);
        errorList.add(error);
        graErrorFlag = true;
    }

    /**
     * 非终结符操作
     * param nonTerm
     */
    private static void nonTermOP(String nonTerm) {
        if ("Z'".equals(nonTerm)) {
            nonTerm = "1";
        } else if ("U'".equals(nonTerm)) {
            nonTerm = "2";
        } else if ("E'".equals(nonTerm)) {
            nonTerm = "3";
        } else if ("H'".equals(nonTerm)) {
            nonTerm = "4";
        } else if ("L'".equals(nonTerm)) {
            nonTerm = "5";
        } else if ("T'".equals(nonTerm)) {
            nonTerm = "6";
        }
        // 栈顶为非终结符处理
        if (!analyseStack.empty()) {
            analyseStack.remove(0);
        }
        if (nonTerm.charAt(0) == 'S') {
            if ("int".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "int", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "main", null));
                analyseStack.add(2, new ParserNode(ParserNode.TERMINAL, "(", null));
                analyseStack.add(3, new ParserNode(ParserNode.TERMINAL, ")", null));
                analyseStack.add(4, new ParserNode(ParserNode.TERMINAL, "{", null));
                analyseStack.add(5, A);
                analyseStack.add(6, new ParserNode(ParserNode.TERMINAL, "}", null));
            } else {
                doError("找不到主函数");
            }
        } else if (nonTerm.charAt(0) == 'A') {
            if (Type.isKey(currentWord.getValue()) || Type.IDENTIFIER.equals(currentWord.getType())) {
                analyseStack.add(0, C);
                analyseStack.add(1, A);
            }
        } else if (nonTerm.charAt(0) == 'B') {
            if ("printf".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "printf", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "(", null));
                analyseStack.add(2, P);
                analyseStack.add(3, new ParserNode(ParserNode.TERMINAL, ")", null));
                analyseStack.add(4, A);
                analyseStack.add(5, new ParserNode(ParserNode.TERMINAL, ";", null));
            } else if ("scanf".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "scanf", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "(", null));
                analyseStack.add(2, new ParserNode(ParserNode.TERMINAL, "id", null));
                analyseStack.add(3, new ParserNode(ParserNode.TERMINAL, ")", null));
                analyseStack.add(4, A);
                analyseStack.add(5, new ParserNode(ParserNode.TERMINAL, ";", null));
            } else if ("if".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "if", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "(", null));
                analyseStack.add(2, G);
                analyseStack.add(3, new ParserNode(ParserNode.TERMINAL, ")", null));
                analyseStack.add(4, IF_FJ);
                analyseStack.add(5, new ParserNode(ParserNode.TERMINAL, "{", null));
                analyseStack.add(6, A);
                analyseStack.add(7, new ParserNode(ParserNode.TERMINAL, "}", null));
                analyseStack.add(8, IF_backPatch_FJ);
                analyseStack.add(9, IF_RJ);
                analyseStack.add(10, new ParserNode(ParserNode.TERMINAL, "else", null));
                analyseStack.add(11, new ParserNode(ParserNode.TERMINAL, "{", null));
                analyseStack.add(12, A);
                analyseStack.add(13, new ParserNode(ParserNode.TERMINAL, "}", null));
                analyseStack.add(14, IF_backPatch_RJ);
            } else if ("while".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "while", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "(", null));
                analyseStack.add(2, G);
                analyseStack.add(3, new ParserNode(ParserNode.TERMINAL, ")", null));
                analyseStack.add(4, WHILE_FJ);
                analyseStack.add(5, new ParserNode(ParserNode.TERMINAL, "{", null));
                analyseStack.add(6, A);
                analyseStack.add(7, new ParserNode(ParserNode.TERMINAL, "}", null));
                analyseStack.add(8, WHILE_RJ);
                analyseStack.add(9, WHILE_backPatch_FJ);
            } else if ("for".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "for", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "(", null));
                analyseStack.add(2, Y);
                analyseStack.add(3, Z);
                analyseStack.add(4, new ParserNode(ParserNode.TERMINAL, ";", null));
                analyseStack.add(5, G);
                analyseStack.add(6, FOR_FJ);
                analyseStack.add(7, new ParserNode(ParserNode.TERMINAL, ";", null));
                analyseStack.add(8, Q);
                analyseStack.add(9, new ParserNode(ParserNode.TERMINAL, ")", null));
                analyseStack.add(10, new ParserNode(ParserNode.TERMINAL, "{", null));
                analyseStack.add(11, A);
                analyseStack.add(12, SINGLE);
                analyseStack.add(13, new ParserNode(ParserNode.TERMINAL, "}", null));
                analyseStack.add(14, FOR_RJ);
                analyseStack.add(15, FOR_backPatch_FJ);
            } else if ("return".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "return", null));
            }
        } else if (nonTerm.charAt(0) == 'C') {
            analyseStack.add(0, X);
            analyseStack.add(1, B);
            analyseStack.add(2, R);
        } else if (nonTerm.charAt(0) == 'X') {
            if ("int".equals(currentWord.getValue()) || "char".equals(currentWord.getValue()) || "bool".equals(currentWord.getValue())) {
                analyseStack.add(0, Y);
                analyseStack.add(1, Z);
                analyseStack.add(2, new ParserNode(ParserNode.TERMINAL, ";", null));
            }
        } else if (nonTerm.charAt(0) == 'Y') {
            if ("int".equals(currentWord.getValue()) || "char".equals(currentWord.getValue()) || "bool".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, currentWord.getValue(), null));
            } else {
                doError("非法数据类型");
            }
        } else if (nonTerm.charAt(0) == 'Z') {
            if (Type.IDENTIFIER.equals(currentWord.getType())) {
                analyseStack.add(0, U);
                analyseStack.add(1, Z1);
            } else {
                doError("非法标识符");
            }
            // z'
        } else if (nonTerm.charAt(0) == '1') {
            if (",".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, ",", null));
                analyseStack.add(1, Z);
            }
        } else if (nonTerm.charAt(0) == 'U') {
            if (Type.IDENTIFIER.equals(currentWord.getType())) {
                analyseStack.add(0, ASS_U);
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "id", null));
                analyseStack.add(2, U1);
            } else {
                doError("非法标识符");
            }
            // u'
        } else if (nonTerm.charAt(0) == '2') {
            if ("=".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "=", null));
                analyseStack.add(1, L);
                analyseStack.add(2, EQ_U1);
            }
        } else if (nonTerm.charAt(0) == 'R') {
            if (Type.IDENTIFIER.equals(currentWord.getType())) {
                analyseStack.add(0, new ParserNode(ParserNode.ACTION_SIGN, "ASS_R", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "id", null));
                analyseStack.add(2, V);
                analyseStack.add(2, new ParserNode(ParserNode.TERMINAL, "=", null));
                analyseStack.add(3, L);
                analyseStack.add(4, EQ);
                analyseStack.add(5, new ParserNode(ParserNode.TERMINAL, ";", null));
            }
        } else if (nonTerm.charAt(0) == 'P') {
            if (Type.IDENTIFIER.equals(currentWord.getType())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "id", null));
            } else if (Type.INT_10.equals(currentWord.getType())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "num", null));
            } else if (Type.CONST_STR.equals(currentWord.getType())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "str", null));
            }
        } else if (nonTerm.charAt(0) == 'E') {
            if (Type.IDENTIFIER.equals(currentWord.getType()) || Type.INT_10.equals(currentWord.getType())
                    || "(".equals(currentWord.getValue())) {
                analyseStack.add(0, H);
                analyseStack.add(1, E1);
            } else {
                doError("不能进行算术运算的数据类型");
            }
            // E'
        } else if (nonTerm.charAt(0) == '3') {
            if ("&&".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "&&", null));
                analyseStack.add(1, E);
            }
        } else if (nonTerm.charAt(0) == 'H') {
            if (Type.IDENTIFIER.equals(currentWord.getType()) || Type.INT_10.equals(currentWord.getType())
                    || "(".equals(currentWord.getValue())) {
                analyseStack.add(0, G);
                analyseStack.add(1, H1);
            } else {
                doError("不能进行算术运算的数据类型");
            }
            // H'
        } else if (nonTerm.charAt(0) == '4') {
            if ("||".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "||", null));
                analyseStack.add(1, E);
            }
        } else if (nonTerm.charAt(0) == 'D') {
            if ("==".equals(currentWord.getValue()) || "!=".equals(currentWord.getValue()) ||
                    ">".equals(currentWord.getValue()) || "<".equals(currentWord.getValue())) {
                analyseStack.add(0, COMPARE_OP);
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, currentWord.getValue(), null));
            } else {
                doError("非法运算符");
            }
        } else if (nonTerm.charAt(0) == 'G') {
            if (Type.IDENTIFIER.equals(currentWord.getType()) || Type.INT_10.equals(currentWord.getType())) {
                analyseStack.add(0, F);
                analyseStack.add(1, D);
                analyseStack.add(2, F);
                analyseStack.add(3, COMPARE);
            } else if ("(".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "(", null));
                analyseStack.add(1, E);
                analyseStack.add(2, new ParserNode(ParserNode.TERMINAL, ")", null));
            } else if ("!".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "!", null));
                analyseStack.add(1, E);
            } else {
                doError("不能进行算术运算的数据类型或括号不匹配");
            }
        } else if (nonTerm.charAt(0) == 'L') {
            if (Type.IDENTIFIER.equals(currentWord.getType()) ||
                    Type.INT_10.equals(currentWord.getType()) || "(".equals(currentWord.getValue())) {
                analyseStack.add(0, T);
                analyseStack.add(1, L1);
                analyseStack.add(2, ADD_SUB);
            } else {
                doError("不能进行算术运算的数据类型或括号不匹配");
            }
            // l'
        } else if (nonTerm.charAt(0) == '5') {
            if ("+".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "+", null));
                analyseStack.add(1, L);
                analyseStack.add(2, ADD);
            } else if ("-".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "-", null));
                analyseStack.add(1, L);
                analyseStack.add(2, SUB);
            }
        } else if (nonTerm.charAt(0) == 'T') {
            if (Type.IDENTIFIER.equals(currentWord.getType()) ||
                    Type.INT_10.equals(currentWord.getType()) || "(".equals(currentWord.getValue())) {
                analyseStack.add(0, F);
                analyseStack.add(1, T1);
                analyseStack.add(2, DIV_MUL);
            } else {
                doError("不能进行算术运算的数据类型");
            }
            // T'
        } else if (nonTerm.charAt(0) == '6') {
            if ("*".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "*", null));
                analyseStack.add(1, T);
                analyseStack.add(2, MUL);
            } else if ("/".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "/", null));
                analyseStack.add(1, T);
                analyseStack.add(2, DIV);
            }
        } else if (nonTerm.charAt(0) == 'F') {
            if (Type.IDENTIFIER.equals(currentWord.getType())) {
                analyseStack.add(0, ASS_F);
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "id", null));
            } else if (Type.INT_10.equals(currentWord.getType())) {
                analyseStack.add(0, ASS_F);
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "num", null));
            } else if ("(".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.TERMINAL, "(", null));
                analyseStack.add(1, L);
                analyseStack.add(2, new ParserNode(ParserNode.TERMINAL, ")", null));
                analyseStack.add(3, TRAN_LF);
            }
        } else if (nonTerm.charAt(0) == 'O') {
            if ("++".equals(currentWord.getValue()) || "--".equals(currentWord.getValue())) {
                analyseStack.add(0, new ParserNode(ParserNode.ACTION_SIGN, "SINGLE_OP", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, currentWord.getValue(), null));
            }
            // Q
        } else if (nonTerm.charAt(0) == 'Q') {
            if (Type.IDENTIFIER.equals(currentWord.getType())) {
                analyseStack.add(0, new ParserNode(ParserNode.ACTION_SIGN, "ASS_Q", null));
                analyseStack.add(1, new ParserNode(ParserNode.TERMINAL, "id", null));
                analyseStack.add(2, new ParserNode(ParserNode.TERMINAL, "O", null));
            }
        }
    }

    /**
     * 动作集操作
     */
    private static void actionSignOP() {
        if ("ADD".equals(top.getName())) {
            OP = "+";
        } else if ("SUB".equals(top.getName())) {
            OP = "-";
        } else if ("DIV".equals(top.getName())) {
            OP = "/";
        } else if ("MUL".equals(top.getName())) {
            OP = "*";
        } else if ("TRAN_LF".equals(top.getName())) {
            F.setValue(L.getValue());
        } else if ("ASS_F".equals(top.getName())) {
            F.setValue(currentWord.getValue());
            semanticStack.push(F.getValue());
        } else if ("ASS_R".equals(top.getName())) {
            R.setValue(currentWord.getValue());
            semanticStack.push(R.getValue());
        } else if ("ASS_Q".equals(top.getName())) {
            Q.setValue(currentWord.getValue());
            semanticStack.push(Q.getValue());
        } else if ("ASS_U".equals(top.getName())) {
            U.setValue(currentWord.getValue());
            semanticStack.push(U.getValue());
        } else if ("SINGLE_OP".equals(top.getName())) {
            for_op.push(currentWord.getValue());
        } else if ("COMPARE_OP".equals(top.getName())) {
            D.setValue(currentWord.getValue());
            semanticStack.push(D.getValue());
        } else if ("IF_backPatch_FJ".equals(top.getName())) {
            backPatch(if_fj.pop(), intermediateCodeCount + 2);
        } else if ("WHILE_backPatch_FJ".equals(top.getName())) {
            backPatch(while_fj.pop(), intermediateCodeCount + 1);
        } else if ("IF_backPatch_RJ".equals(top.getName())) {
            backPatch(if_rj.pop(), intermediateCodeCount + 1);
        } else if ("FOR_backPatch_FJ".equals(top.getName())) {
            backPatch(for_fj.pop(), intermediateCodeCount + 1);
        } else if ("SINGLE".equals(top.getName())) {
            if (!for_op.empty() && for_op.peek() != null) {
                if(!semanticStack.empty()) {
                    ARG1 = semanticStack.pop();
                }
                RES = ARG1;
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, for_op.pop(), ARG1, "/", RES));
                return;
            }
        } else if ("ADD_SUB".equals(top.getName())) {
            if (OP != null && (OP.equals("+") || OP.equals("-"))) {
                ARG2 = semanticStack.pop();
                ARG1 = semanticStack.pop();
                RES = newTemp();
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, ARG1, ARG2, RES));
                L.setValue(RES);
                semanticStack.push(L.getValue());
                OP = null;
                return;
            }
        } else if ("DIV_MUL".equals(top.getName())) {
            if (OP != null && (OP.equals("*") || OP.equals("/"))) {
                ARG2 = semanticStack.pop();
                ARG1 = semanticStack.pop();
                RES = newTemp();
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, ARG1, ARG2, RES));
                T.setValue(RES);
                semanticStack.push(T.getValue());
                OP = null;
                return;
            }
        } else {
            if ("EQ".equals(top.getName()) || "EQ_U".equals(top.getName())) {
                OP = "=";
                ARG1 = semanticStack.pop();
                RES = semanticStack.pop();
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, ARG1, "/", RES));
            } else if ("COMPARE".equals(top.getName())) {
                ARG2 = semanticStack.pop();
                OP = semanticStack.pop();
                if (!semanticStack.empty()) {
                    ARG1 = semanticStack.pop();
                }
                RES = newTemp();
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, ARG1, ARG2, RES));
                G.setValue(RES);
                semanticStack.push(G.getValue());
            } else if ("IF_FJ".equals(top.getName())) {
                OP = "FJ";
                ARG1 = semanticStack.pop();
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, ARG1, "/", RES));
                if_fj.push(intermediateCodeCount);
            } else if ("IF_RJ".equals(top.getName())) {
                OP = "RJ";
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, "/", "/", "/"));
                if_rj.push(intermediateCodeCount);
            } else if ("WHILE_FJ".equals(top.getName())) {
                OP = "FJ";
                ARG1 = semanticStack.pop();
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, ARG1, "/", "/"));
                while_fj.push(intermediateCodeCount);
            } else if ("WHILE_RJ".equals(top.getName())) {
                OP = "RJ";
                RES = (while_fj.peek() - 1) + "";
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, "/", "/", RES));
                for_rj.push(intermediateCodeCount);
            } else if ("FOR_FJ".equals(top.getName())) {
                OP = "FJ";
                ARG1 = semanticStack.pop();
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, ARG1, "/", "/"));
                for_fj.push(intermediateCodeCount);
            } else if ("FOR_RJ".equals(top.getName())) {
                OP = "RJ";
                RES = (for_fj.peek() - 1) + "";
                intermediateCodesList.add(new IntermediateCode(++intermediateCodeCount, OP, "/", "/", RES));
                for_rj.push(intermediateCodeCount);
            }
            OP = null;
        }
        analyseStack.remove(0);
    }

    /**
     * 终结符操作
     * param term
     */
    private static void termOP(String term) {
        if (Type.isKey(currentWord.getValue()) || Type.INT_10.equals(currentWord.getType()) || Type.CONST_STR.equals(currentWord.getType())
                || Type.isOperator(currentWord.getValue()) || Type.isSeparator(currentWord.getValue())
                || term.equals(currentWord.getValue()) || ("id".equals(term) && Type.IDENTIFIER.equals(currentWord.getType()))) {
            analyseStack.remove(0);
            wordList.remove(0);
        } else {
            doError("语法错误");
        }
    }

    private static String newTemp() {
        tempCount++;
        return "T" + tempCount;
    }

    /**
     * 拉链回填
     *
     * @param i
     * @param res
     */
    private static void backPatch(int i, int res) {
        IntermediateCode temp = intermediateCodesList.get(i - 1);
        if (temp != null) {
            temp.setResult(res + "");
        }
        intermediateCodesList.set(i - 1, temp);
    }

    /**
     * 初始化
     */
    private static void init() {
        sb = new StringBuilder();
        analyseStack = new Stack<>();
        intermediateCodeCount = 0;
        errorList = new ArrayList<>();
        tempCount = 0;
        errorCount = 0;
        graErrorFlag = false;
        intermediateCodesList = new ArrayList<>();
        semanticStack = new Stack<>();
        for_op = new Stack<>();
        analyseStack = new Stack<>();
        S = new ParserNode(ParserNode.NONE_TERMINAL, "S", null);
        A = new ParserNode(ParserNode.NONE_TERMINAL, "A", null);
        B = new ParserNode(ParserNode.NONE_TERMINAL, "B", null);
        C = new ParserNode(ParserNode.NONE_TERMINAL, "C", null);
        X = new ParserNode(ParserNode.NONE_TERMINAL, "X", null);
        Y = new ParserNode(ParserNode.NONE_TERMINAL, "Y", null);
        Z = new ParserNode(ParserNode.NONE_TERMINAL, "Z", null);
        Z1 = new ParserNode(ParserNode.NONE_TERMINAL, "Z'", null);
        U = new ParserNode(ParserNode.NONE_TERMINAL, "U", null);
        U1 = new ParserNode(ParserNode.NONE_TERMINAL, "U'", null);
        E = new ParserNode(ParserNode.NONE_TERMINAL, "E", null);
        E1 = new ParserNode(ParserNode.NONE_TERMINAL, "E'", null);
        H = new ParserNode(ParserNode.NONE_TERMINAL, "H", null);
        H1 = new ParserNode(ParserNode.NONE_TERMINAL, "H'", null);
        G = new ParserNode(ParserNode.NONE_TERMINAL, "G", null);
        F = new ParserNode(ParserNode.NONE_TERMINAL, "F", null);
        D = new ParserNode(ParserNode.NONE_TERMINAL, "D", null);
        L = new ParserNode(ParserNode.NONE_TERMINAL, "L", null);
        V = new ParserNode(ParserNode.NONE_TERMINAL, "V", null);
        L1 = new ParserNode(ParserNode.NONE_TERMINAL, "L'", null);
        T = new ParserNode(ParserNode.NONE_TERMINAL, "T", null);
        T1 = new ParserNode(ParserNode.NONE_TERMINAL, "T'", null);
        O = new ParserNode(ParserNode.NONE_TERMINAL, "O", null);
        P = new ParserNode(ParserNode.NONE_TERMINAL, "P", null);
        Q = new ParserNode(ParserNode.NONE_TERMINAL, "Q", null);
        R = new ParserNode(ParserNode.NONE_TERMINAL, "R", null);
        ADD_SUB = new ParserNode(ParserNode.ACTION_SIGN, "ADD_SUB", null);
        ADD = new ParserNode(ParserNode.ACTION_SIGN, "ADD", null);
        SUB = new ParserNode(ParserNode.ACTION_SIGN, "SUB", null);
        DIV_MUL = new ParserNode(ParserNode.ACTION_SIGN, "DIV_MUL", null);
        DIV = new ParserNode(ParserNode.ACTION_SIGN, "DIV", null);
        MUL = new ParserNode(ParserNode.ACTION_SIGN, "MUL", null);
        ASS_F = new ParserNode(ParserNode.ACTION_SIGN, "ASS_F", null);
        ASS_R = new ParserNode(ParserNode.ACTION_SIGN, "ASS_R", null);
        ASS_Q = new ParserNode(ParserNode.ACTION_SIGN, "ASS_Q", null);
        ASS_U = new ParserNode(ParserNode.ACTION_SIGN, "ASS_U", null);
        TRAN_LF = new ParserNode(ParserNode.ACTION_SIGN, "TRAN_LF", null);
        SINGLE = new ParserNode(ParserNode.ACTION_SIGN, "SINGLE", null);
        SINGLE_OP = new ParserNode(ParserNode.ACTION_SIGN, "SINGLE_OP", null);
        EQ = new ParserNode(ParserNode.ACTION_SIGN, "EQ", null);
        EQ_U1 = new ParserNode(ParserNode.ACTION_SIGN, "EQ_U", null);
        COMPARE = new ParserNode(ParserNode.ACTION_SIGN, "COMPARE", null);
        COMPARE_OP = new ParserNode(ParserNode.ACTION_SIGN, "COMPARE_OP", null);
        IF_FJ = new ParserNode(ParserNode.ACTION_SIGN, "IF_FJ", null);
        IF_RJ = new ParserNode(ParserNode.ACTION_SIGN, "IF_RJ", null);
        IF_backPatch_FJ = new ParserNode(ParserNode.ACTION_SIGN, "IF_backPatch_FJ", null);
        IF_backPatch_RJ = new ParserNode(ParserNode.ACTION_SIGN, "IF_backPatch_RJ", null);
        WHILE_FJ = new ParserNode(ParserNode.ACTION_SIGN, "WHILE_FJ", null);
        WHILE_RJ = new ParserNode(ParserNode.ACTION_SIGN, "WHILE_RJ", null);
        WHILE_backPatch_FJ = new ParserNode(ParserNode.ACTION_SIGN, "WHILE_backPatch_FJ", null);
        FOR_FJ = new ParserNode(ParserNode.ACTION_SIGN, "FOR_FJ", null);
        FOR_RJ = new ParserNode(ParserNode.ACTION_SIGN, "FOR_RJ", null);
        FOR_backPatch_FJ = new ParserNode(ParserNode.ACTION_SIGN, "FOR_backPatch_FJ", null);
        if_fj = new Stack<>();
        if_rj = new Stack<>();
        while_fj = new Stack<>();
        while_rj = new Stack<>();
        for_fj = new Stack<>();
        for_rj = new Stack<>();
    }
}