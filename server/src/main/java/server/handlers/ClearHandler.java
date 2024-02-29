package server.handlers;

import spark.Request;
import spark.Response;
import service.ClearService;
import com.google.gson.Gson;

import java.util.Map;

public class ClearHandler {
    private ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    public Object handle(Request request, Response response) {
        try {
            clearService.clearAll();
            response.status(200);
            return "";
        } catch (Exception e) {
            response.status(500);
            return new Gson().toJson(Map.of("message", "Error: Internal server error"));
        }
}
}
