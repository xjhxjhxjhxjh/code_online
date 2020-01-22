package com.xjh.targetCode.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xjhxjhxjh
 * @date 2019/12/24 10:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetCode {
    private int cnt;
    private String op;
    private String registerId;
    private String data;
}
