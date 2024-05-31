# 伙伴匹配系统

介绍：帮助用户找到志同道合的伙伴，移动端H5网页（尽量兼容PC端）

## 需求分析

1.给用户添加标签、标签的分类（需要有什么标签、怎么把标签进行分类）学习方向 java\c++  ,工作/大学……

2.主动搜索：根据标签搜索其他用户

1. Redis 缓存

3.组队

1. 创建队伍
2. 加入队伍
3. 根据标签查询队伍 
4. 邀请其他人

4.允许用户去修改标签

5.推荐

1. 相似度计算算法+本地分布式计算





# 技术栈

## 前端

1. Vue 3 开发框架
2. Vant UI （React版 Zent）
3. Vite（打包工具，快！）
4. Nginx单机部署



## 后端

1. Java+SpringBoot
2. SpringMVC + Mybatis + Mybatis Puls
3. MySql数据库
4. Redis缓存
5. Swagger + Knife4j接口文档



## 第一期计划

1. 前端项目初始化
2. 前端主页 + 组件概览
3. 数据库表设计
   1. 标签表
   2. 用户表
4. 开发后端 - 根据标签搜索用户
5. 开发前端 - 根据标签搜索用户



## 前端项目初始化

用vite脚手架初始化项目

- Vue CLI
- Vite脚手架

整合组件库Vant 3 ：

- 安装Vant
- 按需引入 npm i unplugin-vue-components -D

开发页面经验

1. 多参考
2. 从整体到局部
3. 先想清楚页面要做成什么样子，再写代码





## 前端主页 + 组件概览

设计

导航条：展示当前页面路径

主页搜索框==>搜索页==>搜索结果页

内容：

tab栏：

- 主页（推荐页+**广告**）
  - 搜索框
  - banner
  - 推荐信息流

- 搜索页
- 队伍页
- 用户页（消息 - 暂时考虑用邮件的方式）





## 开发

很多页面复用组件/样式、重复写很麻烦、不利于维护、所以抽象一个通用的布局

组件化



## 数据库表设计

标签的分类（要有哪些标签、怎么把标签进行分类）

### 标签表（分类表）

建议用标签，不要用分类，更灵活

性别：男、女

方向：Java、C++、Go、前端

正在学：Spring

目标：考研、春招、秋招、社招、考公、竞赛（蓝桥杯）、转行

段位：初级、中级、高级、王者

身份：大一、大二、大三、大四、学生、待业、已就业、研一、研二、研三

状态：乐观、有点丧、一般、单身、已婚、有对象

**【用户自己定义标签】？**



字段：

id int 主键

标签名 varchar 非空（必须唯一，唯一索引）

上传标签的用户 userId int（如果要根据userId查已上传标签的话，最好加上，普通索引）

父标签 id, parendId ,int（分类）

是否为父标签 isParent, tinyint（0 - 不是父标签、1 - 是父标签）

创建时间 createTime, datetime

更新时间 updateTime, datetime

是否删除 isDelete, tinyint（0、1）



```DDL
create table tag
(
    id                  bigint auto_increment comment 'id'  primary key,
    tagName             varchar(256)                        null comment '标签名称',
    userId              bigint                              null comment '用户 id',
    parentId            bigint                              null comment '父标签 id',
    isParent            tinyint                             null comment '0 - 不是、1 - 是父标签',
    createTime          datetime default CURRENT_TIMESTAMP  null comment '创建时间',
    updateTime          datetime default CURRENT_TIMESTAMP  null on update CURRENT_TIMESTAMP,
    isDelete            tinyint     default 0               not null comment '是否删除'
)
    comment '标签';
```





怎么查询所有标签，并且把标签分好组？按父标签id分组，能实现√

根据父标签查询子标签？ 根据id查询，能实现√



SQL语言分类

DDL define 建表、操作表

DML manage 更新删除数据，影响实际表里的内容

DCL control 控制、权限

DQL query 查询

[MySQL常用DDL、DML、DCL、MDL、WAL语言整理_ddl mdl-CSDN博客](https://blog.csdn.net/hacker_Lees/article/details/106680712)



### 修改用户表

用户有哪些标签？

##### 根据自己的需求来！！！

1. 直接在用户表补充tags字段 ，**['Java','男‘]存json字符串**  这里选择第一种

   优点：查询方便，不用新建关联表，标签是用户的属性（除了该系统，其它系统也可能用到，标签是用户的固有属性）节省开发成本

   哪怕性能低，可以用缓存优化。

   缺点：用户表多一列，会有点……

2. 加一个关联表，记录用户和标签的关系

   关联表的应用场景：查询灵活，可以正查反查

   缺点：要多建一个表、多维护一个表

   重点：企业大项目开发中尽量减少关联查询、很影响扩展性、而且会影响查询性能





```DDL
-- auto-generated definition
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户   1 - 管理员',
    planetCode   varchar(512)                       null comment '星球编号',
    tags         varchar(1024)                      null comment '标签列表'
)
    comment '用户';


```



### 开发后端接口

搜索标签

1. 允许用户传入多个标签，多个标签都存在才搜索出来 and 。 like  '%Java%' and like '%C++%'
2. 允许用户传入多个标签，有任何一个标签存在就能搜索出来 or。 like  '%Java%' or like '%C++%'

两种方式

1. SQL查询(实现简单，可以通过拆分查询进一步优化)
2. 内存查询（灵活，可以通过并发进一步优化）



- 如果参数可以分析，根据用户的参数去选择查询方式，比如标签数量
- 如果参数不可分析，并且数据库连接足够，内存空间足够，可以并发同时查询，谁先返回用谁
- 还可以SQL查询与内存计算相结合，比如先用SQL过滤掉部分tag



建议通过实际测试来分析哪种查询比较快，数据量哒的时候验证效果更明显！



解析JSON字符串

序列化：Java对象转成json

反序列化：json转成java对象



Java json 序列化库，推荐gson

1. fastjson alibaba（ali出品，快，但是漏洞太多）
2. gson （gogle的）
3. jackon
4. kryo



# 第二期计划



## 2024-4-21

计划

1. 上次标签接口调试
2. 前端整合路由
3. 前端开发（搜索页面、用户信息页、用户信息修改页）
4. 后端整合Swagger + Knife4j接口文档
5. 存量用户信息导入及同步（爬虫）





### Java 8特性

1. stream / parallelStream 流式处理
2. Optional 可选类

### 前端整合路由

Vue-Router:[入门 | Vue Router (vuejs.org)](https://router.vuejs.org/zh/guide/),直接看官方文档引入

Vue-Router帮助根据不同url来展示不同的页面（组件），不用自己写if/else

路由配置影响整个项目，建议单独使用config目录，单独的配置文件去集中定义和管理

有些组件库可能自带了Vue-Router的整合，尽量先看文档，省时间。



## 第三期计划

1. ~~后端整合 Swagger = Knife4j 接口文档~~ 这里使用了apifox代替进行接口管理
2. 存量用户信息导入及同步（爬虫）
3. 前后端联调：搜索页面、用户信息页、用户信息修改页
4. 标签内容整理
5. 部分细节优化



## ~~后端整合 Swagger = Knife4j接口文档~~ apifox代替

什么是接口文档？ 写接口信息的文档，每条接口包括：

- 请求参数
- 响应参数
  - 错误码
- 接口地址
- 接口名称
- 请求类型
- 请求格式
- 备注

谁用？一般是后端或者负责人来提供，后端和前端都要使用

为什么需要接口文档？

- 有个书面内容（背书或者归档），便于大家参考和查阅，便于沉淀和维护，拒绝口口相传
- 接口文档便于前端和后端开发对接，前后端联调的介质。
- 好的接口文档支持在线调试，在线测试，可以作为工具提高开发测试效率

怎么做接口文档？(我这里使用apifox，操作简单，并且用idea的插件，不需要配环境)

- 手写（比如腾讯文档、Markdomn笔记）
- 自动化接口文档生成：自动根据项目代码生成完整的文档或在先调试的网页。Swagger, Postman(侧重接口管理)、 apifox、 apipost、eolink ( 国产)

接口文档有哪些技巧？

Swagger 原理：

1. 自定义 Swagger 配置类
2. 定义需要生成接口文档的代码位置（Controller），千万注意：线上环境不要把接口暴露出去！！！



# 存量用户信息导入及同步

1. 把所有知识星球用户的信息导入
2. 把写了自我介绍的同学的标签信息导入



### 看上了网页信息，怎么抓到

1. 分析原网站是怎么获取信息的，哪个接口

   ```bash
   curl 'https://api.zsxq.com/v2/hashtags/48844541281228/topics?count=20' \
     -H 'accept: application/json, text/plain, */*' \
     -H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6,fr-FR;q=0.5,fr;q=0.4' \
     -H 'origin: https://wx.zsxq.com' \
     -H 'priority: u=1, i' \
     -H 'referer: https://wx.zsxq.com/' \
     -H 'sec-ch-ua: "Chromium";v="124", "Microsoft Edge";v="124", "Not-A.Brand";v="99"' \
     -H 'sec-ch-ua-mobile: ?0' \
     -H 'sec-ch-ua-platform: "Windows"' \
     -H 'sec-fetch-dest: empty' \
     -H 'sec-fetch-mode: cors' \
     -H 'sec-fetch-site: same-site' \
     -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0' \
     -H 'x-request-id: e51b15372-e85a-50ff-2cc1-6bb3d070838' \
     -H 'x-signature: aff120884b7049bda60155fc82908936ad71e6d6' \
     -H 'x-timestamp: 1714628832' \
     -H 'x-version: 2.55.0'
   ```

   

2. **用程序去调用接口 （java/python都可以）**

3. 处理（清洗）一下数据，之后就可以写到数据库里



## 流程

1. 从excel中导入全量用户数据，判重 easy excel ：[EasyExcel官方文档 - 基于Java的Excel处理工具 | Easy Excel (alibaba.com)](https://easyexcel.opensource.alibaba.com/)
2. 抓起学了自我介绍的同学信息提取出用户昵称、用户唯一id、自我介绍信息
3. 从自我介绍中提取信息，然后写到数据库中



#### EasyExcel

两种读对象的方式

1. 确定表头：建立对象，和表头形成映射关系
2. 不确定表头：每一行数据映射为Map<String, Object>

两种读取模式：

1. 监听器：先创建监听器、在读取文件时绑定监听器。单独抽离处理逻辑，代码清晰易于维护；一条一条处理，适用于数据量大的场景。
2. 同步读：无需创建监听器，一次性需要获取完整数据。方便简单，但是数据量大时会有等待时长，也可能内存溢出。

### 到这里其实没办法抓取星球的内容，因为名称重复的比较多，决定用户自己输入



# 2024/5/2 第三期

1. 页面和功能开发
   1. 搜索页面
   2. 用户信息
   3. 用户修改页面
2. 改造用户中心，把单机登录改为分布式 session 登录
3. 标签的整理、细节的优化



### 前端页面跳转传值

1. query=>	url searchParams, url后附加参数，传递的值有限
2. vuex（全局状态管理），搜索页将关键词塞到状态中，搜索结果页从状态取值



### Session共享

种 session 的时候注意范围，cookie.domain

比如两个域名：

aaa.cc.com

bbb.cc.com

如果要共享cookie ,可以种一个更高层的公共域名，比如cc.com



### 为什么服务器A登陆后，请求发送到服务器B，不认识用户

用户在A登录，所以session (用户登录信息) 存在了A上

结果请求B时，B没有用户信息，所以不认识。



![](G:\练手项目\伙伴匹配系统\c65cbd82-65d9-4211-aa73-06ca55fd36bc.png)



解决方案：共享存储，而不是把数据放到单台服务器的内存中

![](G:\练手项目\伙伴匹配系统\79a1c7a3-8011-4b66-99c5-c494dd591bec.png)



如何共享存储？

1. Redis （基于内存的 K / V（键值对） 数据库）此处选择Redis，因为用户信息读取 / 是否登录的判断及其**频繁**，Redis基于内存，读写性能很高，简单的数据单机 qps 5w-10w。
2. MySQL
3. 文件服务器ceph



### Session 共享实现

##### 	1. 安装redis

官网：https://redis.io/

windows下载：

Redis 下载：5.0.14

redis管理工具：quick redis：https://quick123.net/

​	2.引入redis，能够操作redis

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>3.2.4</version>
</dependency>
```

3. 引入spring-session和redis的整合，使得自动将session存储到redis中

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
    <version>3.2.2</version>
</dependency>
```

4. ~~修改spring-session存储配置 spring.session.store-type=redis~~ (鱼皮的版本是2.6.4，我的是3.2.4，不太一样，没有这个参数，原因如下)

原因：原文链接：http://t.csdnimg.cn/UP5Sl

**原因：session store type 是用来设置session的存放方式，现在Spring boot中只支持reids存储方式。**

解决方法：无需再写这个参数啦~



# 2024-5-20

1. 用户修改页面前端、后端开发和联调
2. 开发主页（默认推荐和自己兴趣相当的用户）
3. 优化主页性能（缓存+定时任务+分布式锁）



### 踩坑！！前端配置传递cookie时后端也需要添加配置文件

http://t.csdnimg.cn/EiqXF

```myaxios.js
//踩坑：  这里有问题 ，需要后端controller添加注解或者config添加配置类，可以搜：Access-Control-Allow-Origin
myAxios.defaults.withCredentials = true;//向后台发送请求时携带cookie
```

```java
/**
 * SpringBoot可以基于Cors解决跨域问题，
 * Cors是一种机制，设置那些请求可以访问服务器的数据。
 */
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            // 重写父类提供的跨域请求处理的接口
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 添加映射路径
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")                         // 放行哪些域名，可以多个
                        .allowCredentials(true)                             // 是否发送Cookie信息
                        .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 放行哪些请求方式
                        .allowedHeaders("*")                                // 放行哪些原始域(头部信息)
                        .exposedHeaders("Header1", "Header2")               // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
                        .maxAge(3600);                                      // 预请求的结果有效期，默认1800分钟,3600是一小时
            }
        };
    }
}
```





## 开发主页

最简单：直接list列表

模拟1000万个用户，再去查询

### 导入数据

1. 可视化界面：适合一次性导入，数据量可控
2. 写程序：for 循环，建议分批，不要一把梭哈（建议用接口来控制） **要保证可控、幂等，注意线上环境和测试环境是有区别的**
3. 执行SQL语句：适用小数据量



**编写一次性任务**

for循环插入数据的问题：

1. 建立和释放数据库链接（批量查询解决，用MyBatis Service层自带的userService.saveBatch(userList,100);）
2. for循环是绝对线性的，必须等到上一条执行完才能继续下一条（解决：并发）



**并发要注意执行的先后顺序无所谓，不要用到非并发类的集合（如ArrayList）要用Collections.syncronizedList包一下**

```java
List<User> userList = Collections.synchronizedList(new ArrayList<>());
```





```java
// CPU 密集型： 分配的核心线程数 = CPU - 1 本机为16-1=15
// IO 密集型： 分配的核心线程数可以大于 CPU 核心数
private ExecutorService executorService =new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES,new ArrayBlockingQueue<>(10000));
```



自己定义一个线程池，核心为60，最大1000线程，线程存活时间10分钟，new ArrayBlockingQueue<>(10000)

1)当池子大小小于corePoolSize就新建线程，并处理请求

2)当池子大小等于corePoolSize，把请求放入workQueue中，池子里的空闲线程就去从workQueue中取任务并处理

3)当workQueue放不下新入的任务时，新建线程入池，并处理请求，如果池子大小撑到了maximumPoolSize就用RejectedExecutionHandler来做拒绝处理

4)另外，当池子的线程数大于corePoolSize的时候，多余的线程会等待keepAliveTime长的时间，如果无请求可处理就自行销毁

看这个文章：

**http://t.csdnimg.cn/BRAO7**



**这里数据库大概有了200万条数据**



**MyBatis Plus使用分页查询需要去官网复制拦截器springboot的config文件**

直接在MyBatis Plus官网搜 “分页”：

[分页插件 | MyBatis-Plus (baomidou.com)](https://baomidou.com/plugins/pagination/)



**数据库查询慢，预先把数据查询出来，放到一个读取更快的地方，不用再读取数据库（缓存）**

预加载缓存，定时更新缓存（定时任务）

多个机器都要执行吗？（分布式锁：控制同一时间只有一台机器去执行定时任务，其他机器不用重复执行了）



## 数据查询慢怎么办？

用缓存：提前把数据取出来保存好（通常是保存到读写更快的介质，比如内存），就可以更快地读写。



### 缓存的实现

- Redis（分布式缓存）
- memcached（分布式）
- Etcd（云原生架构的一个分布式存储，**存储配置**，扩容能力）

----

- ehcache(单机)
- 本地缓存（java内存，Map）
- Caffeine（Java内存缓存，高性能）
- Google Guava



## Redis

>NoSQL 数据库

key - value 存储系统（区别于MySQL，它存储的是键值对）

### Redis 数据结构

String 字符串类型：name:"xincheng"

List 列表：names:["xincheng","dogxincheng","xincheng"]

Set 集合: names["xincheng","dogxincheng"]（值不能重复）

Hash 哈希：nameAge：{"xincheng":1,"dogxincheng":2}

Zset 集合： names：{ xincheng - 9, dogxincheng  - 12 } (每个值跟一个分数，适合做排行榜)



bloomfilter（布隆过滤器，主要从大量的数据中快速过滤值，比如邮箱黑名单拦截）

geo (计算地理位置)

hyperloglog  (pv / uv)

pub / sub （发布订阅，类似消息队列）

BitMap （01010101010101010）



### 自定义序列化

```java
package cn.ujn.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author XinCheng
 * date 2024-05-31
 */
@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        return redisTemplate;
    }
}

```



> 引入一个库时，先写测试类



### Java里的实现方式

#### Spring Data Redis（推荐）

Spring Data通用的数据访问框架，定义了一组**增删改查**的接口

mysql，redis，jpa

spring-data-redis



1. 引入

   ```java
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
       <version>3.2.4</version>
   </dependency>
   ```

2. 配置Redis地址

   ````yml
   spring:
   # redis 配置
     data:
       redis:
         port: 6379
         host: localhost
         database: 0
   ````



#### Jedis

独立于Spring操作的Redis的 Java 客户端

要配合Jedis Pool使用



#### Lettuce

**高阶**的操作 Redis 的 Java 客户端

异步、连接池



#### Redisson

分布式操作 Redis 的 Java 客户端，让你像在使用本地的集合一样操作Redis（分布式的 Redis 数据网格）



#### JetCache



### 对比

1. 如果用的是Spring，并且没有过多的定制化要求，可以用 Spring Data Redis，最方便
2. 如果你用的不是Spring，并且追求简单，并且没有过高的性能要求，可以用Jedis + Jedis Pool
3. 如果你的项目不是Spring ，并且追求高性能、高定制化，可以用 **Lettuce**

----

- 如果你的项目是分布式的，需要用到一些分布式的特性（比如分布式锁，分布式集合），推荐使用 redisson



### 设计缓存的Key

不同用户看到的数据不同

systemId:moduleId:func:options (不要和别人冲突)

licheng:user:recommend:userId

#### redis 内存不能无限增加，一定要设置国企时间！！！



## 缓存预热

问题：第一个用户访问还是很慢（假如第一个用户是老板），也能一定程度上保护数据库

缓存预热的优点：

1. 解决上面的问题，可以让用户始终访问很快

缺点：

1. 增加开发成本（要额外的开发、设计）
2. 预热的时机和时间如果错了，可能缓存的数据不对或者太老
3. **需要占用额外空间**



### 怎么缓存预热

1. 定时
2. 模拟触发（手动触发）



> 分析优缺点的时候，要打开思路，从整个项目从0到1的链路上去分析



#### 实现

用定时任务，每天刷新所有用户的推荐列表

注意点：

1. 缓存预热的意义（新增少，总用户多）
2. 缓存空间不能太大，要预留其他缓存空间
3. 缓存数据的周期（此处每天一次）



## 定时任务实现

1. **Spring Scheduler（spring boot 默认整合了）**@Scheduled
2. Quartz (独立于Spring存在的定时任务框架)
3. XXL-job之类的分布式任务调度平台（界面+sdk）



第一种方式：

1. 主类开启@EnableScheduling
2. 给要定时执行的方法添加@Scheduled注解，指定cron表达式或者执行频率

> 这里实现是设置mainUserList，对mainUser进行缓存预热，使用定时任务每天定时执行，仅为实验，实际使用需要结合具体情况
>
> 后续优化可以对设置mainUser表对其进行缓存预热





不要去背 cron 表达式！！！！！！

- [Cron - 在线Cron表达式生成器 (ciding.cc)](https://cron.ciding.cc/)
- [在线Cron表达式生成器 (qqe2.com)](https://cron.qqe2.com/)



# todo 待优化

前端：动态展示页面标题，微调格式



37：00
