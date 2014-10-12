public abstract class Utils {
    public static Properties rootProperties(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            return properties;
        } else {
            return null;
        }
    }
}
