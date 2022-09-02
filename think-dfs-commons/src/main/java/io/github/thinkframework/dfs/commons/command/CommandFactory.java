package io.github.thinkframework.dfs.commons.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class CommandFactory {
    private Gson gson = new GsonBuilder()
//            .setPrettyPrinting()
            .create();
//    public Command command(String context){
//        Map map = gson.fromJson(context,Map.class);
//
//    }
}
