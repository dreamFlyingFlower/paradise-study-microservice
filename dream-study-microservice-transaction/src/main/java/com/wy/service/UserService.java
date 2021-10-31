package com.wy.service;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * 使用Seata的TCC模式进行分布式事务
 * 
 * {@link LocalTCC}:指定当前接口事务使用TCC模式
 * {@link TwoPhaseBusinessAction}:指定TCC事务的调用的微服务名,提交的业务逻辑,回滚的业务逻辑
 * 
 * @author 飞花梦影
 * @date 2021-10-30 16:18:50
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@LocalTCC
public interface UserService {

	@TwoPhaseBusinessAction(name = "testUser", commitMethod = "testCommit", rollbackMethod = "testCancel")
	String testUser();

	boolean testCommit(BusinessActionContext businessActionContext);

	boolean testCancel(BusinessActionContext businessActionContext);
}