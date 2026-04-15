package com.daw.cinemadaw.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.daw.cinemadaw.domain.cinema.New;

@Service
public class NewService {

    private static final Path NEWS_FILE = Path.of("news.txt");
    
    public ArrayList<New> getNews() {
        ArrayList<New> newsList = new ArrayList<>();

        if (!Files.exists(NEWS_FILE)) {
            return newsList;
        }

        try {
            List<String> lines = Files.readAllLines(NEWS_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }

                String[] fields = line.split(":", 2);
                if (fields.length == 2) {
                    newsList.add(new New(fields[0].trim(), fields[1].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error llegint les noticies: " + e.getMessage());
        }

        return newsList;
    }

    public boolean addNews(String headline, String body) {
        String normalizedHeadline = normalizeField(headline).replace(":", " - ");
        String normalizedBody = normalizeField(body);

        if (normalizedHeadline.isBlank() || normalizedBody.isBlank()) {
            return false;
        }

        String line = normalizedHeadline + ":" + normalizedBody + System.lineSeparator();

        try {
            Files.writeString(
                    NEWS_FILE,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.out.println("Error guardant la noticia: " + e.getMessage());
            return false;
        }
    }

    private String normalizeField(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\r", " ").replace("\n", " ").trim();
    }
    
}
