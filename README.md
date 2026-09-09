# rocketmq-extension

[English](./README.md) | [简体中文](./README.zh-CN.md)

[![Java](https://img.shields.io/badge/Java-8-orange)](https://github.com/easy-4-java/rocketmq-extension) [![License](https://img.shields.io/badge/license-Apache%202.0-green)](./LICENSE)

`rocketmq-extension` is a thin business wrapper around the RocketMQ client (4.5.x) that simplifies message
publishing and subscription. It is split into two modules so non-Spring runtimes can depend only on
the pure-Java core:

| Module                            | Depends on Spring? | Purpose                                                                    |
| :-------------------------------- | :------------------ | :------------------------------------------------------------------------- |
| `rocketmq-extension-core`        | **No**              | Envelopes, responsibility-chain router, Disruptor adapter, INI parser, SPI abstractions |
| `rocketmq-extension-spring`      | Yes (transitively)  | `FactoryBean`s for producer/consumer/chain dispatcher, Spring `ApplicationEvent` adapter, `RocketmqTemplate`, lifecycle hooks |

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

**rocketmq-extension** is a thin business wrapper around the RocketMQ client (4.5.x) that simplifies
message publishing and subscription, with several consumption styles:

| Style                                   | Mechanism                                                                 |
| :-------------------------------------- | :----------------------------------------------------------------------- |
| Plain consumption                       | `DefaultMessageConsumeListener` + retry on `RECONSUME_LATER`             |
| Responsibility-chain consumption        | Route each message to the right handler by `Topic / Tags / Keys` (path-style expressions) |
| Asynchronous consumption                | Disruptor-backed event dispatch (via `io.github.easy4j:disruptor-extension`) |
| Spring event consumption                | Publish each message as a Spring `ApplicationEvent` (`SpringRocketmqEvent`) |

| Is                                                     | Is not                                          |
| :----------------------------------------------------- | :---------------------------------------------- |
| A business wrapper around the RocketMQ client          | A replacement for the RocketMQ broker/client    |
| Path-routed handler chains (`/Topic/Tags/Keys = handler`) | A general-purpose ESB or message gateway      |
| Spring `FactoryBean` wiring for producer/consumer      | A Spring Boot starter                           |
| Two-module split: pure-Java core + Spring adapter      | Requires Spring at the core layer              |

Typical scenarios:

| Scenario                              | Description                                                       |
| :------------------------------------ | :---------------------------------------------------------------- |
| Data center ingestion                 | One topic, many tags — each handled by a dedicated handler        |
| Order / SMS / notification routing    | `/Order/TagCreated/**` → one handler, `/Order/TagPaid/**` → another |
| High-throughput async consumption     | Disruptor ring-buffer based handling for bursty traffic           |
| Event-driven integration              | `ApplicationEvent` listeners in the same Spring context           |

## 2. Features & Status

| Capability                                     | Status      | Main API                                                                      | Module        |
| :--------------------------------------------- | :---------- | :---------------------------------------------------------------------------- | :------------ |
| Publish wrappers (sync/async/oneway/batch/ordered/transaction) | Implemented | `RocketmqTemplate` — mirrors `MQProducer` send variants; selectors (`HASH_SELECTOR`, `RANDOOM_SELECTOR`, `Machine_RANDOOM_SELECTOR`) | spring |
| Consumer factory                               | Implemented | `MQPushConsumerFactoryBean` (`ConsumerConfig`, listener, offset store, queue allocation strategy) | spring |
| Producer factory                               | Implemented | `MQProducerFactoryBean` (`ProducerConfig`, default `DefaultTransactionCheckListener`) | spring |
| Retry-aware consumption                        | Implemented | `DefaultMessageConsumeListener` — retries via `RECONSUME_LATER` up to `retryTimesWhenConsumeFailed` | core |
| Responsibility-chain routing                  | Implemented | `MQEventHandlerFactoryBean` + `DefaultHandlerChainManager` + `PathMatchingHandlerChainResolver`; path expressions `/Topic/Tags/Keys` | spring + core |
| Disruptor async consumption                    | Implemented | `RocketmqDisruptorEvent`, `RocketmqDataEventFactory` / `RocketmqDataEventTranslator` (depends on `disruptor-extension`) | core |
| Spring ApplicationEvent consumption            | Implemented | `SpringRocketmqEvent` (extends `ApplicationEvent`, wraps a `RocketmqEvent`) + `ApplicationEventMessageHandler` | spring |
| Shutdown hooks                                 | Implemented | `MQProducerShutdownHook`, `MQPushConsumerShutdownHook`                       | spring |
| INI-style chain definitions                    | Implemented | `config.Ini` parser; `setHandlerChainDefinitions("...")` on the event-handler factory | core |
| Unit tests                                     | Implemented | JUnit Jupiter / JUnit 4 — 436 tests across both modules (core 400 + spring 36) | both   |

## 3. Requirements & Compatibility

**This branch: `feature/3.0.x` — JDK 21, Maven 4.0.0-rc-5.**

| Requirement            | 1.0.x                                | 2.0.x                                | **3.0.x (this)**                     |
| :--------------------- | :----------------------------------- | :----------------------------------- | :----------------------------------- |
| JDK                    | 8                                    | 17                                   | 21                                   |
| Maven                  | 3.9.16                               | 3.9.16                               | 4.0.0-rc-5                           |
| RocketMQ               | 4.5.2 (`rocketmq-client`, `rocketmq-common`) | 4.5.2                          | 4.5.2                                |
| Spring Framework       | 5.3.39 (final 5.3.x; JDK 8 line)     | 6.2.19                               | **7.0.9**                            |
| disruptor-extension    | 1.0.x.20260630-SNAPSHOT             | 2.0.x.20260630-SNAPSHOT              | 3.0.x.20260630-SNAPSHOT              |
| junit-jupiter          | 5.11.4                               | 6.1.0                                | 6.1.0                                |

**Hard rule** (enforced by `maven-enforcer-plugin` on `rocketmq-extension-core`): the core module
contains zero Spring dependencies. Spring-only code lives in `rocketmq-extension-spring`. The path
matcher (`AntPathMatcher`) is sourced from `com.lmax.disruptor.util.AntPathMatcher` (pure Java,
shipped by `disruptor-extension`) so the core does not pull in `spring-core`.

## 4. Architecture & Modules

```text
                   producer path                              consumer path
                         |                                         |
                         v                                         v
                 RocketmqTemplate ------------------> MQPushConsumerFactoryBean
                  (send/oneway/batch/tx)                              |
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
                                          (DefaultRocketmqEvent)       (Disruptor)   plain handlers
                                                          |
                                            +----- SpringRocketmqEvent
                                            |     (extends ApplicationEvent,
                                            |      implements RocketmqEvent)
                                            v
                              ApplicationEventPublisher
                                            |
                                            v
                            Spring ApplicationContext listeners
```

Multi-module Maven project, `packaging=pom` at root:

| Module / Package                          | Responsibility                                                                  |
| :--------------------------------------- | :------------------------------------------------------------------------------ |
| `org.apache.rocketmq.client.extension` (root) | Parent pom — revision + dependencyManagement + plugins. Single deployable unit. |
| `org.apache.rocketmq.client.extension` (`core`) | RocketMQ envelopes (`RocketmqEvent` interface + `DefaultRocketmqEvent` POJO), chain framework (`EventHandler`, `HandlerChain*`, `DefaultHandlerChainManager`, `PathMatchingHandlerChainResolver`), Disruptor adapter (`RocketmqDataEventFactory`, `RocketmqDataEventTranslator`, `RocketmqDisruptorEvent`), `Ini` parser, exception types, `StringUtils` helpers, plain `MessageListener` implementations. |
| `org.apache.rocketmq.client.extension.spring` | `RocketmqTemplate` (`@Autowired MQProducer` / `MQPushConsumer`), `FactoryBean`s (`MQProducerFactoryBean`, `MQPushConsumerFactoryBean`, `MQEventHandlerFactoryBean`), `SpringRocketmqEvent` ApplicationEvent adapter, `ApplicationEventMessageHandler` / `DisruptorEventMessageHandler` / `RocketmqEventMessageHandler`, JVM shutdown hooks. |

## 5. Installation

Artifacts are published to the aliyun repository and GitHub Releases; they are **not** on Maven Central yet.

If you use Spring, depend on the spring module — it transitively pulls in core:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>rocketmq-extension-spring</artifactId>
    <version>3.0.x.20260630-SNAPSHOT</version>
</dependency>
```

If you only need the pure-Java core (no Spring on the classpath), depend on core directly:

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>rocketmq-extension-core</artifactId>
    <version>3.0.x.20260630-SNAPSHOT</version>
</dependency>
```

```groovy
// Spring users
implementation 'io.github.easy4j:rocketmq-extension-spring:3.0.x.20260630-SNAPSHOT'

// Non-Spring users
implementation 'io.github.easy4j:rocketmq-extension-core:3.0.x.20260630-SNAPSHOT'
```

## 6. Quick Start

### 6.1 Spring users

Publishing with `RocketmqTemplate` (Spring module):

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

Consumption with a responsibility chain (Spring wires the factory beans, the chain itself is in core):

```java
// 1) handlers (pure Java — any class implementing EventHandler<RocketmqEvent>)
Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
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

### 6.2 Non-Spring users

Wire the core directly — no Spring on the classpath:

```java
// 1) handlers
Map<String, EventHandler<RocketmqEvent>> handlers = new LinkedHashMap<>();
handlers.put("inDbPostHandler", new InDbPostHandler());

// 2) build chain manager (from core, no Spring)
DefaultHandlerChainManager<RocketmqEvent> manager = new DefaultHandlerChainManager<>();
manager.addHandler("inDbPostHandler", new InDbPostHandler());
manager.createChain("/Topic-DC-Output/TagA-Output/**", "inDbPostHandler");

// 3) build path resolver + chain dispatcher (from core)
PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
resolver.setHandlerChainManager(manager);

// 4) consume a message (rocketmq-client only)
MessageExt msgExt = /* pull from broker */;
DefaultRocketmqEvent event = new DefaultRocketmqEvent(msgExt);
HandlerChain<RocketmqEvent> chain = resolver.getChain(event, new ProxiedHandlerChain());
chain.execute(event);
```

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

### 8.1 Sending

`RocketmqTemplate` (spring module) — all delegate to the injected `MQProducer`:

```java
rocketmqTemplate.send(msg);                            // sync
rocketmqTemplate.send(msg, sendCallback);              // async
rocketmqTemplate.sendOneway(msg);                      // fire-and-forget
rocketmqTemplate.send(msgs);                           // batch
rocketmqTemplate.send(msg, rocketmqTemplate.HASH_SELECTOR, orderId); // ordered by key hash
rocketmqTemplate.sendMessageInTransaction(msg, tranExecuter, arg);   // transactional
```

If you are not using Spring, call `MQProducer` from rocketmq-client directly — the extension library
adds no value at the producer side beyond Spring wiring.

### 8.2 Handler chain internals

| Type                                       | Role                                                     | Module |
| :----------------------------------------- | :------------------------------------------------------- | :----- |
| `RocketmqEvent` (interface)                | Data carrier — `topicMessageExt` / `topic` / `tag` / `body` / `routeExpression` | core |
| `DefaultRocketmqEvent`                     | POJO implementation of `RocketmqEvent`                   | core |
| `SpringRocketmqEvent`                      | Spring adapter — extends `ApplicationEvent`, implements `RocketmqEvent`, delegates to a wrapped `RocketmqEvent` | spring |
| `EventHandler<T>` (interface)              | Single-handler SPI; `<T>` bound to `RocketmqEvent`       | core |
| `AbstractRouteableMessageHandler<T>`       | Resolves and executes the registered chain; wraps failures in `EventHandleException` | core |
| `AbstractPathMatchMessageHandler<T>`       | Matches event route expressions against `Ant` patterns before delegating (path matcher from disruptor-extension) | core |
| `AbstractAdviceMessageHandler<T>`          | `preHandle` / `postHandle` / `cleanup` template-method hook | core |
| `DefaultHandlerChainManager`               | Registers handlers and builds chains from definitions    | core |
| `PathMatchingHandlerChainResolver`         | Matches `/Topic/Tags/Keys` expressions to a chain        | core |
| `ProxiedHandlerChain`                      | Default `HandlerChain` implementation                    | core |
| `RocketmqEventMessageHandler`              | Bridge from `MessageExt` to `DefaultRocketmqEvent` + chain execution | spring |
| `ApplicationEventMessageHandler`           | Wraps `DefaultRocketmqEvent` in `SpringRocketmqEvent` and publishes via Spring `ApplicationEventPublisher` | spring |
| `DisruptorEventMessageHandler`             | Publishes `RocketmqDisruptorEvent` into a Disruptor ring buffer | spring |

### 8.3 The `RocketmqEvent` contract

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

Spring users receive a `SpringRocketmqEvent` and can call `getDelegate()` to unwrap the underlying
`DefaultRocketmqEvent`.

## 9. Testing & Build

```bash
# Run the full reactor (both modules, all 436 tests)
mvn clean verify

# Run only one module
mvn -pl rocketmq-extension-core test
mvn -pl rocketmq-extension-spring test
```

- JaCoCo is configured on both modules with a line-coverage rule of **90%** (`haltOnFailure=false`).
- `maven-enforcer-plugin` on `rocketmq-extension-core` enforces **zero Spring dependencies**
  (`org.springframework:*`, `org.springframework.*:*`, `org.springframework.boot:*` are all banned).
- Both modules produce independent jars; `rocketmq-extension-spring` transitively depends on core.

### 9.1 Examples

`src/test` ships runnable example programs in both modules:

- `rocketmq-extension-core/src/test/java/org/apache/rocketmq/client/` — pure rocketmq-client examples
  (`SimpleProducer`, `SimpleAsyncProducer`, `SimpleOnewayProducer`, `SimpleSyncProducer`,
  `BatchProducer`, `OrderedProducer`, `BroadcastProducer`, `ScheduledMessageProducer`,
  `SimpleConsumer`, `OrderedConsumer`, `BroadcastConsumer`, `ScheduledMessageConsumer`,
  `ListSplitter`) — these require a running RocketMQ broker (default `127.0.0.1:9876`).
- `rocketmq-extension-spring/src/test/java/org/apache/rocketmq/client/` — Spring-wired examples
  (`SimpleProducer`, `BatchProducer`) that demonstrate the `FactoryBean`s and
 `MQProducerShutdownHook`.

## 10. Versioning & Branches

| Branch            | JDK | Maven | Spring | Version pattern               | disruptor-extension     |
| :---------------- | :-- | :---- | :----- | :---------------------------- | :---------------------- |
| `feature/1.0.x`  | 8   | 3.9.16 | 5.3.39 | `1.0.x.20260630-SNAPSHOT`     | `1.0.x.20260630-SNAPSHOT` |
| `feature/2.0.x`  | 17  | 3.9.16 | 6.2.19 | `2.0.x.20260630-SNAPSHOT`     | `2.0.x.20260630-SNAPSHOT` |
| **`feature/3.0.x`**  | **21**  | **4.0.0-rc-5** | **7.0.9** | **`3.0.x.20260630-SNAPSHOT`**   | **`3.0.x.20260630-SNAPSHOT`** |

The three lines are **maintained independently** — a security fix on the JDK 8 line will not
automatically propagate to the JDK 17/21 lines. Each branch publishes its own jars.

Dependabot status across branches:

| Branch            | Alerts remaining | Notes                                                            |
| :---------------- | :--------------- | :--------------------------------------------------------------- |
| `feature/1.0.x`  | 2 (low)          | Spring `AntPathMatcher` ReDoS — patched only in Spring 6.x+. Accept as wontfix on the JDK 8 line, or upgrade to 2.0.x / 3.0.x. |
| `feature/2.0.x`  | 0               | Clean.                                                           |
| **`feature/3.0.x`**  | **0**               | **Clean.**                                                           |

## 11. Contributing & License

Contributions are welcome — particularly JUnit tests for the handler chain, the `Ini` parser, and
end-to-end RocketMQ integration tests (the project intentionally ships only unit tests today).
Please open an issue before larger changes.

This project is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).