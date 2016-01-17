abstract class Utils {
    static Properties loadProperties(String... paths) throws IOException {
        Properties all = new Properties();
        for (String path in paths) {
            File file = new File(path);
            if (file.exists()) {
                all.load(new FileInputStream(file));
            }
        }
        return all;
    }
}
