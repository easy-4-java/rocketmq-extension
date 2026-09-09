# rocketmq-extension

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-8-orange)](https://github.com/easy-4-java/rocketmq-extension) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

rocketmq-extension is a thin business wrapper around the RocketMQ client (4.5.x) that simplifies message

## Table of Contents

- [1. Project Overview](#1-project-overview)
- [2. Features & Status](#2-features--status)
- [3. Requirements & Compatibility](#3-requirements--compatibility)
- [4. Architecture & Modules](#4-architecture--modules)
- [5. Installation](#5-installation)
- [6. Quick Start](#6-quick-start)
- [7. Configuration](#7-configuration)
- [8. Core Usage / API](#8-core-usage--api)
- [9. Testing & Build](#9-testing--build)
- [10. Versioning & Branches](#10-versioning--branches)
- [11. Contributing & License](#11-contributing--license)

## 1. Project Overview

**rocketmq-extension** is a thin business wrapper around the RocketMQ client (4.5.x) that simplifies message
publishing and subscription, with several consumption styles:

| Style                                   | Mechanism                                                                 |
| :-------------------------------------- | :----------------------------------------------------------------------- |
| Plain consumption                       | `DefaultMessageConsumeListener` + retry on `RECONSUME_LATER`             |
| Responsibility-chain consumption        | Route each message to the right handler by `Topic / Tags / Keys` (path-style expressions) |
| Asynchronous consumption                | Disruptor-backed event dispatch (via `io.github.easy4j:disruptor-extension`)    |
| Spring event consumption                | Publish each message as a Spring `ApplicationEvent` (`RocketmqEvent`)     |

| Is                                                     | Is not                                          |
| :----------------------------------------------------- | :---------------------------------------------- |
| A business wrapper around the RocketMQ client          | A replacement for the RocketMQ broker/client    |
| Path-routed handler chains (`/Topic/Tags/Keys = handler`) | A general-purpose ESB or message gateway      |
| Spring `FactoryBean` wiring for producer/consumer      | A Spring Boot starter                           |

Typical scenarios:

| Scenario                              | Description                                                       |
| :------------------------------------ | :---------------------------------------------------------------- |
| Data center ingestion                 | One topic, many tags — each handled by a dedicated handler        |
| Order / SMS / notification routing    | `/Order/TagCreated/**` → one handler, `/Order/TagPaid/**` → another |
| High-throughput async consumption     | Disruptor ring-buffer based handling for bursty traffic           |
| Event-driven integration              | ApplicationEvent listeners in the same Spring context             |

## 2. Features & Status

| Capability                                     | Status      | Main API                                                                      |
| :--------------------------------------------- | :---------- | :---------------------------------------------------------------------------- |
| Publish wrappers (sync/async/oneway/batch/ordered/transaction) | Implemented | `RocketmqTemplate` — mirrors `MQProducer` send variants; selectors (`HASH_SELECTOR`, `RANDOOM_SELECTOR`, `Machine_RANDOOM_SELECTOR`) |
| Consumer factory                               | Implemented | `MQPushConsumerFactoryBean` (`ConsumerConfig`, listener, offset store, queue allocation strategy) |
| Producer factory                               | Implemented | `MQProducerFactoryBean` (`ProducerConfig`, default `DefaultTransactionCheckListener`) |
| Retry-aware consumption                        | Implemented | `DefaultMessageConsumeListener` — retries via `RECONSUME_LATER` up to `retryTimesWhenConsumeFailed` |
| Responsibility-chain routing                  | Implemented | `MQEventHandlerFactoryBean` + `DefaultHandlerChainManager` + `PathMatchingHandlerChainResolver`; path expressions `/Topic/Tags/Keys` |
| Disruptor async consumption                    | Implemented | `RocketmqDisruptorEvent`, `RocketmqDataEventFactory` / `RocketmqDataEventTranslator` (depends on `disruptor-extension`) |
| Spring ApplicationEvent consumption            | Implemented | `RocketmqEvent` (extends `ApplicationEvent`) + `ApplicationEventMessageHandler` |
| Shutdown hooks                                 | Implemented | `MQProducerShutdownHook`, `MQPushConsumerShutdownHook`                       |
| INI-style chain definitions                    | Implemented | `config.Ini` parser; `setHandlerChainDefinitions("...")` on the event-handler factory |
| Unit tests                                     | Partial     | `src/test` contains runnable examples (`SimpleProducer`, `SimpleConsumer`, ...); no JUnit `@Test` classes |

## 3. Requirements & Compatibility

| Requirement   | Version                                      |
| :------------ | :------------------------------------------- |
| JDK           | 8+                                           |
| Maven         | 3.0+ (wrapper included)                      |
| RocketMQ      | 4.5.2 (`rocketmq-client`, `rocketmq-common`) |
| Spring        | 4.3.11.RELEASE (`spring-beans`/`context`/`core`) |
| disruptor-extension | `1.0.x.20260630-SNAPSHOT` (same line)        |

Version lines of the easy4j project:

| Branch        | JDK  | Version pattern | Notes                       |
| :------------ | :--- | :-------------- | :-------------------------- |
| `feature/1.0.x` | 8    | `1.0.x.*`       | This README, current branch |
| `feature/2.0.x` | 17   | `2.0.x.*`       | JDK 17 line                 |
| `feature/3.0.x` | 21   | `3.0.x.*`       | JDK 21 line                 |

## 4. Architecture & Modules

```text
 producer path                          consumer path
      |                                      |
      v                                      v
 RocketmqTemplate ----------------> MQPushConsumerFactoryBean
 (send/oneway/batch/tx)                  |
                                         v
                              DefaultMessageConsumeListener (retry)
                                         |
                                         v
                              MQEventHandlerFactoryBean -> HandlerChain
                              /Topic/Tags/Keys = handler1, handler2 ...
                              |               |           |
                              v               v           v
                        RocketmqEventMessageHandler
                              |               |           |
                    ApplicationEvent     Disruptor      plain handlers
                    (Spring events)   (disruptor-extension)
```

Single-module Maven project (`jar` packaging), root package `org.apache.rocketmq.client.extension`:

| Package                    | Responsibility                                    |
| :------------------------- | :------------------------------------------------ |
| `config`                   | `ProducerConfig`, `ConsumerConfig`, `Ini` parser  |
| `factory`                  | `MQProducerFactoryBean`, `MQPushConsumerFactoryBean`, `MQEventHandlerFactoryBean` |
| `listener`                 | `DefaultMessageConsumeListener`, `DefaultTransactionCheckListener` |
| `event` + `event.handler`  | `RocketmqEvent`, chain framework and handler implementations |
| `disruptor`                | `RocketmqDataEventFactory`, `RocketmqDataEventTranslator` |
| `hooks`                    | Producer/consumer shutdown hooks                  |
| `exception` / `util`       | `RocketMQException`, `EventHandleException`, `StringUtils` |

## 5. Installation

Artifacts are published to the aliyun repository and GitHub Releases; they are **not** on Maven Central yet.

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>rocketmq-extension</artifactId>
    <version>1.0.x.20260630-SNAPSHOT</version>
</dependency>
```

```groovy
implementation 'io.github.easy4j:rocketmq-extension:1.0.x.20260630-SNAPSHOT'
```

## 6. Quick Start

Publishing with `RocketmqTemplate`:

```java
@Autowired
private RocketmqTemplate rocketmqTemplate;

public void publish() throws Exception {
    SendResult result = rocketmqTemplate.send(
            "Topic-DC-Output",   // topic
            "TagA-Output",       // tags
            "OrderID001",        // keys (business uniqueness)
            "hello rocketmq");   // body
}
```

Consumption with a responsibility chain:

```java
// 1) handlers
Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<String, EventHandler<RocketmqEvent>>();
handlers.put("inDbPostHandler", new InDbPostHandler());
handlers.put("smsPostHandler", new SmsPostHandler());

// 2) route expressions: /Topic/Tags/Keys = handler(s)
MQEventHandlerFactoryBean factoryBean = new MQEventHandlerFactoryBean();
factoryBean.setHandlers(handlers);
factoryBean.setHandlerChainDefinitions(
        "/Topic-DC-Output/TagA-Output/** = inDbPostHandler\n" +
        "/Topic-DC-Output/TagB-Output/** = smsPostHandler");

EventHandler<RocketmqEvent> eventHandler = factoryBean.getObject();
```

Expected result: a message with `Topic=Topic-DC-Output, Tags=TagA-Output` and any key is delivered to
`inDbPostHandler`; a `TagB-Output` message goes to `smsPostHandler`. The `/**` suffix matches any `Keys`
value, so different systems pushing into the same topic are routed to dedicated processing logic.

## 7. Configuration

| Setting                                | How                                                      | Default                                  |
| :------------------------------------- | :------------------------------------------------------- | :--------------------------------------- |
| Namesrv address                        | `ProducerConfig` / `ConsumerConfig` (extends `ClientConfig`) | —                                    |
| Producer group / timeouts              | `ProducerConfig` (`producerGroup`, `sendMsgTimeout`, `compressMsgBodyOverHowmuch`, ...) | 3000 ms / 4 KiB |
| Consumer group / model / from-where    | `ConsumerConfig` (`consumerGroup`, `messageModel`, consume-from-where, retry count, ...) | `CLUSTERING` |
| Chain definitions                      | `MQEventHandlerFactoryBean.setHandlerChainDefinitions(String)` (INI format, `[urls]` section) | —      |
| Queue allocation strategy              | `MQPushConsumerFactoryBean.setAllocateMessageQueueStrategy(...)` | `AllocateMessageQueueConsistentHash` |
| Retry on failure                       | `ConsumerConfig.retryTimesWhenConsumeFailed`             | —                                        |

## 8. Core Usage / API

Sending variants on `RocketmqTemplate` (all delegate to the injected `MQProducer`):

```java
rocketmqTemplate.send(msg);                            // sync
rocketmqTemplate.send(msg, sendCallback);              // async
rocketmqTemplate.sendOneway(msg);                      // fire-and-forget
rocketmqTemplate.send(msgs);                           // batch
rocketmqTemplate.send(msg, rocketmqTemplate.HASH_SELECTOR, orderId); // ordered by key hash
rocketmqTemplate.sendMessageInTransaction(msg, tranExecuter, arg);   // transactional
```

Handler chain internals:

| Type                                       | Role                                                     |
| :----------------------------------------- | :------------------------------------------------------- |
| `DefaultHandlerChainManager`               | Registers handlers and builds chains from definitions    |
| `PathMatchingHandlerChainResolver`         | Matches `/Topic/Tags/Keys` expressions to a chain        |
| `AbstractRouteableMessageHandler`          | Executes the resolved chain, wraps failures in `EventHandleException` |
| `RocketmqEventMessageHandler`              | Bridge from `MessageExt` to `RocketmqEvent` + chain      |
| `ApplicationEventMessageHandler`           | Publishes `RocketmqEvent` into the Spring context        |
| `DisruptorEventMessageHandler`             | Publishes `RocketmqDisruptorEvent` into the Disruptor pipeline |

## 9. Testing & Build

```bash
./mvnw clean verify
```

- Maven wrapper (`mvnw`) is committed to the repository.
- JaCoCo is configured with a line-coverage rule of 90% (`haltOnFailure=false`).
- `src/test` ships runnable example programs (e.g. `SimpleProducer`, `SimpleSyncProducer`,
  `SimpleAsyncProducer`, `SimpleOnewayProducer`, `BatchProducer`, `OrderedProducer`,
  `ScheduledMessageProducer`, `BroadcastProducer`, `SimpleConsumer`, `OrderedConsumer`,
  `BroadcastConsumer`, `ScheduledMessageConsumer`, `ListSplitter`) — these require a running RocketMQ
  broker (default `127.0.0.1:9876`) and are not JUnit tests.

## 10. Versioning & Branches

| Branch        | JDK  | Version pattern | Maintenance                          |
| :------------ | :--- | :-------------- | :----------------------------------- |
| `feature/1.0.x` | 8    | `1.0.x.*`       | Current branch (RocketMQ 4.5.x)      |
| `feature/2.0.x` | 17   | `2.0.x.*`       | JDK 17 line                          |
| `feature/3.0.x` | 21   | `3.0.x.*`       | JDK 21 line                          |

Artifacts are distributed via the aliyun Maven repository and GitHub Releases. Use the branch matching your
JDK baseline.

## 11. Contributing & License

Contributions are welcome — especially JUnit tests for the handler chain and the `Ini` parser. Please open an
issue before larger changes.

This project is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
