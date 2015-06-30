solutions = [
  { "name"        : "src",
    "url"         : "https://chromium.googlesource.com/chromium/src.git@0b56551f7dd21d7abc2c3ae4212dd8199d4e9519",
    "deps_file"   : ".DEPS.git",
    "managed"     : False,
    "custom_deps" : {
      "src/third_party/WebKit": "https://IgorDemin@bitbucket.org/IgorDemin/blink-typo.git@e5e77e8eec0fcaf59b891f8a3c6ce9fd9be46fc4",
    },
    "safesync_url": "",
  },
]
cache_dir = None
target_os = ['android']