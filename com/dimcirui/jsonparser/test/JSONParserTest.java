package com.dimcirui.jsonparser.test;

import org.junit.Test;

import com.dimcirui.jsonparser.core.JSONParser;
import com.dimcirui.jsonparser.model.JsonArray;
import com.dimcirui.jsonparser.model.JsonObject;

public class JSONParserTest {
    @Test
    public void JsonObjectTest() throws Exception {
        // String json = "{\"employees\":[{\"name\":\"张三\",\"age\":30,\"skills\":[\"Java\",\"Python\"]},{\"name\":\"李四\",\"age\":25,\"skills\":[\"JavaScript\",\"C#\"]},{\"name\":\"王五\",\"age\":35,\"skills\":[\"PHP\",\"SQL\"]}],\"departments\":[\"研发部\",\"市场部\",\"人力资源部\"],\"company\":{\"name\":\"示例科技有限公司\",\"foundedYear\":2010,\"location\":\"上海\"}}";
        String json = "{\"name\": \"张三\", \"age\": 3, \"test\": 1}";
        JSONParser jsonParser = new JSONParser();
        JsonObject jsonObject = (JsonObject) jsonParser.fromJSON(json);
        System.out.println(jsonObject);
    }

    @Test
    public void JsonArrayTest() throws Exception {
        // String json = "[[1, 2, 35651.27184e+1024613], {\"name\":\"张三\",\"age\":30}]";
        String json = "[[true, 1e01]]";
        JSONParser jsonParser = new JSONParser();
        JsonArray jsonArray = (JsonArray) jsonParser.fromJSON(json);
        System.out.println(jsonArray);
    }
}
