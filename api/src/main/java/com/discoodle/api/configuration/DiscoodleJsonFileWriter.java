package com.discoodle.api.configuration;

import com.discoodle.api.model.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

public class DiscoodleJsonFileWriter {


    public static void runWriter(Message msg, String roomUUID) {
        LinkedList<Message> message;
        StringBuilder jsonContent = new StringBuilder();

        try {
            File myObj = new File(String.format("static/common/json/%s.json", roomUUID));
            if (!myObj.exists()) {
                myObj.createNewFile();
                PrintWriter writer = new PrintWriter(myObj);
                writer.write("[\n\n]");
                writer.close();
            }
            Path path = Paths.get(String.format("static/common/json/%s.json", roomUUID));
            Gson gson = new Gson();

            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                jsonContent.append(data);
            }
            myReader.close();
            message = gson.fromJson(String.valueOf(jsonContent), new TypeToken<LinkedList<Message>>() {}.getType());

            if (message == null)
                message = new LinkedList<>();

            message.addFirst(msg);

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                gson.toJson(gson.toJsonTree(message), writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
