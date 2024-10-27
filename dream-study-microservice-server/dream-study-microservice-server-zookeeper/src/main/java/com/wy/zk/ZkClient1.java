package com.wy.zk;

/**
 * LeaderSelector是选举的核心类
 * 
 * @author 飞花梦影
 * @date 2019-04-25 17:10:06
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class ZkClient1 {

	public static void main(String[] args) throws Exception {
		new ZkWatcher();
		Thread.sleep(100000000);
	}
}