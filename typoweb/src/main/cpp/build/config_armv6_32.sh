. ./private/setup.sh
export GYP_CROSSCOMPILE='1'
export GYP_DEFINES='OS=android target_arch=arm arm_version=6'
python ${tempMirrorPath}/build/gyp_typoweb.py "$@"
. ./private/shutdown.sh