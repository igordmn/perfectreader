cppPath=".."
chromiumPath="$(head -1 chromiumPath.local)"
tempMirrorPath="${chromiumPath}/src/typoweb~"  # Folder with sources must be in chromium folder, so copy all into mirror folder

rm -rf ${tempMirrorPath}
mkdir ${tempMirrorPath} --parents
cp -rfp ${cppPath} ${tempMirrorPath}
