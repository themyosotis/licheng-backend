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

#### redis 内存不能无限增加，一定要设置过期时间！！！



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



### 定时任务实现

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



----

## 控制定时任务的执行

为啥？

1. 浪费资源（想象一下10000台服务器同时执行）
2. 脏数据（比如同时插入重复数据）



**要控制定时任务在同一时间只有一个服务器能执行**

怎么做？

1. 分离定时任务和主程序，只在一个服务器运行定时任务，成本太大

2. 写死配置，每个服务器都执行定时任务，但是只有 ip 符合配置的服务器才真实执行业务逻辑，其他的直接返回。成本最低；但是我们的 IP 可能是不固定的，把 IP 写的太死了

3. 动态配置，配置可以轻松的，很方便更新的（**代码无需重启**），但是只有 ip 符合配置的服务器才真实执行业务逻辑

   - 数据库
   - Redis
   - 配置中心（Nacos、Apollo、Spring Cloud Config）

   问题：服务器多了、IP 不可控还是很麻烦、还是需要人工修改

4. 分布式锁，只有抢到锁的服务器才能执行业务逻辑。坏处：增加成本；好处：不用手动配置，多少个服务器都一样



**单机就会存在单点故障**。



## 锁

有限资源的情况下，控制同一时间（段）只有某些线程（用户 / 服务器）能访问到资源。

Java 实现锁：synchronized 关键字、并发包的类

问题：只对单个 JVM 有效，即对单个服务器有效





## 分布式锁

为啥需要分布式锁

1. 有限资源的情况下，控制同一时间（段）只有某些线程（用户 / 服务器）能访问到资源。
2. 单个锁只对单个 JVM 有效



### 分布式锁实现的关键

#### 抢锁机制

怎么保证同一时间只有 1 个服务器能抢到锁？

**核心思想**就是：先来的人先把数据改成自己的标识（服务器 IP），后来的人发现标识已存在，就抢锁失败，继续等待。

等先来的人执行方法结束，把标识清空，其他的人继续抢锁。





MySQL 数据库：select for update 行级锁 （最简单）

乐观锁

✔️Redis 实现：内存数据库、**读写速度快。** 支持 **setnx**、lua脚本，比较方便我们实现分布式锁

setnx：set if not exists 如果不存在，则设置，只有设置成功返回true



### 注意事项

1. 用完锁要释放（省资源） √

2. **锁一定要加过期时间**  √

3. 如果方法执行时间过长，锁提前过期了？
   问题：

   1. 连锁效应：释放掉别人的锁
   2. 这样还是会存在多个方法同时执行的情况？

   解决方案：续期

   ```java
   boolean end = false;
   
   new Thred(()->{
       if(!end){
           续期
       }
   })
   
   end = true;
   ```

4. 释放锁的时候，有可能先判断出是自己的锁，当事者时锁过期了，最后还是释放别人的锁

   ````java
   // 原子操作
   if(get lock == A){
       del lock
   }
   ````

   Redis + lua 脚本实现

   lua不建议学，工作中用的不多

5. Redis 如果是集群（而不是只有一个 Redis），如果分布式锁的数据不同步怎么办？
   https://blog.csdn.net/qq_21383435/article/details/129733880



#### 拒绝自己写！！！！！



## Redisson 实现分布式锁

Java客户端，数据网格

实现了很多 Java 里支持的接口和数据结构



Redisson 是一个 java 操作 Redis 的客户端，**提供了大量的分布式数据集来简化对 Redis 的操作和使用，可以让开发者使用本地集合一样使用 Redis，完全感知不到 Redis 的存在。**



### 2 种引入方式

1. spring boot starter 引入 （不推荐，版本迭代太快，容易冲突）[redisson/redisson-spring-boot-starter at master · redisson/redisson (github.com)](https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter)
2. 直接引入https://github.com/redisson/redisson?tab=readme-ov-file#quick-start



#### 示例代码

```setnx
void testWatchDog(){

        RLock lock = redissonClient.getLock("licheng:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
            	// todo 实际要执行的方法
            	doSomeThings();
                System.out.println("getLock: " + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己的锁, 不要放到try里，如果报错了不会释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("unLock: " + Thread.currentThread().getId());
            }
        }
    }
```



### 定时任务 + 锁

1. waitTime 设置未 0， 只抢一次，抢不到就放弃
2. **注意释放锁要写在 finally 中**



### 看门狗机制

> redisson 中提供的续期机制



开一个监听线程，如果方法还没执行完，就帮你重置 redis 的过期时间



原理：

1. 监听当前线程，默认过期时间30秒，每 10 秒续期一次（补到30秒）
2. 如果线程挂掉（注意 debug 模式也会被它当成服务器宕机），则不会续期



```java
void testWatchDog(){

        RLock lock = redissonClient.getLock("licheng:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            // lock.tryLock(等待时间,多长时间释放锁,TimeUnit.MILLISECONDS)
            // 等待时间为0 即没获取到锁直接不执行任务，返回false
            // 等待时间非0，即未获取到锁等待这些时间，若到时间仍未获取到锁，不执行，返回false
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Thread.sleep(300000);
                System.out.println("getLock: " + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己的锁, 不要放到try里，如果报错了不会释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("unLock: " + Thread.currentThread().getId());
            }
        }
    }
```





----



Zookeeper 实现



## 组队功能

理解为王者荣耀

### 理想的应用场景

我要跟别人一起参加竞赛或者做项目，可以发起队伍或者假如别人的队伍

### 需求分析

用户可以**创建**一个队伍（工会），设置队伍的人数、队伍名称（标题）、描述、超时时间

> 队长、剩余人数
>
> 聊天？
>
> 公开 或 private 或 加密
>
> 信息流中不展示已过期的队伍
>
> **用户创建队伍最多5个** ，想多创建开VIP ：）



展示队伍列表，根据名称搜索队伍，根据标签名称搜索队伍 P0，信息流中不展示已过期的队伍

修改队伍信息 P0-P1

用户创建队伍最多 5 个

用户可以加入队伍（其他人、未满、未过期），允许加入多个队伍，但是要有上限 P0

> 是否需要队长同意？筛选审批？

用户可以退出队伍（如果是队长退出、权限转移给第二早加入的用户 —— 先来后到）P1

队长可以解散队伍 P0



---

分享队伍=》邀请其他用户加入队伍 P1

业务流程：

1. 生成分享链接（分享二维码）
2. 用户访问链接，可以点击加入



队伍人满后发送消息通知 P1





### 系统（接口）设计

#### 1、创建队伍

用户可以**创建**一个队伍（工会），设置队伍的人数、队伍名称（标题）、描述、超时时间

> 队长、剩余人数
>
> 聊天？
>
> 公开 或 private 或 加密
>
> 信息流中不展示已过期的队伍



1. 请求参数是否为空
2. 是否登录，未登录不允许创建
3. 校验信息
   1. 队伍人数 > 1 且 <=20
   2. 队伍标题 <= 20
   3. 描述 <= 512
   4. status 是否公开 （int）不传默认为 0（公开）
   5. 如果 status 是加密状态，一点要有密码，且密码  <= 32
   6. 超时时间 > 现在时间
   7. 校验用户最多创建 5 个队伍
4. 插入队伍信息到队伍表
5. 插入用户 => 队伍关系到关系表



#### 2、查询队伍列表

分页展示队伍列表，根据名称、最大人数等搜索队伍，根据标签名称搜索队伍 P0，信息流中不展示已过期的队伍

1. 从请求参数中去除队伍名称，如果存在则作为查询条件
2. 不展示已过期的队伍（根据过期时间筛选）
3. 可以通过某个**关键词**同时对名称和描述查询
4. **只有管理员才能查看加密还有非公开的房间**
5. 关联查询已加入队伍的用户信息
6. **todo：关联查询已加入队伍的信息（可能会很耗费性能，建议使用自己写 SQL 的方式实现）** 



#### 实现方式

1. 自己写 SQL

   ```sql
   // 关联查询用户信息
           // 1、自己写 SQL
           // 查询队伍和创建人的信息
           // select * from team t left join user u on t.userId = u.id
           // 查询队伍和已加入队伍成员的信息
           // select * from team t left join user_team ut on t.id = ut.teamId 
           //                   left join user u on ut.userId = u.id
   ```



#### 3、修改队伍信息 

1. 判断请求参数是否为空
2. 查询队伍是否存在
3. 只有管理员或者队伍的创建者可以修改
4. todo如果用户传入的新值和老值一致，就不用 update 了（可自行实现，降低数据库使用）
5. **如果队伍状态改为加密，必须要有密码**
6. 更新成功



#### 4、用户可以加入队伍

（其他人、未满、未过期），允许加入多个队伍，但是要有上限 P0

1. 用户最多加入 5 个队伍
2. 队伍必须存在只能加入未满、未过期的队伍
3. 不能重复加入已加入的队伍（幂等性）
4. 禁止加入私有队伍
5. 如果加入的队伍是加密的，必须密码匹配
6. 修改队伍信息，补充人数
7. 新增队伍 - 用户关联信息



**注意一定要加上事务注解！！！！**

> 注意，并发请求时可能出现问题



#### 5、用户可以退出队伍

> 如果是队长退出、权限转移给第二早加入的用户 —— 先来后到

请求参数：队伍 id

业务流程：

1. 校验请求参数

2. 校验队伍是否存在

3. 校验用户是否在队伍里

4. 如果队伍

   1. 只剩一人，队伍解散

   2. 还有其他人

      1. 如果是队长退出、权限转移给第二早加入的用户 —— 先来后到（取 ID 最小的用户，这里的 id 是 user-team表的，只有新加入队伍才会生成新纪录）

         > 只用取 id 最小的两条数据

      2. 非队长，自己退出队伍



#### 6、队长解散队伍

请求参数：队伍 id

业务流程：

1. 校验参数请求
2. 校验队伍是否存在
3. 校验你是不是队长
4. 移除所有加入队伍的关系
5. 删除队伍



#### 7、获取当前用户已加入的队伍

#### 8、获取当前用户创建的队伍

服用 listTeam 方法，只新增查询条件，不做修改（开闭原则）

---







队伍人满后发送消息通知 P1





#### 事务注解

@Transactional

要么数据操作都成功，要么都失败



### 数据库表设计

队伍表 team

字段

- id 主键 bigint （最简单、连续、放url上比较简短，但缺点是怕爬虫）
- name 队伍名称
- description 描述
- maxNum 最大人数
- expireTime 过期时间
- userId 创建人 Id
- status 0 - 公开，1 - 私有，2 - 加密
- password 密码
- createTime 创建时间
- updateTime 更新时间
- isDelete 是否删除



````sql
-- 队伍表
create table team
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    maxNum      int      default 1                 not null comment '最大人数',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint comment '用户id',
    status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                       null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除'

)
    comment '队伍';
````





用户 - 队伍表user_team

字段 

- id 主键
- userId 用户id
- teamId 队伍id
- joinTime 加入时间
- createTime 创建时间
- updateTime 更新时间
- isDelete 是否删除



````sql
-- 用户队伍关系
create table user_team
(
    id          bigint auto_increment comment 'id'
        primary key,
    userId          bigint comment '用户id',
    teamId          bigint comment '队伍id',
    joinTime   datetime  null comment '加入时间',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除'

)
    comment '用户队伍关系';
````





两个关系：

1. 用户加了哪些队伍
2. 队伍有哪些用户

方式：

1. **建立用户 - 队伍关系表 （便于修改，查询性能高一点，可以选这个，不用全表遍历）这里用该方法**
2. 用户表补充已加入的队伍字段，队伍表补充已加入的用户字段（不用写多对多的代码，可以根据队伍查用户、根据用户查队伍）



## 为什么需要请求参数包装类？

1. 请求参数名称和实体类不一样
2. 有一些参数用不到，如果要自动生成接口文档，会增加理解成本
3. 多个对象对应一个字段





可能有些字段



### 实现

库表设计

增删改查

业务逻辑开发





##### 出现的问题汇总：

1. 给后端传日期报以下错误：Cannot deserialize value of type java.util.Date from String

   解决:  https://blog.csdn.net/Hunipei/article/details/136626580 

   在对接前端的 DTO 实体类中，所有日期Date类型属性必须加上

   ```java
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   ```

   



# 2024-6-19

1. 开发前端功能
   1. 搜索队伍 √
   2. 更新队伍 √
   3. 查看个人已加入的队伍 √
   4. 查看个人已创建的队伍 √
   5. 解散队伍
   6. 退出队伍
   7. 
2. 随机匹配
3. 完成之前的遗留问题 todo



### 前端不同页面怎么传递数据？

1. **url querystring (xxx?id=1) 比较适用于页面跳转**
2. **url (/team/:id .xxx/1)**
3. hash (/team#1)
4. localStorage
5. **context (全局变量，同页面或整个项目要访问公共变量)**





## 随机匹配

> 为了帮大家更快的发现和自己兴趣相同的朋友

匹配 1 个还是多个？

答：匹配多个，并且按照相似度从高到低排序



怎么匹配（根据什么匹配）

答：标签 tags         varchar(1024)                      null 

>还可以根据 user_team 匹配加入相同队伍的用户



本质：找到有相似标签的用户

举例：

用户 A：[Java, 大一 , 男]

用户 B：[Java, 大二 , 男]

用户 C：[Python, 大二 , 女]

用户 D：[Java, 大一 , 女]



### 1、怎么匹配





1. 找到共同标签最多的用户
2. 共同标签越多，分数越高，越排在前面
3. 如果没有匹配的用户，随机匹配几个（降级方案)



编辑距离算法：https://blog.csdn.net/DBC_121/article/details/104198838

> 最小编辑距离：指字符串 1 最少经过多少次增删改字符的操作可以变成字符串 2

余弦相似度匹配算法：https://blog.csdn.net/qq_36488175/article/details/109787805（如果需要带权重，比如学什么方向比较重要，性别相对次要）

编辑距离算法：

````java
public int minDistance(String word1, String word2){
    int n = word1.length();
    int m = word2.length();
 
    if(n * m == 0)
        return n + m;
 
    int[][] d = new int[n + 1][m + 1];
    for (int i = 0; i < n + 1; i++){
        d[i][0] = i;
    }
 
    for (int j = 0; j < m + 1; j++){
            d[0][j] = j;
    }
 
    for (int i = 1; i < n + 1; i++){
        for (int j = 1; j < m + 1; j++){
            int left = d[i - 1][j] + 1;
            int down = d[i][j - 1] + 1;
            int left_down = d[i - 1][j - 1];
            if (word1.charAt(i - 1) != word2.charAt(j - 1))
                left_down += 1;
            d[i][j] = Math.min(left, Math.min(down, left_down));
        }
    }
    return d[n][m];
}
````



### 2、怎么对所有用户匹配，取 TOP

直接取出所有用户，依次和当前用户计算分数，取 TOP N（54秒）

优化方法：

1. 切记不要在数据量答的时候循环输出日志（取消掉日志后 20 秒）

2. Map 存了所有的分数信息，占用内存

   解决：维护一个固定长度的有序集合（sortedSet），只保留分数最高的几个用户 (时间换空间)

3. 细节：剔除自己 √

4. 尽量只查需要的用户

   1. 过滤掉标签为空的用户 √
   2. 根据部分标签取用户 （前提是能区分出来哪个标签比较重要）
   3. 只查需要的数据（比如 id 和 tags）√ （7.2 s）

5. 提前查？

   1. 提前把所有用户给缓存 （不适用于经常更新的数据）
   2. 提前运算出来结果，缓存（针对一些重点用户，提前缓存）



大数据推荐，比如说有几亿个商品，难道要查出来所有的商品？

难道要对所有的数据计算一遍相似度？



检索 => 召回 => 粗排 => 精排 => 重排序等等

检索：尽可能多地查符合要求的数据（比如按记录查）

召回：查询可能要用到的数据（不做运算）

粗排：粗略排序，简单的运算（运算相对轻量）

精排：精细排序，确定固定排位



### 分表学习建议

mycat、sharding sphere 框架

一致性 hash



## 队伍操作权限控制

加入队伍：仅非队伍创建人，且未加入可见

更新队伍：仅创建人可见

解散队伍：仅创建人可见

退出队伍：创建人不可见，仅加入队伍的人可见



# 优化、上线

1. 加载 骨架屏 特效 √
2. 仅加入队伍和创建队伍的人可以看见队伍操作按钮 （listTeam 接口要能获取我加入的队伍状态）√
   1. 方案 1：前端查询我加入了哪些队伍列表，然后判断每个队伍 id 是否在列表中（前端多发一次请求）
   2. 方案 2：后端去做这些（推荐）
3. 前端死【标题】问题 √
   1. 使用 router.beforeEach ，根据 要跳转页面的url 路径 匹配 config/routes 配置的 title 字段
4. 强制登录，前端全局响应拦截，自动跳转到登录页 √
   1. 解决：axios 全局配置相应拦截、并且添加重定向
5. 区分公开和加密的房间，加入有密码的房间要设置密码 √
6. 展示队伍已加入人数 √
7. 重复加入队伍的问题（加锁，分布式锁）√



# 上线

1. 先区分多环境：前端区分开发和线上接口，后端 prod 改为用线上公网可访问数据库



前端：Vercel （免费）



后端：微信云托管 （付费）



数据库 sqlfather



# todo 需要学习内容

1. Redis
2. Session
3. 



# todo可优化

1. 分布式锁 导致的其他服务器数据不统一的问题
   或者多个 Redis 里的数据不一致。
2. 标签
3. 仅加入队伍和创建队伍的人能看到队伍
4. 可以增加AOP进行全局的参数判空操作，这样conller中就可以不需要重复判空了
5. 展示我加入的队伍，当我没有加入任何队伍，会输出我没加入的队伍，原理是，listMyJoinTeams调用teamService.listTeams(teamQuery, true);有问题，这里会传入idList为【】的teamQuery,导致查询所有队伍





# todo 发现BUG

1. 注册用户账号大小写不敏感



第61集  10：00 

准备编写 Coltroller层 推荐接口
