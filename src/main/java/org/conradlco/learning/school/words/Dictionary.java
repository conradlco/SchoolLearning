package org.conradlco.learning.school.words;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

  public List<DictionaryEntry> getWordsForLevel(ReadingLevel level) {
    return entries.get(level).stream().toList();
  }

  public DictionaryEntry getRandomWord() {
    int randomLevel = random.nextInt(ReadingLevel.values().length);

    List<DictionaryEntry> words = getWordsForLevel(ReadingLevel.values()[randomLevel]);
    int index = random.nextInt(words.size());

    return words.get(index);
  }

  public DictionaryEntry getRandomWordForLevel(ReadingLevel level) {
    List<DictionaryEntry> words = getWordsForLevel(level);
    int index = random.nextInt(words.size());
    return words.get(index);
  }

  public ReadingLevel getLevelOfWord(String word) {
    if (word == null) return null;
    for (Map.Entry<ReadingLevel, List<DictionaryEntry>> e : entries.entrySet()) {
      for (DictionaryEntry de : e.getValue()) {
        if (word.equals(de.word())) {
          return e.getKey();
        }
      }
    }
    return null;
  }

  private void loadFile(String filename, ReadingLevel level) {
    List<DictionaryEntry> levelEntries = entries.get(level);
    try {
      URL resource = this.getClass().getClassLoader().getResource(filename);
      if (resource == null) {
        throw new IllegalArgumentException("Resource not found: " + filename);
      }
      Path path = Path.of(resource.toURI());
      List<String> words = Files.readAllLines(path);
      for (String word : words) {
        String[] split = word.split(" ");
        levelEntries.add(new DictionaryEntry(split[0], split.length > 1 ? split[1] : null, level));
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
