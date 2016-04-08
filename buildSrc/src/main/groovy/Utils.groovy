static Properties loadProperties(String... paths) throws IOException {
    Properties all = new Properties();
    for (String path in paths) {
        File file = new File(path);
        if (file.exists())
            all.load(new FileInputStream(file));
    }
    return all;
}

static versionNameFromGit(path) {
    return "git describe --tags --match \"v[0-9]*\" --abbrev=0".execute([], path).text.trim()
}

static versionCodeFromGit(path) {
    return Integer.parseInt("git rev-list master --first-parent --count".execute([], path).text.trim())
}
