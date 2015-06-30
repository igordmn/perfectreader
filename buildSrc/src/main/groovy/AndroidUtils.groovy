abstract class AndroidUtils {
    static int computeVersionCode(buildNumber, abi) {
        // values must not changed
        def variantVersion =
                abi == 'armeabi' ? 0 :
                abi == 'armeabi-v7a' ? 1 :
                abi == 'mips' ? 2 :
                abi == 'x86' ? 3 :
                abi == 'arm64-v8a' ? 10 :
                abi == 'x86_64' ? 11 :
                abi == 'mips64' ? 12 :
                -1

        assert variantVersion >= 0 : 'unknown build variant'

        assert variantVersion < 2_000
        assert buildNumber < 1_000_000 : 'build number must less than 1 000 000 because max value of version code is 2^31 - 1 (~ 2 000 000 000)'

        return 2_000 * variantVersion + buildNumber
    }
}


