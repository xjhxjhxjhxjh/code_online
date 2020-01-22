package com.xjh.targetCode;

import com.xjh.parser.pojo.IntermediateCode;
import com.xjh.targetCode.pojo.TargetCode;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author xjhxjhxjh
 * @date 2019/12/24 14:33
 */

public class GetTargetCodeTest {

    @Test
    public void test1() {
        List<TargetCode> targetCodes = GetTargetCode.doTargetCode(Arrays.asList(new IntermediateCode(1, "-", "A", "B", "T"),
                new IntermediateCode(2, "-", "A", "C", "U"),
                new IntermediateCode(3, "+", "T", "U", "V"),
                new IntermediateCode(4, "+", "V", "U", "W")
        ));
        targetCodes.forEach(System.out::println);
    }
}
