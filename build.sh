#!/usr/bin/env bash

set -o pipefail

ColorReset="\033[0m"
ColorWhite="\033[97m"
ColorCyan="\033[36m"
ColorBlue="\033[34m"
ColorRed="\033[31m"

SourceRoot="src/java"
OutputRoot="out"
MainClass="com.htonmapper.Main"
TotalSteps=5

LogInfo() {
    local StepNumber="$1"
    local MessageText="$2"
    echo -e "${ColorWhite}[${ColorCyan}INFO${ColorWhite}]${ColorReset} ${ColorWhite}progress ${StepNumber}/${TotalSteps} ${MessageText}${ColorReset}"
}

LogWarn() {
    local StepNumber="$1"
    local MessageText="$2"
    echo -e "${ColorWhite}[${ColorBlue}WARN${ColorWhite}]${ColorReset} ${ColorWhite}progress ${StepNumber}/${TotalSteps} ${MessageText}${ColorReset}"
}

LogError() {
    local StepNumber="$1"
    local MessageText="$2"
    echo -e "${ColorWhite}[${ColorRed}ERROR${ColorWhite}]${ColorReset} ${ColorWhite}progress ${StepNumber}/${TotalSteps} ${MessageText}${ColorReset}"
}

CheckJavaInstallation() {
    if ! command -v java >/dev/null 2>&1; then
        LogError 1 "java runtime not found in PATH"
        return 1
    fi
    if ! command -v javac >/dev/null 2>&1; then
        LogError 1 "javac compiler not found, install a JDK not just a JRE"
        return 1
    fi

    local JavaVersionRaw
    JavaVersionRaw="$(javac -version 2>&1)"
    LogInfo 1 "detected ${JavaVersionRaw}"
    return 0
}

CleanOutputDirectory() {
    if [ -d "${OutputRoot}" ]; then
        LogInfo 2 "cleaning previous output directory"
        rm -rf "${OutputRoot}"
    else
        LogInfo 2 "no previous output directory found"
    fi
    mkdir -p "${OutputRoot}"
    return 0
}

CollectSourceFiles() {
    if [ ! -d "${SourceRoot}" ]; then
        LogError 3 "source root ${SourceRoot} does not exist"
        return 1
    fi

    SourceFileList="$(find "${SourceRoot}" -name "*.java")"
    if [ -z "${SourceFileList}" ]; then
        LogError 3 "no java source files found under ${SourceRoot}"
        return 1
    fi

    local FileCount
    FileCount="$(echo "${SourceFileList}" | wc -l)"
    LogInfo 3 "collected ${FileCount} java source files"
    return 0
}

CompileSourceFiles() {
    LogInfo 4 "compiling sources into ${OutputRoot}"

    local CompileLog
    CompileLog="$(javac -encoding UTF-8 -d "${OutputRoot}" ${SourceFileList} 2>&1)"
    local CompileStatus=$?

    if [ ${CompileStatus} -ne 0 ]; then
        LogError 4 "compilation failed, see details below"
        echo -e "${ColorWhite}${CompileLog}${ColorReset}"
        return 1
    fi

    if [ -n "${CompileLog}" ]; then
        LogWarn 4 "javac reported warnings during compilation"
        echo -e "${ColorWhite}${CompileLog}${ColorReset}"
    fi

    LogInfo 4 "compilation completed successfully"
    return 0
}

VerifyBuildOutput() {
    local MainClassPath="${OutputRoot}/$(echo ${MainClass} | tr '.' '/').class"
    if [ ! -f "${MainClassPath}" ]; then
        LogError 5 "expected main class not found at ${MainClassPath}"
        return 1
    fi
    LogInfo 5 "build verified, entry point ready at ${MainClassPath}"
    return 0
}

Main() {
    CheckJavaInstallation || exit 1
    CleanOutputDirectory || exit 1
    CollectSourceFiles || exit 1
    CompileSourceFiles || exit 1
    VerifyBuildOutput || exit 1

    echo -e "${ColorWhite}[${ColorCyan}INFO${ColorWhite}]${ColorReset} ${ColorWhite}progress ${TotalSteps}/${TotalSteps} build finished, run ./run.sh to launch HTONMapper${ColorReset}"
}

Main "$@"
