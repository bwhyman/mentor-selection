# mentor-selection
### 主要功能
#### 基础功能及规则
所有角色用户登录操作  
学生默认账号密码均为学号  
为保证安全，登录时检测到账号密码相同会要求修改密码，可忽略  
点击左上角个人姓名，退出/修改密码  
用户身份角色加密，无法篡改；基于解密的身份完成用户独有操作    
密码加密保存请放心  
未登录跳转登录页面，不再单写角色加载，全局守卫控制角色下路由路径  

#### 学生
未开始，弹出开始时间说明，且无法加载教师列表，更无法选择教师  
开始后未选择，加载教师列表，数量已满教师无法选择操作；后端保证并发操作的数据完整性；
选择请求逻辑，处理，学生不存在/教师不存在/教师数量已满(独占)/重复选择/开始前提交选择等逻辑判断

开始后已选择，选择后不可更改；不再加载教师列表，确保无法再次选择，同时避免学生反复查看  
后端对选择请求验证，拒绝已选学生再次发出的选择请求  

#### 教师
查看已选自己学生；查看所有未选择学生；导出学生指导教师excel表格

#### 管理员
独立角色，无教师操作权限  
设置开始时间；导入学生名单；添加教师(姓名/账号/数量)；重置学生/教师密码  

### 技术栈
预期技术栈，
vue/element-plus/mock/springboot-reactive/ratelimiter/spring-data-redis-reactive，3容器nginx/openjdk/redis的持续部署  
实际技术栈，
vue/element-plus/springboot/spring-data-jdbc/mysql，3容器nginx/openjdk/mysql，无持续部署  
基于reactive重构技术栈，vue/element-plus/spring-webflux/spring-data-r2dbc/mysql，3容器  

### Others
算写了2.5天  
逻辑/请求/数据查询等有很大优化空间  
前端页面设计比后端麻烦耗时的多  
spring-data-jdbc很方便，简单的项目可替代mybatis  
本来想mysql数据库直接用原来的，但声明为公网IP不知云厂商是否能感知并实现内部路由，怕影响性能还是单启个容器  
基于DDD的repository不会设计啊，怎么确定业务边界？那么多的概念。。。  
会重构不？加些功能当学生毕设？  

基于webflux+r2dbc按异步非阻塞重构  
异步非阻塞的反应式真是不好写，思路逻辑与同步的写法完全不一样  
找时间测试一下阻塞/非阻塞性能效率的比较，reactive在低并发下效率可能不如同步高  

### Reactive
#### Flux/Mono
Flux/Mono发布者，支持异步订阅  
- map()，同步，映射操作结果，映射结果将置于Flux/Mono返回。内部如果调用其他异步方法，不会被执行    
- flatMap()，异步，映射操作结果，必须手动返回一个Flux/Mono。用于内部执行异步操作并返回publisher以继续进行后续操作。从而链接到一起形成非阻塞的异步执行链  
因此，多异步操作必须通过flatMap()链接调用  

#### WebFlux
异步非阻塞Web框架，直接支持背压  
webflux中禁止阻塞操作，所有方法必须返回Flux/Mono，直到controller方法返回。用户的发送到controller的请求即为消息订阅者      
使用异步的ServerHttpResponse/ServerHttpRequest接口替代Servlet接口，可直接注入controller方法  
依然可通过@RequestAttribute注解直接注入请求中数据  

#### WebFilter
拦截器通过WebFilter接口实现  
拦截路径不再单独注册声明，默认全部拦截，需手动通过请求路径判断拦截/排除规则。像Servlet提供注解多好    
通过order声明拦截顺序  
没找到像以前Interceptor的注册方法  

#### WebExceptionHandler
在filter中抛出的异常，无法在全局异常中捕获，必须通过WebExceptionHandler接口单独处理  

#### R2DBC
标准JDBC是阻塞的，通过r2dbc实现整合webflux的全非阻塞实现  

#### @EventListener
可通过注解实现应用多种事件的监听回调。可通过监听ApplicationReadyEvent事件在应用就绪后执行初始化数据的插入  

### Updates
#### 2022.08.13
开跨域，给学生练习提供接口

#### 2021.11.08
后端基于reactive反应式编程重构。webflux+r2dbc  

#### 2021.11.02
先完成功能使系统可用，再提高优化的思路还是正确的  
还是得用nginx。支持缓存与压缩，数据量减少了38%，部署直接扔上去就行也不用再和后端一起打包  
前端增加生成学生选择excel表格功能    
学生记录冗余教师姓名，减少查询  
取消教师录入学生功能  
无权限请求将跳转至登录页面，而不再是简单的返回异常码  
又忘处理容器内时区问题了  
修复重复选择问题，还是得for update  