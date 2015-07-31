#!/usr/bin/env python

# script will be run from temp mirror folder in chromium src (src/typoweb~)

import os
import sys

mirror = os.path.realpath(os.path.join(__file__, '..'))
home = os.path.realpath('.')
chromiumSrc = os.path.realpath(os.path.join(mirror, '../..'))

sys.path.append(os.path.realpath(os.path.join(chromiumSrc, 'tools/gyp/pylib')))
sys.path.append(os.path.realpath(os.path.join(chromiumSrc, 'tools/grit')))
sys.path.append(os.path.realpath(os.path.join(chromiumSrc, 'build')))
sys.path.append(os.path.realpath(os.path.join(chromiumSrc, 'build/android/gyp')))

import gyp
import gyp_environment

if __name__ == "__main__":
    gyp_environment.SetEnvironment()

    args = sys.argv[1:]
    args.append(os.path.join(mirror, 'typoweb.gyp'))
    args.append('-I' + os.path.realpath(os.path.join(chromiumSrc, 'build/common.gypi')))
    args.append('--no-circular-check')
    args.append('--check')
    args.append('-Djni_libs_path=' + os.path.realpath(os.path.join(home, '../../jniLibs')))
    args.append('-Drelease_unwind_tables=0')
    args.append('-Dv8_use_external_startup_data=0')
    args.append('-Duse_openmax_dl_fft=0')
    args.append('-Denable_basic_printing=0')

    print "Generating project build files..."

    sys.exit(gyp.main(args))
