package com.wy.base;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 树形结构数据,若需要额外的参数,可继承本类
 * 
 * @author ParadiseWY
 * @date 2020年6月16日 上午11:16:40
 */
@Getter
@Setter
@ToString
public class Tree<T> extends AbstractModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 树形结构唯一标识符
	 */
	private String treeId;

	/**
	 * 树形结构需要展示的名称
	 */
	private String treeName;

	/**
	 * 树形结构的Code
	 */
	private String treeCode;

	/**
	 * 树形结构本层数据的下级数据个数
	 */
	private Long childNum;

	/**
	 * 下层数据列表
	 */
	private List<T> children;
}