chromiumPath="$(head -1 chromiumPath.local)"
mkdir ${chromiumPath} --parents
pushd ${chromiumPath}
gclient sync --with_branch_heads --jobs 8
popd
