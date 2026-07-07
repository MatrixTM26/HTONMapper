# HTONMapper

Cross platform TCP connect port scanner built with Java Swing, styled after a GitHub dark theme layout.

## Overview

HTONMapper scans a target host across a configurable port range using multi-threaded TCP connect attempts. Results are shown live in a table with response times and resolved service names, alongside a real time percentage progress bar and an activity log.

This tool performs TCP connect scanning through the standard Java `Socket` API. It does not use raw sockets or packet crafting, so it runs identically on Windows and Linux without elevated privileges.

## Features

- Multi-threaded TCP connect scanning with configurable thread count and timeout
- Live results table with port, status, resolved service name, and response time
- Percentage based progress bar updated per completed port
- Activity log with timestamps
- Service name resolution for around 100 common ports

## Requirements

- JDK 8 or newer (a full JDK, not just a JRE, since `javac` is required to build)
- Windows or Linux desktop environment capable of running Swing applications

## Build

Run the build script from the project root:

```bash
./build.sh
```

`build.sh` performs these steps:

1. Checks that `java` and `javac` are available in `PATH`
2. Cleans and recreates the `out/` output directory
3. Collects all `.java` source files under `src/java`
4. Compiles the sources into `out/`
5. Verifies that the compiled entry point class exists

Each step prints a colored status line in the format:

```
[LEVEL] progress N/5 message
```

`INFO` is cyan, `WARN` is blue, `ERROR` is red, and the brackets plus message text stay white.

## Run

Run the run script from the project root:

```bash
./run.sh
```

`run.sh` performs these steps:

1. Checks that `java` is available in `PATH`
2. Checks for an existing build in `out/`, running `build.sh` automatically if missing
3. Prepares the launch environment
4. Launches `com.htonmapper.Main` through the JVM

Each step prints a colored status line in the same `[LEVEL] progress N/4 message` format used by `build.sh`.

## Manual Build and Run

If you prefer not to use the scripts:

```bash
cd src/java
javac -d ../../out com/htonmapper/Main.java com/htonmapper/core/*.java com/htonmapper/gui/*.java
cd ../../out
java com.htonmapper.Main
```

## Usage Notes

- Target Host accepts an IP address or a resolvable hostname
- Start Port and End Port define the inclusive scan range, from 1 to 65535
- Timeout controls how long, in milliseconds, a connection attempt waits before being treated as closed or filtered
- Thread Count controls how many concurrent connection attempts run at once, higher values scan faster but increase local resource usage and network noise

## Responsible Use

Only scan hosts and networks you own or are explicitly authorized to test. Unauthorized port scanning may violate laws or terms of service depending on your jurisdiction and the target network's policies.

## Credit

- [MatrixTM26](https://github.com/MatrixTM26)
- [AGPL-V3 License](./LICENSE)

> [!CAUTION]
> This tool is currently under development, in some release versions, you may encounter functional errors or logic flaws.
