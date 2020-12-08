# Security



# Tomcat

* 1.TomcatServletWebServerFactory#configureSession方法会指定session的超时时间,最少不能低于1min



# Social

* 1.OAuth2的授权模式下,需要先从授权服务器获得授权码code,发放code之前需要先把用户引到到授权页面,若没有登录,用户需要先登录,登录之后再授权
* 2.用户同意授权后,程序会再次往授权服务器发送请求获得accesstoken,而OAuth2Template类的createRestTemplate方法只会处理3种请求头,有可能授权服务器给的请求头不符合这情况,此时会报错,跳到默认的错误页SocialAuthenticationFilter#DEFAULT_FAILURE_URL.该情况下需要重写createRestTemplate方法,见QqTemplate



# OAuth2

* 1.OAuth2官网->OAuth2.0 Framework->Table of Contents->4.Obtaining Authorization可查看OAuth2相关信息
* 2.OAuth2启动时会打印相关可访问url日志,需要开启日志打印级别为trace,同时需要指定打印日志级别的包

>
	logging.level.org.springframework.security.oauth2=trace



## 认证以及资源服务器

* 1.直接类上添加@Configuration和@EnableAuthorizationServer,加入spring上下文即可,同时会将OAuth2的4种认证模式都会实现
* 2.直接类上添加@Configuration和@EnableResourceServer,加入spring上下文即可实现OAuth2资源服务器,认证服务器的注解和资源服务器的资源可以放在一个类上

>
	当服务被当做资源服务器时,/oauth/authorize接口将不能再被调用,此时除了/oauth/token请求之外的所有请求都必须在请求头的Authentication中带上token,且Authentication的模式为bearer

* 3.若access_token没有失效,多次登录的access_token将会是一样的,只是失效时间在减少



## 授权码模式流程

* 第三方登录固定流程,该方式为原始方式,使用Social更快捷

* /oauth/authorize:获得授权码code,接口地址固定,不可配置,get或post请求都可
	
>
	源码:AuthorizationEndpoint

>
	response_type:4种认证模式中的一种,code是授权码认证模式,固定写死,可在OAuth2官网查看
	client_id:进行授权时由服务器给客户端的标识,若不配置,在控制台会自动生成一个随机uuid
	redirect_uri:客户端验证通过之后取得授权码code的页面跳转地址,系统内置了http://example.com,也可以自定义,都必须在配置文件中进行配置security.oauth2.client.registered-redirect-uri
	scope:取得什么权限,可自定义

>
	向认证服务器发送请求时,会检查第三方需要使用的用户是否登录,若自定义了登录方式,那么程序会报错.
	若没有自定义登录方式,则会跳出登录框,用户登录之后接口继续执行,若成功,会在当前页面让用户选择是否同意授权,不管用户是否同意,都会跳转到redirect_uri指向的url,若同意则授权码code将会直接拼在url后面.若不同意,不会带上code

* /oauth/token:获得access_token,地址不可变,post

>
	源码:TokenEndPoint.该接口不用在本地系统中登录,可直接调用,但是需要用到上一步取得的code,code有时效

>
	发送请求时,请求头中需添加验证:Authentication->Basic Auth->oauth2服务器发放的client_id和client_secret
	其他参数:
	grant_type:授权模式下固定为authorization_code
	code:1中最后跳转网页得到的code
	client_id:同/oauth/authorize中的参数
	redirect_uri:同/oauth/authorize中的参数
	scope:同/oauth/authorize中的参数

>
	成功结果集中参数:access_token,token_type,refresh_token,expires_in,scope



## 用户名密码模式

* 第三方登录固定流程,该方式为原始方式,使用Social更快捷

	该模式下用户在授权资源服务器的信息都将被第三方所知晓,因为要传递用户名和密码,最好是第三方自己的前端和本公司其他服务之间调用才采用该模式

* 1./oauth/token:获得access_token,地址不可变,post

>
	和授权码模式不同的是,可以直接跳到第2步,且不用在授权资源服务器登录,调用结果仍然能拿到access_token

>
	发送请求时,请求头中需添加验证:Authentication->Basic Auth->oauth2服务器发放的client_id和client_secret
	其他参数:
	grant_type:用户名密码模式下固定为password
	username:用户的用户名,非第三方服务的client_id
	password:用户密码,非第三方服务的client_secret
	scope:请求需要的权限

>
	成功结果集中参数:access_token,token_type,refresh_token,expires_in,scope



# JWT