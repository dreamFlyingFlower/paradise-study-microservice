# AuthorizationServer2



# 常见问题



1. 访问授权申请(/oauth2/authorize)接口跳转到默认登录页面,登录成功后响应错误码999

```json
{"timestamp":"2023-06-24 01:08:42","status":999,"error":"None"}
```

* 可能造成该问题的原因:
  * 登录页面的某些静态资源被拦截了,在资源服务器中放行登录页面的所有静态资源
  * 未放行路径`/error`,当登录页面的某些静态资源不存在导致404时会跳转到/error处理,未放行该路径会导致请求重定向至登录页面
* 以上问题排查建议:

> 浏览器打开登录页面([http://127.0.0.1:8080/login),](https://link.juejin.cn?target=http%3A%2F%2F127.0.0.1%3A8080%2Flogin)%EF%BC%8C) 然后按F12,看一下控制台中有哪些请求是302并且被重定向至登录页面的

2. 访问授权申请(/oauth2/authorize)接口跳转到默认登录页面,登录成功后跳转回来时授权申请(/oauth2/authorize)接口响应400错误

   * 检查数据库中是否存在授权申请使用的客户端信息
   * 可能有些是存入redis中的,所以根据客户端存入位置去对应的库检查客户端信息

3. 访问授权申请(/oauth2/authorize)接口跳转到默认登录页面,登录成功后跳转回来时授权申请(/oauth2/authorize)接口响应404错误

   * 在添加认证服务配置与资源服务配置时两个过滤器链不要添加`Order`注解,以防认证服务配置被覆盖
   * 如果有网关代理,认证服务配置中的签发地址(issue)中需要添加网关的代理路径

4. 在PKCE流程中通过token(/oauth2/token)接口获取token时,响应 invalid_grant,可能造成该问题的原因

   * 授权码错误
   * 客户端id错误
   * 回调地址错误(跟请求/oauth2/authorize时携带的不一致)
   * 授权码过期
   * 生成code_challenge的算法有问题

5. 在OAuth2流程中通过token(/oauth2/token)接口获取token时,响应 invalid_client,可能造成该问题的原因

   * 客户端id错误

6. client 授权登录后,如何退出

   * Spring Security 提供了退出的端点:`/logout`

7. 直接配置 @PreAuthorize注解不生效

   * 检查是否添加以下两个注解:`@EnableWebSecurity`,`@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)`

8. 客户端对接认证服务时出现[authorization_request_not_found]异常

   * 认证服务器和客户端在同一个机器上时不能使用同一个ip,例如127.0.0.1,在存储cookie时不会区分端口的,比如127.0.0.1:8000和127.0.0.1:8080这两个,他们的cookie是同一个的,后者会覆盖前者
   * 如果配置认证服务的地址是127.0.0.1:8080然后通过127.0.0.1:8000去访问客户端则会在登录后出现`[authorization_request_not_found]`异常,**详见[spring-security issues 5946](https://link.juejin.cn/?target=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-security%2Fissues%2F5946)**
   * 如果使用的是域名,可以解析两个子域名,一个解析到认证服务,一个解析到客户端服务

9. 如果不用Web页面登录,有个接口,然后用安卓界面登录的处理方式

   * 移动app和pc的app用的比较多的是PKCE模式,如果不想跳转到web登录页面就用自定义grant_type的方式添加一种认证并获取token的grant

10. OIDC中的idToken作用

    * idToken中包含了用户信息,解析后可以直接获取用户信息,不用再请求服务器了

11. 用自定义的UserDetailsService登录时出现序列化问题

    * 异常堆栈描述

    > java.lang.IllegalArgumentException: The class with com.wy.entity.OAuth2UserEntity and name of com.wy.entity.OAuth2UserEntity is not in the allowlist. If you believe this class is safe to deserialize, please provide an explicit mapping using Jackson annotations or by providing a Mixin. If the serialization is only done by a trusted source, you can also enable default typing. See [github.com](https://link.juejin.cn/?target=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-security%2Fissues%2F4370) for details

    * 解决方案:
      * 实体类添加两个注解:`@JsonSerialize(JsonMixin)`和`@JsonIgnoreProperties(ignoreUnknown = true)`

12. 使用Oracle数据库在授权申请时会抛出异常堆栈

    * 框架问题,使用Oracle时确实会出现这种问题,如果需要解决可能需要重写AuthorizationService,详见[issues 428](https://link.juejin.cn?target=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-authorization-server%2Fissues%2F428)

13. 自定义access_token以后随着权限的增多,Jwt格式的access_token越来越长

    * 使用匿名token(opaque token),匿名token的长度都是固定的
    * 可以自己在响应access_token时包装一层加密算法,减小token的长度
    * 如果生成/响应token时有自定义操作,则解析时做好对应处理

14. 客户端登录时跳转到客户端的登录页面并提示错误信息`Bad Crendentials`

    * 使用的客户端id或secret错误

    * 获取用户信息失败

    * yml配置中用户名字段配置错误(用户信息接口响应的数据中没有该字段)

      ```yaml
      spring:
        security:
          oauth2:
            client:
              # oauth登录提供商
              provider:
                # 这里是oauth2登录提供商,自定义的,但引用时要对应,针对oauth2登录的特殊配置,指定使用该客户端时的授权申请、用户信息地址等
                github:
                  # 这里就是上边说的用户名字段,详见下方说明1
                  user-name-attribute: login
      ```

    * 如果配置在这里的值在用户信息接口的响应数据中不存在则也会提示 Bad Crendentials,这里之所以需要配置这个字段是因为使用oauth2登录后,客户端会将用access_token获取的用户信息以map的方式存储,Principal对象调用getName时是以该配置当做key来获取用户名的,并校验是否为空,为空则抛出异常,所以说该配置必须在用户信息响应数据中存在

15. PKCE常用在什么场景下

    * [PKCE](https://link.juejin.cn?target=https%3A%2F%2Fwww.rfc-editor.org%2Frfc%2Frfc7636.html):`Proof Key for Code Exchange`,是一种用于增强授权码模式安全性的方法,它可以防止恶意应用程序通过截获授权码和重定向URI来获得访问令牌
    * PKCE通过将随机字符串(code_verifier)和其SHA-256(code_challenge)与授权请求一起发送,确保访问令牌只能被有相应code_verifier的应用程序使用,保障用户的安全性
    * OAuth 2.0 协议扩展 PKCE 扩展协议:为了解决公开客户端的授权安全问题
      * 面向对象:public客户端,其本身没有能力保存密钥信息(恶意攻击者可以通过反编译等手段查看到客户端的密钥client_secret,也就可以通过授权码code换取access_token,到这一步,恶意应用就可以拿着 token 请求资源服务器了)
      * 原理:PKCE协议本身是对 OAuth 2.0 [授权码模式](https://link.juejin.cn?target=https%3A%2F%2Fdatatracker.ietf.org%2Fdoc%2Fhtml%2Fdraft-ietf-oauth-v2-1-07%23name-authorization-code-grant) 的扩展,它和之前的授权码流程大体上是一致的, 区别在于在向授权服务器的authorize endpoint请求时,需要额外的code_challenge和code_challenge_method参数;向tokenendpoint请求时,需要额外的code_verifier参数;最后授权服务器会对这三个参数进行对比验证,通过后颁发令牌
      * 官网[How-to: Authenticate using a Single Page Application with PKCE](https://link.juejin.cn?target=https%3A%2F%2Fdocs.spring.io%2Fspring-authorization-server%2Freference%2Fguides%2Fhow-to-pkce.html)一文中说明了单页面应用如何使用PKCE模式获取认证,从这里可以看出PKCE模式适用于安全系数不高的客户端中,因为单页面应用在浏览器运行时,用户打开F12可以直接看到源码,从而获取客户端id与秘钥,桌面应用同理,反编译之后就能拿到,所以推出了“通过将随机字符串(code_verifier)和其SHA-256哈希值(code_challenge)与授权请求一起发送”的方案,因为code_verifier是随机生成的,攻击者无法提前知道code_verifier值,也就无法通过后端的校验

16. oidc与传统的oauth有啥区别

    * [www.zhihu.com/question/59…](https://link.juejin.cn?target=https%3A%2F%2Fwww.zhihu.com%2Fquestion%2F59673793%2Fanswer%2F2430690659)

17. 前后端分离中,实现AuthenticationSuccessHandler和AuthenticationFailureHandler返回json的作用

    * 在前后端分离的授权码模式中前端与认证服务的交互都应该以json的形式交互,不应该重定向,
    * 前端收到登录成功/失败响应后作出对应的处理,成功就获取地址栏参数target(认证服务跳转登录之前处理,获取当前url并拼接至登录页面地址)并进行跳转(由前端跳转),失败就弹框处理

18. 如果项目中禁用session,前后端分离的nonceId不用从session获取那改需要怎么处理

    * 如果项目中禁用session,则需要由前端生成一个uuid当做nonceId的值,然后前端在发起授权申请(/oauth2/authorize),重定向到登录页面,提交登录,授权确认时都要携带这个nonceId
    * 前端在与认证服务交互时,只要是需要认证信息就需要带着这个参数.与OAuth2的交互完成后就不需要了,因为后续流程有access_token了

19. 公司内部有多个业务系统,认证服务器是否需要为都创建一个client

    * 最好是提供一个管理平台,可以动态管理客户端

20. 在前后端分离中,通过code换取token的时候需要传递客户端的id和密钥,那客户端密钥不是相当于裸奔了吗

    * 使用PKCE模式

21. 使用Authorization Server获取到的token和SSO的区别,一个token是否可以在多个业务系统中使用

    * SSO是在多个应用系统中,用户只需要登录一次就可以访问所有相互信任的应用系统
    * 相对于后端来说,只要是认证服务的资源服务器则获取一次access_token以后就都可以使用access_token来访问
    * 对于前端来说,如果有两个域名不一样的子业务系统,那么在浏览器中它们之间无法共享token,需要走一遍oauth2的登录流程来获取access_token,但是对于浏览器来说认证服务的域名一致没有变,相对应的它们之间的session也没有变化,所以说当子业务系统走oauth2流程时也是不用登录就能获取到access_token的,这样也是符合SSO的标准的

22. client的scope

    * scope是用来约束客户端的权限的,跟用户权限(authorities)是不同的
    * 资源服务器(Resource Server)默认情况下解析access_token后里边只有客户端的scope而没有用户的权限
    * 为简化开发步骤,直接使用登录用户的权限来替换客户端的scope,或直接忽略客户端的scope,这样在鉴权时可以直接对登录用户鉴权,简化逻辑

23. `/login/oauth2/code/gitee`

    * OAuth2AuthorizationCodeGrantFilter拦截`/login/oauth2/code/{registrationId}`,根据registrationId获取客户端信息,之后获取请求中携带的授权码,使用授权码获取token,再使用token获取用户信息,至此OAuth2的流程也结束了,之后就会跳转到未登录之前的地址

    * 客户端根据授权码获取认证信息这些都是默认提供的,但是在yml中的配置一定要对应上

      ```yaml
      spring:
        security:
          oauth2:
            client:
              registration:
                # 这个'gitee'就是registrationId
                gitee:
                  # 指定oauth登录提供者,该oauth登录由provider中的gitee来处理
                  provider: gitee
                  # 回调地址
                  redirect-uri: ${custom.security.issuer-url}/login/oauth2/code/gitee
                  # 申请scope列表
                  scope:
                    - emails
                    - projects
      ```

