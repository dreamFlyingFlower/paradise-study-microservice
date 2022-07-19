package com.wy.zk.curator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;

/**
 * ZK利用临时节点创建分布式锁,业务超时需要设置超时时间
 *
 * @author 飞花梦影
 * @date 2021-12-24 17:32:35
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class ZkLock {

	// 计数器
	protected static CountDownLatch countDownLatch = null;

	// 创建zkClient连接
	protected CuratorFramework client =
			CuratorFrameworkFactory.newClient("192.168.1.106:2181", 5000, 5000, new RetryForever(5000));

	protected String lockPath = "/lockPath";

	public void getLock() throws Exception {
		if (tryLock()) {
			System.out.println(Thread.currentThread().getName() + ">>>获取锁信息成功<<<<");
		} else {
			// 等待
			waitLock();
			// 重新获取锁
			getLock();
		}
	}

	/**
	 * 获取锁
	 * 
	 * @param lockPath
	 * @throws Exception
	 */
	public void getLock(String lockPath) throws Exception {
		InterProcessMutex interProcessMutex = new InterProcessMutex(client, lockPath);
		// 获取锁
		interProcessMutex.acquire();
		boolean acquire = interProcessMutex.acquire(0, TimeUnit.SECONDS);
		System.out.println(acquire);
		// 释放锁
		interProcessMutex.release();
	}

	// 尝试获取锁
	protected boolean tryLock() {
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(lockPath,
					lockPath.getBytes());
			// 获取锁成功
			return true;
		} catch (Exception e) {
			// 获取锁失败
			// e.printStackTrace();
			return false;
		}
	}

	// 当前线程等待
	protected boolean waitLock() throws Exception {
		// 初始化zk的watcher,监听节点是否被删除
		TreeCache tc = new TreeCache(client, lockPath);
		// 启动cache
		tc.start();
		// 添加监听事件
		tc.getListenable().addListener(new TreeCacheListener() {

			@Override
			public void childEvent(CuratorFramework cf, TreeCacheEvent event) throws Exception {
				// 事件处理
				// 初始化事件
				if (event.getType() == Type.INITIALIZED) {

				}
				// 添加节点事件
				if (event.getType() == Type.NODE_ADDED) {

				}
				// 删除节点
				if (event.getType() == Type.NODE_REMOVED) {
					if (countDownLatch != null) {
						countDownLatch.countDown();// 重新进入获取锁
					}
				}
				// 更新节点
				if (event.getType() == Type.NODE_UPDATED) {

				}
			}
		});

		// 判断当前节点是否存在,如果存在就等待
		if (client.checkExists().forPath(lockPath) != null) {
			if (countDownLatch == null) {
				countDownLatch = new CountDownLatch(1);
			}
			// 等待
			countDownLatch.await();
		}
		// 正常执行业务逻辑 重新获取锁

		tc.close();
		return false;
	}

	public void unLock() {
		if (client != null) {
			System.out.println(Thread.currentThread().getName() + ">>>关闭锁成功<<<<");
			client.close();
		}
	}
}