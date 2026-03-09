package com.daw.cinemadaw.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import com.daw.cinemadaw.domain.cinema.New;

@Service
public class NewService {
    
    public ArrayList<New> getNews() throws FileNotFoundException {
        // Llegir un fitxer de text línia a línia
        ArrayList<New> newList = new ArrayList<>();
        File f = new File("news.txt");
        if (f.exists()) {
                // llegir l'arxiu
                Scanner lectorFitxer = new Scanner(f);
                String linia;
                while(lectorFitxer.hasNextLine()) {
                    linia = lectorFitxer.nextLine();
                    String[] camps = linia.split(":");
                    if (camps.length >= 2) {
                        New n = new New(camps[0], camps[1]);
                        newList.add(n);
                    }
                    System.out.println(linia);
                }
                return newList;
        }
        else {
                System.out.println("No existeix l'arxiu");
        }
        return newList;
    }
    
}
