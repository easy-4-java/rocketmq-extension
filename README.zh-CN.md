# rocketmq-extension

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-8-orange)](https://github.com/easy-4-java/rocketmq-extension) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

`rocketmq-extension` 是基于 RocketMQ 客户端（4.5.x）实现的轻量业务封装，简化消息的发布与订阅。
项目拆分为两个模块，让非 Spring 运行环境也能依赖纯 Java 的核心：

| 模块                            | 是否依赖 Spring | 作用                                                                |
| :------------------------------ | :--------------- | :------------------------------------------------------------------ |
| `rocketmq-extension-core`        | **否**           | 消息封装、责任链路由、Disruptor 适配、INI 解析、SPI 抽象 |
| `rocketmq-extension-spring`      | 是（传递依赖）   | 生产者/消费者/责任链的 `FactoryBean`、Spring `ApplicationEvent` 适配、`RocketmqTemplate`、生命周期钩子 |

## 目录

- [1. 项目概述](#1-项目概述)
- [2. 功能与状态](#2-功能与状态)
- [3. 环境要求与兼容性](#3-环境要求与兼容性)
- [4. 架构与模块](#4-架构与模块)
- [5. 安装](#5-安装)
- [6. 快速开始](#6-快速开始)
- [7. 配置](#7-配置)
- [8. 核心用法 / API](#8-核心用法--api)
- [9. 测试与构建](#9-测试与构建)
- [10. 版本线与分支](#10-版本线与分支)
- [11. 贡献与许可](#11-贡献与许可)

## 1. 项目概述

**rocketmq-extension** 是基于 RocketMQ 客户端（4.5.x）实现的轻量业务封装，简化消息的发布与订阅，并支持多种消费方式：

| 方式                     | 机制                                                                  |
| :----------------------- | :-------------------------------------------------------------------- |
| 普通消费                 | `DefaultMessageConsumeListener` + `RECONSUME_LATER` 重试              |
| 责任链消费               | 按 `Topic / Tags / Keys`（路径式表达式）将消息路由到对应 Handler      |
| 异步消费                 | 基于 Disruptor 的事件分发（依赖 `io.github.easy4j:disruptor-extension`）     |
| Spring 事件消费          | 将每条消息以 Spring `ApplicationEvent`（`SpringRocketmqEvent`）发布   |

| 是                                                     | 不是                                          |
| :----------------------------------------------------- | :-------------------------------------------- |
| RocketMQ 客户端的业务封装层                            | RocketMQ broker/client 的替代品               |
| 基于路径表达式的责任链路由                             | 通用 ESB 或消息网关                           |
| 通过 Spring `FactoryBean` 装配生产者/消费者            | Spring Boot starter                           |
| 双模块拆分：纯 Java core + Spring adapter              | 核心层强依赖 Spring                           |

典型场景：

| 场景                       | 描述                                                       |
| :------------------------- | :--------------------------------------------------------- |
| 数据中心接入               | 一个 Topic 多个 Tag，每个 Tag 由独立 Handler 处理         |
| 订单 / 短信 / 通知路由     | `/Order/TagCreated/**` → handler A，`/Order/TagPaid/**` → handler B |
| 高吞吐异步消费             | 使用 Disruptor 环形队列处理突发流量                        |
| 事件驱动集成               | 在同一 Spring 上下文中通过 `ApplicationEvent` 监听器消费   |

## 2. 功能与状态

| 能力                                                | 状态      | 主要 API                                                                                  | 模块     |
| :-------------------------------------------------- | :-------- | :---------------------------------------------------------------------------------------- | :------- |
| 发布封装（同步/异步/单向/批量/有序/事务）           | 已实现    | `RocketmqTemplate` —— 镜像 `MQProducer` 发送变体；选择器（`HASH_SELECTOR`、`RANDOOM_SELECTOR`、`Machine_RANDOOM_SELECTOR`） | spring |
| 消费者工厂                                          | 已实现    | `MQPushConsumerFactoryBean`（`ConsumerConfig`、监听器、offset store、队列分配策略）         | spring |
| 生产者工厂                                          | 已实现    | `MQProducerFactoryBean`（`ProducerConfig`、默认 `DefaultTransactionCheckListener`）      | spring |
| 可重试消费                                           | 已实现    | `DefaultMessageConsumeListener` —— 通过 `RECONSUME_LATER` 重试，上限 `retryTimesWhenConsumeFailed` | core |
| 责任链路由                                          | 已实现    | `MQEventHandlerFactoryBean` + `DefaultHandlerChainManager` + `PathMatchingHandlerChainResolver`；路径表达式 `/Topic/Tags/Keys` | spring + core |
| Disruptor 异步消费                                   | 已实现    | `RocketmqDisruptorEvent`、`RocketmqDataEventFactory` / `RocketmqDataEventTranslator`（依赖 `disruptor-extension`） | core |
| Spring ApplicationEvent 消费                         | 已实现    | `SpringRocketmqEvent`（继承 `ApplicationEvent`，包装 `RocketmqEvent`）+ `ApplicationEventMessageHandler` | spring |
| 关闭钩子                                            | 已实现    | `MQProducerShutdownHook`、`MQPushConsumerShutdownHook`                                     | spring |
| INI 风格的责任链定义                                | 已实现    | `config.Ini` 解析器；`setHandlerChainDefinitions("...")`                                   | core |
| 单元测试                                            | 已实现    | JUnit Jupiter / JUnit 4 —— 两模块共 436 个测试（core 400 + spring 36）                   | both |

## 3. 环境要求与兼容性

**本分支：`feature/3.0.x` —— JDK 21，Maven 4.0.0-rc-5。**

| 依赖                | 1.0.x                                | 2.0.x                                | **3.0.x（本分支）**                  |
| :------------------ | :----------------------------------- | :----------------------------------- | :----------------------------------- |
| JDK                 | 8                                    | 17                                   | 21                                   |
| Maven               | 3.9.16                               | 3.9.16                               | 4.0.0-rc-5                           |
| RocketMQ            | 4.5.2（`rocketmq-client`、`rocketmq-common`） | 4.5.2                          | 4.5.2                                |
| Spring Framework    | 5.3.39（5.3.x 终版；JDK 8 线）       | 6.2.19                               | 7.0.8                                |
| disruptor-extension | 1.0.x.20260630-SNAPSHOT              | 2.0.x.20260630-SNAPSHOT              | 3.0.x.20260630-SNAPSHOT              |
| junit-jupiter       | 5.11.4                               | 6.1.0                                | 6.1.0                                |

**硬性约束**（由 `maven-enforcer-plugin` 在 `rocketmq-extension-core` 上强制）：core 模块**零
Spring 依赖**。所有 Spring 相关代码都在 `rocketmq-extension-spring` 模块。路径匹配器
（`AntPathMatcher`）来自 `com.lmax.disruptor.util.AntPathMatcher`（纯 Java 实现，由
`disruptor-extension` 提供），因此 core 模块不会传递引入 `spring-core`。

## 4. 架构与模块

```text
                   生产者路径                              消费者路径
                        |                                       |
                        v                                       v
                RocketmqTemplate ------------------> MQPushConsumerFactoryBean
                 (send/oneway/batch/tx)                              |
                                                                       v
                                                        DefaultMessageConsumeListener（重试）
                                                                       |
                                                                       v
                                                        MQEventHandlerFactoryBean -> HandlerChain
                                                        /Topic/Tags/Keys = handler1, handler2 ...
                                                         |               |           |
                                                         v               v           v
                                                  RocketmqEventMessageHandler
                                                         |               |           |
                                         (DefaultRocketmqEvent)       (Disruptor)   普通 Handler
                                                         |
                                           +----- SpringRocketmqEvent
                                           |     （继承 ApplicationEvent，
                                           |      实现 RocketmqEvent）
                                           v
                             ApplicationEventPublisher
                                           |
                                           v
                            Spring ApplicationContext 监听器
```

多模块 Maven 项目，根 `packaging=pom`：

| 模块 / 包                                  | 职责                                                                                            |
| :----------------------------------------- | :---------------------------------------------------------------------------------------------- |
| `org.apache.rocketmq.client.extension`（根）| 父 pom —— revision + dependencyManagement + 插件。唯一可部署单元。                              |
| `org.apache.rocketmq.client.extension`（`core`）| 消息封装（`RocketmqEvent` 接口 + `DefaultRocketmqEvent` POJO）、责任链框架（`EventHandler`、`HandlerChain*`、`DefaultHandlerChainManager`、`PathMatchingHandlerChainResolver`）、Disruptor 适配（`RocketmqDataEventFactory`、`RocketmqDataEventTranslator`、`RocketmqDisruptorEvent`）、`Ini` 解析器、异常类型、`StringUtils` 助手、纯 `MessageListener` 实现。 |
| `org.apache.rocketmq.client.extension.spring` | `RocketmqTemplate`（`@Autowired MQProducer` / `MQPushConsumer`）、`FactoryBean`（`MQProducerFactoryBean`、`MQPushConsumerFactoryBean`、`MQEventHandlerFactoryBean`）、`SpringRocketmqEvent` ApplicationEvent 适配器、`ApplicationEventMessageHandler` / `DisruptorEventMessageHandler` / `RocketmqEventMessageHandler`、JVM 关闭钩子。 |

## 5. 安装

制品发布到 aliyun 私有仓库和 GitHub Releases，**尚未** 发布到 Maven Central。

如果使用 Spring，依赖 spring 模块即可 —— 它会传递引入 core：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>rocketmq-extension-spring</artifactId>
    <version>3.0.x.20260630-SNAPSHOT</version>
</dependency>
```

如果只需要纯 Java core（classpath 上没有 Spring），直接依赖 core：

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>rocketmq-extension-core</artifactId>
    <version>3.0.x.20260630-SNAPSHOT</version>
</dependency>
```

```groovy
// Spring 用户
implementation 'io.github.easy4j:rocketmq-extension-spring:3.0.x.20260630-SNAPSHOT'

// 非 Spring 用户
implementation 'io.github.easy4j:rocketmq-extension-core:3.0.x.20260630-SNAPSHOT'
```

## 6. 快速开始

### 6.1 Spring 用户

使用 `RocketmqTemplate` 发布消息（spring 模块）：

```java
@Autowired
private RocketmqTemplate rocketmqTemplate;

public void publish() throws Exception {
    SendResult result = rocketmqTemplate.send(
            "Topic-DC-Output",   // topic
            "TagA-Output",       // tags
            "OrderID001",        // keys（业务唯一性）
            "hello rocketmq");   // body
}
```

使用责任链消费（Spring 装配 FactoryBean，链本身在 core 中）：

```java
// 1) Handler（纯 Java —— 任何实现 EventHandler<RocketmqEvent> 的类）
Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
handlers.put("inDbPostHandler", new InDbPostHandler());
handlers.put("smsPostHandler", new SmsPostHandler());

// 2) 路由表达式：/Topic/Tags/Keys = handler(s)
MQEventHandlerFactoryBean factoryBean = new MQEventHandlerFactoryBean();
factoryBean.setHandlers(handlers);
factoryBean.setHandlerChainDefinitions(
        "/Topic-DC-Output/TagA-Output/** = inDbPostHandler\n" +
        "/Topic-DC-Output/TagB-Output/** = smsPostHandler");

EventHandler<RocketmqEvent> eventHandler = factoryBean.getObject();
```

### 6.2 非 Spring 用户

直接装配 core，无需任何 Spring 依赖：

```java
// 1) Handler
Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
handlers.put("inDbPostHandler", new InDbPostHandler());

// 2) 构建责任链管理器（core，无 Spring）
DefaultHandlerChainManager<RocketmqEvent> manager = new DefaultHandlerChainManager<>();
manager.addHandler("inDbPostHandler", new InDbPostHandler());
manager.createChain("/Topic-DC-Output/TagA-Output/**", "inDbPostHandler");

// 3) 构建路径解析器 + 链分发器（core）
PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
resolver.setHandlerChainManager(manager);

// 4) 消费消息（仅依赖 rocketmq-client）
MessageExt msgExt = /* 从 broker 拉取 */;
DefaultRocketmqEvent event = new DefaultRocketmqEvent(msgExt);
HandlerChain<RocketmqEvent> chain = resolver.getChain(event, new ProxiedHandlerChain());
chain.execute(event);
```

## 7. 配置

| 配置项                                | 配置方式                                                  | 默认值                                  |
| :------------------------------------ | :------------------------------------------------------- | :-------------------------------------- |
| Namesrv 地址                          | `ProducerConfig` / `ConsumerConfig`（继承 `ClientConfig`） | —                                     |
| 生产者分组 / 超时                     | `ProducerConfig`（`producerGroup`、`sendMsgTimeout`、`compressMsgBodyOverHowmuch` 等） | 3000 ms / 4 KiB |
| 消费者分组 / 模型 / 起始位置           | `ConsumerConfig`（`consumerGroup`、`messageModel`、consume-from-where、重试次数等） | `CLUSTERING` |
| 责任链定义                             | `MQEventHandlerFactoryBean.setHandlerChainDefinitions(String)`（INI 格式，`[urls]` 段） | —      |
| 队列分配策略                           | `MQPushConsumerFactoryBean.setAllocateMessageQueueStrategy(...)` | `AllocateMessageQueueConsistentHash` |
| 消费失败重试                           | `ConsumerConfig.retryTimesWhenConsumeFailed`             | —                                       |

## 8. 核心用法 / API

### 8.1 发送

`RocketmqTemplate`（spring 模块）—— 所有方法都委托给注入的 `MQProducer`：

```java
rocketmqTemplate.send(msg);                            // 同步
rocketmqTemplate.send(msg, sendCallback);              // 异步
rocketmqTemplate.sendOneway(msg);                      // 单向
rocketmqTemplate.send(msgs);                           // 批量
rocketmqTemplate.send(msg, rocketmqTemplate.HASH_SELECTOR, orderId); // 按 key hash 有序
rocketmqTemplate.sendMessageInTransaction(msg, tranExecuter, arg);   // 事务
```

如果不使用 Spring，直接调用 rocketmq-client 的 `MQProducer` 即可 —— 本库在生产端的价值主要是 Spring 装配。

### 8.2 责任链内部组件

| 类型                                       | 角色                                                       | 模块   |
| :----------------------------------------- | :--------------------------------------------------------- | :----- |
| `RocketmqEvent`（接口）                    | 数据载体 —— `messageExt` / `topic` / `tag` / `body` / `routeExpression` | core |
| `DefaultRocketmqEvent`                     | `RocketmqEvent` 的 POJO 实现                                | core |
| `SpringRocketmqEvent`                      | Spring 适配器 —— 继承 `ApplicationEvent`，实现 `RocketmqEvent`，委托给包装的 `RocketmqEvent` | spring |
| `EventHandler<T>`（接口）                  | 单 Handler SPI；`<T>` 绑定到 `RocketmqEvent`               | core |
| `AbstractRouteableMessageHandler<T>`       | 解析并执行注册的责任链；将失败包装为 `EventHandleException` | core |
| `AbstractPathMatchMessageHandler<T>`       | 将事件路径表达式与 `Ant` 模式匹配后再委托（路径匹配器来自 disruptor-extension） | core |
| `AbstractAdviceMessageHandler<T>`          | `preHandle` / `postHandle` / `cleanup` 模板方法钩子        | core |
| `DefaultHandlerChainManager`               | 注册 Handler 并从定义构建责任链                             | core |
| `PathMatchingHandlerChainResolver`         | 将 `/Topic/Tags/Keys` 表达式匹配到责任链                    | core |
| `ProxiedHandlerChain`                      | 默认 `HandlerChain` 实现                                   | core |
| `RocketmqEventMessageHandler`              | 从 `MessageExt` 到 `DefaultRocketmqEvent` 的桥接 + 链执行    | spring |
| `ApplicationEventMessageHandler`           | 将 `DefaultRocketmqEvent` 包装为 `SpringRocketmqEvent` 并通过 Spring `ApplicationEventPublisher` 发布 | spring |
| `DisruptorEventMessageHandler`             | 将 `RocketmqDisruptorEvent` 发布到 Disruptor 环形缓冲区     | spring |

### 8.3 `RocketmqEvent` 契约

```java
public interface RocketmqEvent {
    MessageExt getMessageExt();   void setMessageExt(MessageExt);
    String getTopic();             void setTopic(String);
    String getTag();               void setTag(String);
    byte[] getBody();              void setBody(byte[]);
    String getMsgBody();           // UTF-8
    String getMsgBody(String charset);
    String getRouteExpression();   // /Topic/Tags/Keys
    void    setRouteExpression(String);
}
```

Spring 用户收到 `SpringRocketmqEvent`，可以调用 `getDelegate()` 解包到底层 `DefaultRocketmqEvent`。

## 9. 测试与构建

```bash
# 完整 reactor 构建（两模块全 436 个测试）
mvn clean verify

# 仅构建单一模块
mvn -pl rocketmq-extension-core test
mvn -pl rocketmq-extension-spring test
```

- JaCoCo 在两模块都配置了 **90%** 行覆盖率规则（`haltOnFailure=false`）。
- `maven-enforcer-plugin` 在 `rocketmq-extension-core` 上强制**零 Spring 依赖**（`org.springframework:*`、`org.springframework.*:*`、`org.springframework.boot:*` 全部禁用）。
- 两模块独立产出 jar；`rocketmq-extension-spring` 传递依赖 core。

### 9.1 示例程序

两模块都附带可运行的示例程序：

- `rocketmq-extension-core/src/test/java/org/apache/rocketmq/client/` —— 纯 rocketmq-client 示例
  （`SimpleProducer`、`SimpleAsyncProducer`、`SimpleOnewayProducer`、`SimpleSyncProducer`、
  `BatchProducer`、`OrderedProducer`、`BroadcastProducer`、`ScheduledMessageProducer`、
  `SimpleConsumer`、`OrderedConsumer`、`BroadcastConsumer`、`ScheduledMessageConsumer`、
  `ListSplitter`）—— 需要运行中的 RocketMQ broker（默认 `127.0.0.1:9876`）。
- `rocketmq-extension-spring/src/test/java/org/apache/rocketmq/client/` —— Spring 装配示例
  （`SimpleProducer`、`BatchProducer`）展示 `FactoryBean` 和 `MQProducerShutdownHook` 的用法。

## 10. 版本线与分支

| 分支                | JDK | Maven       | Spring | 版本模式                            | disruptor-extension      |
| :------------------ | :-- | :---------- | :----- | :---------------------------------- | :----------------------- |
| `feature/1.0.x`    | 8   | 3.9.16      | 5.3.39 | `1.0.x.20260630-SNAPSHOT`           | `1.0.x.20260630-SNAPSHOT` |
| `feature/2.0.x`    | 17  | 3.9.16      | 6.2.19 | `2.0.x.20260630-SNAPSHOT`           | `2.0.x.20260630-SNAPSHOT` |
| **`feature/3.0.x`** | **21** | **4.0.0-rc-5** | **7.0.8** | **`3.0.x.20260630-SNAPSHOT`** | **`3.0.x.20260630-SNAPSHOT`** |

三条线**独立维护** —— 在 JDK 8 线上的安全修复**不会**自动传播到 JDK 17/21 线。每个分支独立发版。

Dependabot 漏洞状态：

| 分支                | 剩余 alert 数 | 备注                                                              |
| :------------------ | :------------ | :---------------------------------------------------------------- |
| `feature/1.0.x`    | 2（low）      | Spring `AntPathMatcher` ReDoS —— 仅在 Spring 6.x+ 修复。在 JDK 8 线上接受为 wontfix，或升级到 2.0.x / 3.0.x。 |
| `feature/2.0.x`    | 0             | 已清零。                                                          |
| **`feature/3.0.x`** | **0**         | **已清零。**                                                       |

## 11. 贡献与许可

欢迎贡献 —— 特别是责任链、`Ini` 解析器的单元测试，以及端到端的 RocketMQ 集成测试（项目当前
刻意只附单元测试）。大规模改动前请先开 issue。

本项目采用 [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0) 许可。