#!/usr/bin/env bash

set -o pipefail

ColorReset="\033[0m"
ColorWhite="\033[97m"
ColorCyan="\033[36m"
ColorBlue="\033[34m"
ColorRed="\033[31m"

OutputRoot="out"
MainClass="com.htonmapper.Main"
TotalSteps=4

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

CheckJavaRuntime() {
    if ! command -v java >/dev/null 2>&1; then
        LogError 1 "java runtime not found in PATH"
        return 1
    fi

    local JavaVersionRaw
    JavaVersionRaw="$(java -version 2>&1 | head -n 1)"
    LogInfo 1 "using ${JavaVersionRaw}"
    return 0
}

CheckBuildOutput() {
    local MainClassPath="${OutputRoot}/$(echo ${MainClass} | tr '.' '/').class"
    if [ ! -d "${OutputRoot}" ] || [ ! -f "${MainClassPath}" ]; then
        LogWarn 2 "no existing build found, running build.sh first"
        if [ ! -x "./build.sh" ]; then
            LogError 2 "build.sh not found or not executable"
            return 1
        fi
        ./build.sh || return 1
    else
        LogInfo 2 "existing build found at ${OutputRoot}"
    fi
    return 0
}

PrepareLaunchEnvironment() {
    LogInfo 3 "preparing to launch ${MainClass}"
    return 0
}

LaunchApplication() {
    LogInfo 4 "launching HTONMapper GUI"
    echo ""
    java -cp "${OutputRoot}" "${MainClass}"
    local RunStatus=$?

    echo ""
    if [ ${RunStatus} -ne 0 ]; then
        LogError 4 "application exited with non zero status ${RunStatus}"
        return 1
    fi

    LogInfo 4 "application closed normally"
    return 0
}

Main() {
    CheckJavaRuntime || exit 1
    CheckBuildOutput || exit 1
    PrepareLaunchEnvironment || exit 1
    LaunchApplication || exit 1
}

Main "$@"
