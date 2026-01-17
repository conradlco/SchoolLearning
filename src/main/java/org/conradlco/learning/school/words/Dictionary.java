package org.conradlco.learning.school.words;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Dictionary {

  private static Dictionary instance;

  private final Random random = new Random();

  private Map<ReadingLevel, List<DictionaryEntry>> entries;

  private Dictionary() {
    entries = new HashMap<>();

    for (ReadingLevel level : ReadingLevel.values()) {
      entries.put(level, new ArrayList<>());
    }

    loadFile("Words_A1.txt", ReadingLevel.A1);
    loadFile("Words_A2.txt", ReadingLevel.A2);
    loadFile("Words_B1.txt", ReadingLevel.B1);
    loadFile("Words_B2.txt", ReadingLevel.B2);
  }

  public static Dictionary getInstance() {
    if (instance == null) {
      instance = new Dictionary();
    }

    return instance;
  }

  public List<String> getWordsForLevel(ReadingLevel level) {
    return entries.get(level).stream().map(DictionaryEntry::word).toList();
  }

  public String getRandomWordForLevel(ReadingLevel level) {
    List<String> words = getWordsForLevel(level);
    int index = random.nextInt(words.size());
    return words.get(index);
  }

  private void loadFile(String filename, ReadingLevel level) {
    List<DictionaryEntry> levelEntries = entries.get(level);
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      List<String> words = br.readAllLines();
      for (String word : words) {
        String[] split = word.split(" ");
        levelEntries.add(new DictionaryEntry(split[0], level));
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
