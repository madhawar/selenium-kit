package systems;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

public class Prop {
    private static final String rs_generic = "src/test/resources/generic.properties";
    private static final String rs_elements = "src/test/resources/elements.properties";
    private static final String rs_tmp = "src/test/resources/tmp.properties";
    private static final String rs_web = "src/test/resources/web.properties";
    private static final String rs_ex = "src/test/resources/ex.properties";

    public static Properties loadProperties(String path){
        Charset inputCharset = StandardCharsets.UTF_8;

        Properties properties = new Properties();
        FileInputStream file = null;
        try {
            file = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void writeProperties(String propKey, String propValue) {
        Properties properties = new Properties();
        File file = new File(rs_tmp);

        try (InputStream in = Files.newInputStream(file.toPath()))
        {
            properties.load(in);
            properties.setProperty(propKey, propValue);

            OutputStream out = Files.newOutputStream(file.toPath());
            properties.store(out, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        properties.stringPropertyNames().stream()
                .map(key -> key + ":" + properties.getProperty(key))
                .forEach(System.out::println);
    }

    public static void sampathExRate(String propKey, String propValue) {
        Properties properties = new Properties();
        File file = new File(rs_ex);

        try (InputStream in = Files.newInputStream(file.toPath()))
        {
            properties.load(in);
            properties.setProperty(propKey, propValue);

            OutputStream out = Files.newOutputStream(file.toPath());
            properties.store(out, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        properties.stringPropertyNames().stream()
                .map(key -> key + ":" + properties.getProperty(key))
                .forEach(System.out::println);
    }

    public static Properties generic(){
        return loadProperties(rs_generic);
    }

    public static Properties elements(){
        return loadProperties(rs_elements);
    }

    public static Properties tmp(){
        return loadProperties(rs_tmp);
    }

    public static Properties web(){
        return loadProperties(rs_web);
    }

}
