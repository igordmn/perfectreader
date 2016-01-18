abstract class AndroidUtils {
    static int computeVersionCode(buildNumber, abi) {
        // values must not be changed
        def variantVersion =
                abi == null ? 0 :
                abi == 'armeabi' ? 1 :
                abi == 'armeabi-v7a' ? 2 :
                abi == 'mips' ? 3 :
                abi == 'x86' ? 4 :
                abi == 'arm64-v8a' ? 11 :
                abi == 'x86_64' ? 12 :
                abi == 'mips64' ? 13 :
                -1

        assert variantVersion >= 0 : 'unknown build variant'

        assert variantVersion < 2_000
        assert buildNumber < 1_000_000 : 'build number must less than 1 000 000 because max value of version code is 2^31 - 1 (~ 2 000 000 000)'

        return 2_000 * variantVersion + buildNumber
    }
}


