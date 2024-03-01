package server.handlers;

import spark.Request;
import spark.Response;
import service.ClearService;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    public Object handleClear(Response response) {
        Map<String, String> responseData = new HashMap<>();

        try {
            clearService.clearAll();
            response.status(200);
            return "";
        } catch (Exception e) {
            response.status(500);
            responseData.put("message", "Error: Internal server error");
            return new Gson().toJson(responseData);
        }
    }
}
