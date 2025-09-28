# BaseFrame2

基于SpringBoot2.6 + Shiro 搭建的一个基本项目框架，旨在减少项目起步时搭建框架消耗的时间

## 一些注解的使用

1. `@AutoLog`：自动记录日志注解，标注在Controller层的方法上，使用该注解会自动记录用户操作日志，并将日志存入数据库。
2. `@AllowAnonymous`：允许匿名访问注解，标注在Controller层的类或方法上，使用该注解会允许匿名用户访问该接口/该类下的所有接口。
3. `@Disabled`
   ：禁用接口注解，标注在Controller层的类或方法上，使用该注解会禁用该接口/该类下的所有接口,返回404错误,且会在swagger中隐藏接口,默认在`spring.profiles.active=dev`
   模式下<span style="color:red;font-weight:bold">
   失效</span>,可在`work.chncyl.base.global.security.JwtFilter#isAccessAllowed`中修改。
4. `@CurrentUser`：获取当前登录用户信息注解，标注在Controller层
   `JwtClaimDto`或`LoginedUserInfo`
   参数上，使用该注解会自动注入当前登录用户的JwtClaimDto或LoginedUserInfo对象，且不会在Swagger接口文档中显示该参数。（也可以使用`SessionUtil.getSession()`获取当前登录用户信息）

## 登录接口

1. 登录接口：/login，配合/Captcha验证码接口实现登录功能。
    1. 验证码接口：/Captcha，生成验证码图片，并返回验证码图片的字符数组。
2. 登录接口：/siginSkip，用于快速登录，无需输入验证码。
3. 登录接口（仅限测试使用）：/simpleSignAndLogin，更简单的登录接口，仅手机号即可登录，相同手机号会登录同一账号（仅限测试使用，或引入短信验证码）。

This project is maintained by [GitHub](https://github.com/wx6523610521).
