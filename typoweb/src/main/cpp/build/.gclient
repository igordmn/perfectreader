solutions = [
  { "name"        : "src",
    "url"         : "https://chromium.googlesource.com/chromium/src.git@0b56551f7dd21d7abc2c3ae4212dd8199d4e9519",
    "deps_file"   : ".DEPS.git",
    "managed"     : False,
    "custom_deps" : {
      "src/third_party/WebKit": "https://IgorDemin@bitbucket.org/IgorDemin/blink-typo.git@5e32a3fd9c4dd12c2030e7f185c53af0562c5501",
    },
    "safesync_url": "",
  },
]
cache_dir = None
target_os = ['android']