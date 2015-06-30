#!/bin/bash

set -e

echo "Building armv7_32"
. ./config_armv7_32.sh
. ./buildRelease.sh
echo "Building armv8_64"
. ./config_armv8_64.sh
. ./buildRelease.sh
echo "Building mips_32"
. ./config_mips_32.sh
. ./buildRelease.sh
echo "Building mips_64"
. ./config_mips_64.sh
. ./buildRelease.sh
echo "Building x86_32"
. ./config_x86_32.sh
. ./buildRelease.sh
echo "Building x86_64"
. ./config_x86_64.sh
. ./buildRelease.sh