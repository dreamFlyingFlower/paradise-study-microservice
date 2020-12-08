# Gateway
# Gateway

# Zuul
	由于zuul在spring2.0的时候没有发布新的版本,所以spring自己开发了一个路由网关,就是gateway
## 简单使用
	引用zuul的包,直接在启动类上添加@EnableZuulProxy注解,不需要其他任何配置,可以在原来的api接口上添加接口所在的服务前缀,之后即可访问原api,如原api为user/getById,服务为user

>
	直接由原微服务访问为userip:userport/user/getById
	zuul路由方法:zuulip:zuulport/user/user/getById

## 自定义路由
	可直接在application中查看注释