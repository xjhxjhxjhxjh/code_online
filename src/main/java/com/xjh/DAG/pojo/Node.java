package com.xjh.DAG.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xjhxjhxjh
 * @date 2019/12/23 16:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private int left = -1;
    private int right = -1;
    private String id;
    private StringBuilder var = new StringBuilder();
}
