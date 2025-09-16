package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;

// JsonUtils provides a reusable method to read any JSON file under src/test/resources
public class JsonUtils {

    public static JsonNode readPageJson(String filePath, String pageKey) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new FileReader(filePath));
            return root.get(pageKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
