{
  'targets': [
    {
      'target_name': 'libtypoweb_skia',
      'type': 'static_library',
      'dependencies': [
        '<(DEPTH)/skia/skia.gyp:skia',
      ],
      'sources': [
        '../src/skia/GrGLCreateNativeInterface_android.cpp',
      ],
      'include_dirs': [
        '<(DEPTH)/third_party/skia/include/gpu',
        '<(DEPTH)/third_party/skia/src/gpu',
      ],
      'link_settings': {
        'libraries': [
          '-lGLESv2',
          '-lEGL',
        ],
      },
    },
  ],
}
