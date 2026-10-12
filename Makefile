# Common Chisel workflow: make verilog / make bsp.
# Override BUILD_TOOL=sbt to use the alternate build definition.
.DEFAULT_GOAL := help

BUILD_TOOL ?= mill
SBT ?= sbt
MILL ?= mill
MILL_FLAGS ?= --no-daemon
OUT_DIR ?= generated
MAIN_CLASS := example.GenerateVerilog

ifeq ($(BUILD_TOOL),mill)
COMPILE_CMD = $(MILL) $(MILL_FLAGS) compile
VERILOG_CMD = $(MILL) $(MILL_FLAGS) runMain $(MAIN_CLASS) "$(OUT_DIR)"
BSP_CMD = $(MILL) $(MILL_FLAGS) --bsp-install
CLEAN_CMD = $(MILL) $(MILL_FLAGS) clean
else ifeq ($(BUILD_TOOL),sbt)
COMPILE_CMD = $(SBT) compile
VERILOG_CMD = $(SBT) 'runMain $(MAIN_CLASS) "$(OUT_DIR)"'
BSP_CMD = $(SBT) bspConfig
CLEAN_CMD = $(SBT) clean
else
$(error BUILD_TOOL must be mill or sbt, got '$(BUILD_TOOL)')
endif

.PHONY: help compile verilog bsp clean

help:
	@printf '%s\n' \
	  'make verilog                 Generate SystemVerilog with Mill (default)' \
	  'make verilog BUILD_TOOL=sbt  Generate SystemVerilog with sbt' \
	  'make bsp                     Generate Mill BSP connection for Metals' \
	  'make bsp BUILD_TOOL=sbt      Generate sbt BSP connection for Metals' \
	  'make compile                 Compile Scala / Chisel sources' \
	  'make clean                   Clean selected tool; keep RTL and BSP files' \
	  'Options: BUILD_TOOL=mill|sbt OUT_DIR=generated MILL_FLAGS=--no-daemon' \
	  'Run all targets from the project root; compilation may download dependencies.'

compile:
	$(COMPILE_CMD)

# runMain already compiles dependencies; no separate compile prerequisite needed.
verilog:
	$(VERILOG_CMD)

# Mill 1.x uses --bsp-install; sbt provides bspConfig natively.
bsp:
	$(BSP_CMD)

clean:
	$(CLEAN_CMD)
