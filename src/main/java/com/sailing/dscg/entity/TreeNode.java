package com.sailing.dscg.entity;

import lombok.Data;

import java.util.List;

/**
 * @Description: 树形结构
 * @Auther:史俊华
 * @Date:2018/7/700
 */
@Data
public class TreeNode {
    /**唯一key*/
    private String key;
    /**值*/
    private String value;
    /**名称*/
    private String label;
    private List<TreeNode> children;
}
