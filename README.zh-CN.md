# rocketmq-extension

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-17-orange)](https://github.com/easy-4-java/rocketmq-extension) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

rocketmq-extension 是基于 RocketMQ 客户端（4.5.x）实现的轻量业务封装，简化消息的发布与订阅，并支持多种消费方式：

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
| Spring 事件消费          | 将每条消息以 Spring `ApplicationEvent`（`RocketmqEvent`）发布          |

| 是                                                     | 不是                                          |
| :----------------------------------------------------- | :-------------------------------------------- |
| RocketMQ 客户端的业务封装                               | RocketMQ Broker / 客户端的替代品              |
| 路径式 Handler 链（`/Topic/Tags/Keys = handler`）       | 通用 ESB 或消息网关                           |
| 面向 Producer/Consumer 的 Spring `FactoryBean` 接线     | Spring Boot Starter                           |

典型场景：

| 场景                     | 说明                                                          |
| :----------------------- | :------------------------------------------------------------ |
| 数据中心数据接入         | 一个 Topic 多个 Tag，各有专责 Handler 处理                    |
| 订单/短信/通知路由       | `/Order/TagCreated/**` 交给一个 Handler，`/Order/TagPaid/**` 交给另一个 |
| 高吞吐异步消费           | 基于 Disruptor 环形缓冲处理突发流量                           |
| 事件驱动集成             | 同一 Spring 上下文中的 ApplicationEvent 监听器处理            |

## 2. 功能与状态

| 能力                                             | 状态       | 主要 API                                                              |
| :----------------------------------------------- | :--------- | :-------------------------------------------------------------------- |
| 发布封装（同步/异步/单向/批量/顺序/事务）         | 已实现     | `RocketmqTemplate` —— 覆盖 `MQProducer` 各发送变体；选择器（`HASH_SELECTOR`、`RANDOOM_SELECTOR`、`Machine_RANDOOM_SELECTOR`） |
| 消费者工厂                                       | 已实现     | `MQPushConsumerFactoryBean`（`ConsumerConfig`、监听器、Offset 存储、队列分配策略） |
| 生产者工厂                                       | 已实现     | `MQProducerFactoryBean`（`ProducerConfig`，默认 `DefaultTransactionCheckListener`） |
| 带重试的消费                                      | 已实现     | `DefaultMessageConsumeListener` —— 通过 `RECONSUME_LATER` 重试，上限 `retryTimesWhenConsumeFailed` |
| 责任链路由                                       | 已实现     | `MQEventHandlerFactoryBean` + `DefaultHandlerChainManager` + `PathMatchingHandlerChainResolver`；路径表达式 `/Topic/Tags/Keys` |
| Disruptor 异步消费                               | 已实现     | `RocketmqDisruptorEvent`、`RocketmqDataEventFactory` / `RocketmqDataEventTranslator`（依赖 `disruptor-extension`） |
| Spring ApplicationEvent 消费                     | 已实现     | `RocketmqEvent`（继承 `ApplicationEvent`）+ `ApplicationEventMessageHandler` |
| 关闭钩子                                         | 已实现     | `MQProducerShutdownHook`、`MQPushConsumerShutdownHook`               |
| INI 格式链定义                                   | 已实现     | `config.Ini` 解析器；事件处理器工厂的 `setHandlerChainDefinitions("...")` |
| 单元测试                                         | 部分       | `src/test` 为可运行示例（`SimpleProducer`、`SimpleConsumer` 等）；无 JUnit `@Test` 类 |

## 3. 环境要求与兼容性

| 要求         | 版本                                         |
| :----------- | :------------------------------------------- |
| JDK          | 8+                                           |
| Maven        | 3.0+（已内置 wrapper）                        |
| RocketMQ     | 4.5.2（`rocketmq-client`、`rocketmq-common`）|
| Spring       | 4.3.11.RELEASE（`spring-beans`/`context`/`core`）|
| disruptor-extension| `2.0.x.20260630-SNAPSHOT`（同一版本线）         |

easy4j 项目的版本线：

| 分支           | JDK  | 版本模式   | 说明                            |
| :------------- | :--- | :--------- | :------------------------------ |
| `feature/1.0.x` | 8    | `1.0.x.*`  | 本文档对应分支                   |
| `feature/2.0.x` | 17   | `2.0.x.*`  | JDK 17 版本线                   |
| `feature/3.0.x` | 21   | `3.0.x.*`  | JDK 21 版本线                   |

## 4. 架构与模块

```text
 生产者路径                           消费者路径
      |                                      |
      v                                      v
 RocketmqTemplate ----------------> MQPushConsumerFactoryBean
 (send/oneway/batch/tx)                  |
                                         v
                              DefaultMessageConsumeListener (重试)
                                         |
                                         v
                              MQEventHandlerFactoryBean
                              /Topic/Tags/Keys = handler1, handler2
                              |               |           |
                              v               v           v
                        RocketmqEventMessageHandler
                              |               |           |
                    ApplicationEvent     Disruptor     普通 Handler
                    (Spring 事件)    (disruptor-extension)
```

单模块 Maven 项目（`jar` 打包），根包 `org.apache.rocketmq.client.extension`：

| 包                          | 职责                                              |
| :-------------------------- | :------------------------------------------------ |
| `config`                    | `ProducerConfig`、`ConsumerConfig`、`Ini` 解析器  |
| `factory`                   | `MQProducerFactoryBean`、`MQPushConsumerFactoryBean`、`MQEventHandlerFactoryBean` |
| `listener`                  | `DefaultMessageConsumeListener`、`DefaultTransactionCheckListener` |
| `event` + `event.handler`   | `RocketmqEvent`、责任链框架与各 Handler 实现      |
| `disruptor`                 | `RocketmqDataEventFactory`、`RocketmqDataEventTranslator` |
| `hooks`                     | 生产者/消费者关闭钩子                             |
| `exception` / `util`        | `RocketMQException`、`EventHandleException`、`StringUtils` |

## 5. 安装

制品发布在阿里云私服与 GitHub Releases，**尚未发布到 Maven Central**。

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>rocketmq-extension</artifactId>
    <version>2.0.x.20260630-SNAPSHOT</version>
</dependency>
```

```groovy
implementation 'io.github.easy4j:rocketmq-extension:2.0.x.20260630-SNAPSHOT'
```

## 6. 快速开始

使用 `RocketmqTemplate` 发布消息：

```java
@Autowired
private RocketmqTemplate rocketmqTemplate;

public void publish() throws Exception {
    SendResult result = rocketmqTemplate.send(
            "Topic-DC-Output",   // topic
            "TagA-Output",       // tags
            "OrderID001",        // keys（业务唯一性标识）
            "hello rocketmq");   // body
}
```

基于责任链的消费：

```java
// 1) 处理器
Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<String, EventHandler<RocketmqEvent>>();
handlers.put("inDbPostHandler", new InDbPostHandler());
handlers.put("smsPostHandler", new SmsPostHandler());

// 2) 路由表达式：/Topic/Tags/Keys = handler
MQEventHandlerFactoryBean factoryBean = new MQEventHandlerFactoryBean();
factoryBean.setHandlers(handlers);
factoryBean.setHandlerChainDefinitions(
        "/Topic-DC-Output/TagA-Output/** = inDbPostHandler\n" +
        "/Topic-DC-Output/TagB-Output/** = smsPostHandler");

EventHandler<RocketmqEvent> eventHandler = factoryBean.getObject();
```

预期结果：`Topic=Topic-DC-Output, Tags=TagA-Output`（任意 Keys）的消息交给 `inDbPostHandler` 处理；`TagB-Output` 消息交给 `smsPostHandler`。`/**` 后缀匹配任意 `Keys` 值，因此各系统推入同一 Topic 的消息可被路由到各自专责的处理逻辑。

## 7. 配置

| 配置项                 | 设置方式                                                         | 默认值                                  |
| :--------------------- | :--------------------------------------------------------------- | :-------------------------------------- |
| Namesrv 地址           | `ProducerConfig` / `ConsumerConfig`（继承 `ClientConfig`）       | —                                       |
| 生产者组 / 超时        | `ProducerConfig`（`producerGroup`、`sendMsgTimeout`、`compressMsgBodyOverHowmuch` 等） | 3000 ms / 4 KiB |
| 消费者组 / 模式 / 起点 | `ConsumerConfig`（`consumerGroup`、`messageModel`、消费起点、重试次数等） | `CLUSTERING`              |
| 链定义                 | `MQEventHandlerFactoryBean.setHandlerChainDefinitions(String)`（INI 格式，`[urls]` 段） | —           |
| 队列分配策略           | `MQPushConsumerFactoryBean.setAllocateMessageQueueStrategy(...)` | `AllocateMessageQueueConsistentHash`    |
| 失败重试               | `ConsumerConfig.retryTimesWhenConsumeFailed`                    | —                                       |

## 8. 核心用法 / API

`RocketmqTemplate` 发送变体（全部委托给注入的 `MQProducer`）：

```java
rocketmqTemplate.send(msg);                            // 同步
rocketmqTemplate.send(msg, sendCallback);              // 异步
rocketmqTemplate.sendOneway(msg);                      // 单向（不等待结果）
rocketmqTemplate.send(msgs);                           // 批量
rocketmqTemplate.send(msg, rocketmqTemplate.HASH_SELECTOR, orderId); // 按 key 哈希顺序发送
rocketmqTemplate.sendMessageInTransaction(msg, tranExecuter, arg);   // 事务消息
```

责任链内部结构：

| 类型                                       | 职责                                                     |
| :----------------------------------------- | :------------------------------------------------------- |
| `DefaultHandlerChainManager`               | 注册处理器并根据定义构建链                               |
| `PathMatchingHandlerChainResolver`         | 将 `/Topic/Tags/Keys` 表达式匹配到对应链                 |
| `AbstractRouteableMessageHandler`          | 执行解析出的链，失败包装为 `EventHandleException`         |
| `RocketmqEventMessageHandler`              | 从 `MessageExt` 到 `RocketmqEvent` 的桥接 + 链执行       |
| `ApplicationEventMessageHandler`           | 将 `RocketmqEvent` 发布到 Spring 上下文                  |
| `DisruptorEventMessageHandler`             | 将 `RocketmqDisruptorEvent` 发布到 Disruptor 流水线      |

## 9. 测试与构建

```bash
./mvnw clean verify
```

- 仓库内置 Maven wrapper（`mvnw`）。
- 已配置 JaCoCo 行覆盖率 90% 门禁（`haltOnFailure=false`）。
- `src/test` 提供可运行示例程序（如 `SimpleProducer`、`SimpleSyncProducer`、`SimpleAsyncProducer`、`SimpleOnewayProducer`、`BatchProducer`、`OrderedProducer`、`ScheduledMessageProducer`、`BroadcastProducer`、`SimpleConsumer`、`OrderedConsumer`、`BroadcastConsumer`、`ScheduledMessageConsumer`、`ListSplitter`）——这些示例需要可用的 RocketMQ Broker（默认 `127.0.0.1:9876`），不是 JUnit 测试。

## 10. 版本线与分支

| 分支           | JDK  | 版本模式   | 维护说明                              |
| :------------- | :--- | :--------- | :------------------------------------ |
| `feature/1.0.x` | 8    | `1.0.x.*`  | RocketMQ 4.5.x                       |
| `feature/2.0.x` | 17   | `2.0.x.*`  | 当前分支（RocketMQ 4.5.x）            |
| `feature/3.0.x` | 21   | `3.0.x.*`  | JDK 21 版本线                         |

制品通过阿里云 Maven 私服与 GitHub Releases 分发。请按 JDK 基线选择对应分支。

## 11. 贡献与许可

欢迎贡献——尤其是责任链与 `Ini` 解析器的 JUnit 测试。较大改动请先提交 issue 讨论。

本项目基于 [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0) 许可。
