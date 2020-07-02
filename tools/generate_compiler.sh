#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o xtrace

set -o nounset
SCRIPT_PATH="$( cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"
BIN_DIR="$SCRIPT_PATH/tmp-bin"
OUT_DIR="$SCRIPT_PATH/../core/src/com/kyper/yarn/compiler"
JAR_PATH="$BIN_DIR/antlr4.jar"
GRAMMAR_DIR="grammarFork"

set +o nounset
export CLASSPATH=".:$JAR_PATH:$CLASSPATH"
set -o nounset

mkdir -p "$BIN_DIR"
mkdir -p "$OUT_DIR"
rm -Rf "$OUT_DIR/*"

if [[ ! -f "$JAR_PATH" ]]; then
    curl --location https://www.antlr.org/download/antlr-4.7.1-complete.jar -o "$JAR_PATH"
fi

java -Xmx500M -cp "$JAR_PATH:$CLASSPATH" org.antlr.v4.Tool -lib "$GRAMMAR_DIR" "$GRAMMAR_DIR"/*.g4 -o "$OUT_DIR" -visitor -package com.kyper.yarn.compiler -Xlog -Xexact-output-dir

