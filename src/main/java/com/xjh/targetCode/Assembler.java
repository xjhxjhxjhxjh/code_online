package com.xjh.targetCode;

import com.xjh.parser.pojo.IntermediateCode;
import com.xjh.targetCode.pojo.TargetCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xjhxjhxjh
 * @date 2019/12/25 14:06
 */
public class Assembler {

    public static List<TargetCode> doAssembler(List<IntermediateCode> intermediateCodeList) {
        int cnt = 1;
        List<TargetCode> result = new ArrayList<>();
        for (IntermediateCode intermediateCode : intermediateCodeList) {
            if ("=".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "MOV", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("++".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "INC", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("+".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "ADD", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("-".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "SUB", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("*".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "MUL", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("/".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "DIV", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("RJ".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "JMP", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("FJ".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "JZ", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if (">".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "JG", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("<".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "JL", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("==".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "EQ", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("!=".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "NEQ", intermediateCode.getResult(), intermediateCode.getArg1()));
            } else if ("--".equals(intermediateCode.getOp())) {
                result.add(new TargetCode(cnt++, "NEC", intermediateCode.getResult(), intermediateCode.getArg1()));
            }
        }
        return result;
    }
}
