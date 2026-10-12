# chisel-example

一个最小 Chisel 项目，支持 **sbt / Mill**，演示从 Chisel 源码生成 SystemVerilog RTL。

## Quick Start

### 电路连接

`ExampleTop` 将计数器输出与外部操作数相加：

```text
                       ExampleTop

              +---------------------+
 enable ----->| enable              |
 clear ------>| clear       Counter |
              |               value |----+--------> count
              |            overflow |----|--------> overflow
              +---------------------+    |
                                         |
              +---------------------+    |
              |                   a |<---+
 operand ---->| b             Adder |
              |                 sum |-------------> sum
              +---------------------+
```

`value` 分支与 `overflow` 交叉处不相连。图中省略了隐式的 `clock` 和 `reset`。

### Makefile

准备好 JDK 25、GNU Make 和 sbt 或 Mill，在项目根目录执行：

```bash
make verilog                       # 默认使用 Mill，输出到 generated/
make bsp                           # 生成 Mill BSP 配置，供 Metals 使用

make verilog BUILD_TOOL=sbt         # 切换到 sbt
make bsp BUILD_TOOL=sbt

make compile                       # 单独编译
make clean                         # 清理所选工具，不删除生成的 RTL
make help                          # 查看选项
```

可通过 `OUT_DIR=generated/custom` 指定输出目录。首次运行需要联网下载依赖和 `firtool`，生成 RTL 不需要仿真器。

### 直接使用构建工具

| 操作 | sbt | Mill |
| --- | --- | --- |
| 编译 | `sbt compile` | `mill --no-daemon compile` |
| 生成 RTL | `sbt "runMain example.GenerateVerilog generated"` | `mill --no-daemon runMain example.GenerateVerilog generated` |
| BSP 配置 | `sbt bspConfig` | `mill --no-daemon --bsp-install` |
| 清理 | `sbt clean` | `mill --no-daemon clean` |

也可以使用 `sbt run` 或 `mill --no-daemon run`，默认输出到 `generated/`。Mill 使用 `--no-daemon` 避免本机遇到的守护进程连接问题。

生成文件：`Counter.sv`、`Adder.sv`、`ExampleTop.sv` 和 `filelist.f`。

## 项目组成

```text
├── Makefile                        # 编译、RTL 生成、BSP 和清理入口
├── build.sbt                       # sbt 构建配置
├── project/build.properties        # sbt 版本
├── build.mill                      # Mill 构建配置，共享 sbt 源码布局
├── .mill-version                   # Mill 版本
├── .vscode/extensions.json         # 推荐 Metals 扩展
└── src/main/scala/example/
    ├── Adder.scala                 # 保留进位的组合加法器
    ├── Counter.scala               # 支持使能、清零和回绕的计数器
    ├── ExampleTop.scala            # 顶层模块及连接
    └── GenerateVerilog.scala       # RTL 生成入口
```

计数器复位为零，`clear` 为同步清零且优先于 `enable`。`overflow` 在允许递增、未清零且计数达到最大值时置位；`sum` 比操作数多一位，保留进位。

## 环境与版本

| 组件 | 版本 |
| --- | --- |
| JDK | 25 |
| Scala | 2.13.18 |
| Chisel / 编译插件 | 7.16.0 |
| sbt | 2.0.10 |
| Mill | 1.3.0 |

`.mill-version` 使用 `1.3.0-jvm` 选择 JVM 启动器，`build.mill` 使用系统 Java。Scala 与 Chisel 编译插件的完整版本需保持匹配。

已在 JDK 25 下验证两套工具的编译、RTL 生成及清理流程，生成的顶层 RTL 一致。构建与生成产物已加入 `.gitignore`。

## VSCode / Cursor 与 BSP

**BSP（Build Server Protocol）** 连接 Metals 与构建工具，提供源码、依赖和编译信息，支持跳转、补全和诊断；它不是 RTL 仿真工具。

1. 打开项目根目录，安装 **Scala (Metals)** 扩展；WSL / SSH 环境应安装在远端。
2. 执行 `make bsp`（Mill）或 `make bsp BUILD_TOOL=sbt`。
3. 接受 Metals 导入提示；如果存在多个服务器，用 **Metals: Switch build server** 选择对应工具。
4. 等导入和索引完成后，对 `Counter` / `Adder` 使用 **F12** 跳转。

若 Metals 找不到 Java 或卡在启动阶段，在本机 / 远端用户设置中将 `metals.metalsJavaHome` 和 `metals.javaHome` 指向 JDK 25，设置 `metals.javaVersion` 为 `"25"`，再执行 **Developer: Reload Window**。仅修改终端 `JAVA_HOME` 不一定影响编辑器进程。

BSP 连接文件位于 `.bsp/`，不提交 Git；换机器后重新生成。Metals 导入和索引可能触发 Scala 编译。

## 后续工作

目前只覆盖 RTL 生成，**不代表电路功能和时序已验证**。后续可按需补充 RTL 仿真、波形检查、lint、综合及 FPGA / ASIC 约束。

新增模块放在 `src/main/scala/example/`；修改 `GenerateVerilog.scala` 可更换顶层和参数。添加依赖时同步更新 `build.sbt` 与 `build.mill`。

Makefile 入口形式参考[香山项目](https://github.com/OpenXiangShan/XiangShan/blob/master/Makefile)，按本项目版本做了简化。
