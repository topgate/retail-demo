#!/bin/sh
script_dir="$(cd "$(dirname "${BASH_SOURCE:-${(%):-%N}}")"; pwd)"
version=$(date '+%Y%m%d-%H%M%S')
goapp deploy --application=retail-dataflow-demo --version=$version src