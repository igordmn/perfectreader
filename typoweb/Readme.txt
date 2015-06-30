	How to build libtypoweb.so

I. Get Ubuntu (for example in VirtualBox VM). Tested on Ubuntu 14.04.2 LTS

II. Get chromium code (original see https://code.google.com/p/chromium/wiki/AndroidBuildInstructions)
    1. Download depot_tools:

        call:
          mkdir ~/app/
          cd app
          git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
          sudo gedit ~/.bashrc

        add to the end :
          export PATH=~/app/depot_tools:$PATH

        restart terminal

    3. Set path to folder where chromium will be loading in file cpp/build/chromiumPath.local. For example /home/user/dev/chromium

    4. Call ./syncChromium.sh from Ubuntu (via network shared folder)

    5. Configure Ubuntu:

        sudo src/build/install-build-deps.sh
        sudo src/build/install-build-deps-android.sh

III. Build
    1. Call: ./config_armv7_32.sh
       (or other architecture)
       (for fast builds add -Dfastbuild=1, for more understandable errors add -Dclang=1)

    2. Call: ./build.sh

IV. Build all architectures
    Call: ./buildAll.sh


    How to see stack

1. Download NDK and add root directory to PATH
2. Create file logcat.log
3. Paste error begins ** ** ** ... and ends before stack:
4. Call: ./stack.sh


    How to upgrade chromium

1. See last stable <current_version>, <branch_commit>, <true_branch> at https://omahaproxy.appspot.com/, android-stable
2. In <chromium_home>/src folder: git checkout <branch_commit>
3. Open repo src/third_party/WebKit
4. Call git checkout original
5. Remove all except .git
6. Call: svn checkout http://src.chromium.org/blink/branches/chromium/<true_branch>/
7. Remove all .svn folders
8. Call: git add -A
9. Call: git commit -m "<true_branch>"
10. Merge "original" to "dev"
11. Test and apply fixes in "dev" branch
12. Merge "dev" to "master"
13. Push all changed
14. Change url and custom_deps in <cppbuild_path>/.gclient
    (url point to <branch_commit>, custom_deps/Webkit points to commit in master branch)
15. Call: ./syncChromium.sh
16. Copy resources to <typoweb>/src/main/res.
    css from third_party\WebKit\Source\core\css (rename them, and associate in BlinkResourceLoader)
    icudtl from third_party\icu\android