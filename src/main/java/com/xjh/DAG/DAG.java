package com.xjh.DAG;

import com.xjh.DAG.pojo.Node;
import com.xjh.parser.pojo.IntermediateCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAG优化
 *
 * @author xjhxjhxjh
 * @date 2019/12/23 15:03
 */
public class DAG {
    /**
     * 四元式
     */
    private static IntermediateCode[] data;
    /**
     * 四元式个数
     */
    private static int size;
    /**
     * 节点个数
     */
    private static int count;

    /**
     * 当前四元式
     */
    private static IntermediateCode curr;
    /**
     * 优化结果
     */
    private static List<IntermediateCode> result;
    private static Node[] ans;
    private static Boolean[] flag;

    /**
     * DAG优化
     * @param intermediateCodesList
     * @return
     */
    public static List<IntermediateCode> doDAG(List<IntermediateCode> intermediateCodesList) {
        data = new IntermediateCode[intermediateCodesList.size()];
        data = intermediateCodesList.toArray(data);
        size = data.length;
        init();
        for (int i = 0; i < size; i++) {
            addOp(data[i].getResult(), data[i].getOp(), addNode(data[i].getArg1()), addNode(data[i].getArg2()));
        }
        for (int i = 0; i < count; i++) {
            if (ans[i].getLeft() != -1) {
                IntermediateCode intermediateCode = new IntermediateCode();
                char[] chars = ans[i].getVar().toString().toCharArray();
                intermediateCode.setResult(chars[0] + "");
                intermediateCode.setOp(ans[i].getId());
                Node l = ans[ans[i].getLeft()];
                Node r = ans[ans[i].getRight()];
                char[] charsL = l.getVar().toString().toCharArray();
                char[] charsR = r.getVar().toString().toCharArray();
                intermediateCode.setArg1(l.getVar().length() > 0 ? charsL + "" : l.getId());
                intermediateCode.setArg2(r.getVar().length() > 0 ? charsR + "" : r.getId());
                result.add(intermediateCode);
            }
        }
        Set<String> op = new HashSet<>();
        for (int i = 0; i < data.length; i++) {
            op.add(data[i].getResult());
        }
        String[] ops = new String[op.size()];
        ops = op.toArray(ops);
        for (int i = 0; i < ops.length; i++) {
            for (int j = result.size() - 1; j > 0; j--) {
                if (result.get(j).getResult().equals(ops[i])) {
                    dfs(j);
                    break;
                }
            }
        }
        int cnt = 0;
        List<IntermediateCode> finalResult = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (flag[i] != null && flag[i]) {
                IntermediateCode intermediateCode = result.get(i);
                intermediateCode.setId(cnt++);
                finalResult.add(intermediateCode);
            }
        }
        System.out.println("DAG:");
        System.out.println(result.get(2));
        return finalResult;
    }

    /**
     * 初始化
     */
    private static void init() {
        count = 0;
        result = new ArrayList<>(size);
        ans = new Node[2 * size + 1];
        for (int i = 0; i < 2 * size + 1; i++) {
            ans[i] = new Node();
        }
        flag = new Boolean[2 * size + 1];
    }

    /**
     * 查找
     * @param i
     * @param c
     * @return
     */
    private static Boolean findVar(int i, String c) {
        int length = ans[i].getVar().length();
        for (int j = 0; j < length; j++) {
            if (c.equals(ans[i].getVar().charAt(j) + "")) {
                return true;
            }
        }
        return false;
    }

    private static int addNode(String c) {
        for (int i = count - 1; i >= 0; i--) {
            if (ans[i].getId().equals(c) || findVar(i, c)) {
                return i;
            }
        }
        ans[count].setId(c + "");
        return count++;
    }

    private static void addOp(String c, String op, int left, int right) {
        for (int i = count - 1; i >= 0; i--) {
            if (ans[i].getId().equals(op) && ans[i].getRight() == right && ans[i].getLeft() == left) {
                ans[i].getVar().append(c);
                return;
            }
        }
        ans[count].setId(op);
        ans[count].setLeft(left);
        ans[count].setRight(right);
        ans[count].getVar().append(c);
        count++;
    }

    private static void dfs(int x) {
        if (ans[x].getLeft() != -1) {
            flag[x] = true;
            dfs(ans[x].getLeft());
            dfs(ans[x].getRight());
        }
    }
}