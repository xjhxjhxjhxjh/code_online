package com.xjh.DAG;

import com.xjh.parser.pojo.IntermediateCode;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author xjhxjhxjh
 * @date 2019/12/24 15:15
 */
public class DAGTest {
    @Test
    public void test1(){
        List<IntermediateCode> a = Arrays.asList(new IntermediateCode(1, "=", "/", "1", "a"),
                new IntermediateCode(2, "=", "/", "2", "a"),
                new IntermediateCode(3, "=", "/", "3", "a"));
        System.out.println(DAG.doDAG(a));
    }
}
