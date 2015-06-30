. ./private/setup.sh
export GYP_CROSSCOMPILE='1'
export GYP_DEFINES='OS=android target_arch=arm64 arm_version=8'
python ${tempMirrorPath}/build/gyp_typoweb "$@"
. ./private/shutdown.sh