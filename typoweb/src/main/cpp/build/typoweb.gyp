{
  'targets': [
    {
      'target_name': 'libtypoweb',
      'type': 'shared_library',
      'dependencies': [
        'skia.gyp:libtypoweb_skia',
        '<(DEPTH)/third_party/WebKit/public/blink.gyp:blink',
        '<(DEPTH)/url/url.gyp:url_lib',
        '<(DEPTH)/v8/tools/gyp/v8.gyp:v8',
      ],
      'sources': [
        '../src/util/JniUtils.cpp',
        '../src/util/StringUtils.cpp',
        '../src/util/UriUtils.cpp',
        '../src/TypoWebLibrary.cpp',
        '../src/BlinkPlatformImpl.cpp',
        '../src/BlinkResourceLoader.cpp',
        '../src/EntryPoint.cpp',
        '../src/DataURL.cpp',
        '../src/RenderContext.cpp',
        '../src/WebMimeRegistryImpl.cpp',
        '../src/WebThreadImpl.cpp',
        '../src/WebSchedulerImpl.cpp',
        '../src/WebURLLoaderImpl.cpp',
        '../src/TypoWeb.cpp',
        '../src/extensions/WordHyphenator.cpp',
        '../src/extensions/TypoExtensionsImpl.cpp',
        '../src/extensions/TypoHyphenatorImpl.cpp',
        '../src/extensions/TypoHangingPunctuationImpl.cpp',
      ],
      'ldflags': [
        '-Wl,--no-fatal-warnings',
      ],
    },
    {
      'target_name': 'libtypoweb_stripped',
      'type': 'none',
      'dependencies': [
        'libtypoweb',
      ],
      'actions': [
        {
          'action_name': 'strip',
          'message': 'Stripping libraries',
          'inputs': [
            '<(SHARED_LIB_DIR)/libtypoweb.so'
          ],
          'outputs': [
            '<(jni_libs_path)/<(android_app_abi)/libtypoweb.so',
          ],
          'action': [
            '<(android_strip)',
            '--strip-unneeded',
            '-o',
            '<(jni_libs_path)/<(android_app_abi)/libtypoweb.so',
            '<(SHARED_LIB_DIR)/libtypoweb.so'
          ],
        }
      ],
    },
  ],
}
