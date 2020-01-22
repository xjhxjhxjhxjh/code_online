package com.xjh.targetCode;

import com.xjh.parser.pojo.IntermediateCode;
import com.xjh.targetCode.pojo.TargetCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xjhxjhxjh
 * @date 2019/12/24 10:38
 */
public class GetTargetCode {
    /**
     * 寄存器个数
     */
    private final static int REGISTER_NUMBER = 2;
    private static int top;
    private static IntermediateCode[] data;
    private static String ans;

    /**
     * 目标代码生成
     * @param intermediateCodesList
     */
    public static List<TargetCode> doTargetCode(List<IntermediateCode> intermediateCodesList) {
        int cnt = 1;
        top = 0;
        ans = "";
        data = new IntermediateCode[intermediateCodesList.size()];
        data = intermediateCodesList.toArray(data);
        List<TargetCode> result = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            int k = get(data[i].getArg1());
            if (k == -1) {
                k = findx(i);
                if (ans.length() > i && ans.charAt(k) != '\0' && use(i, ans.charAt(k) + "") < data.length) {
                    result.add(new TargetCode(cnt++, "ST", "R" + k, ans.charAt(k) + ""));
                }
                result.add(new TargetCode(cnt++, "LD", "R" + k, data[i].getArg1()));
            }
            TargetCode targetCode = new TargetCode();
            System.out.println(data[i].getOp());
            if ("\\".equals(data[i].getOp())) {
                targetCode.setOp("DIV");
            } else if ("*".equals(data[i].getOp())) {
                targetCode.setOp("MUL");
            } else if ("+".equals(data[i].getOp())) {
                targetCode.setOp("ADD");
            } else if ("-".equals(data[i].getOp())) {
                targetCode.setOp("SUB");
            } else if ("FJ".equals(data[i].getOp())) {
                targetCode.setOp("JZ");
            } else if ("RJ".equals(data[i].getOp())) {
                targetCode.setOp("JMP");
            } else if ("<".equals(data[i].getOp())) {
                targetCode.setOp("JL");
            } else if (">".equals(data[i].getOp())) {
                targetCode.setOp("JG");
            } else if ("==".equals(data[i].getOp())) {
                targetCode.setOp("EQ");
            } else if ("!=".equals(data[i].getOp())) {
                targetCode.setOp("NEQ");
            }
            targetCode.setCnt(cnt++);
            targetCode.setRegisterId("R" + k);
            targetCode.setData(get(data[i].getArg2()) == -1 ?
                    data[i].getArg2() : "R" + get(data[i].getArg2()));
            if (targetCode.getOp() != null) {
                result.add(targetCode);
            }
            StringBuilder sb = new StringBuilder(ans);
            ans = sb.replace(k, k + 1, data[i].getResult()).toString();
        }
        return result;
    }

    private static int get(String c) {
        for (int i = 0; i < REGISTER_NUMBER; i++) {
            if (ans.length() > i && c.equals(ans.charAt(i) + "")) {
                return i;
            }
        }
        return -1;
    }

    private static int use(int x, String c) {
        for (int i = x; i < data.length; i++) {
            if (data[i].getArg1().equals(c) || data[i].getArg2().equals(c)) {
                return i;
            }
        }
        return data.length;
    }

    private static int findx(int x) {
        if (top < REGISTER_NUMBER) {
            return top++;
        }
        int flag = -1, t = -1;
        for (int i = 0; i < REGISTER_NUMBER; i++) {
            int k = use(x, ans.charAt(i) + "");
            if (k > flag) {
                flag = k;
                t = i;
            }
        }
        return t;
    }
}
