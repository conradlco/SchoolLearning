package org.conradlco.learning.school.web;

import java.util.ArrayList;
import java.util.List;
import org.conradlco.learning.school.words.Dictionary;
import org.conradlco.learning.school.words.DictionaryEntry;
import org.conradlco.learning.school.words.ReadingLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/words")
public class ApiWordsController {
  private final Dictionary dictionary = Dictionary.getInstance();
  private static final int DEFAULT_MAX = 30; // capacity: 10 rows x 3 columns

  @GetMapping("/levels")
  public ResponseEntity<List<String>> levels() {
    List<String> out = new ArrayList<>();
    for (ReadingLevel rl : ReadingLevel.values()) {
      out.add(rl.name());
    }
    out.add("All");
    return ResponseEntity.ok(out);
  }

  @GetMapping("/search")
  public ResponseEntity<List<DictionaryEntry>> search(
      @RequestParam(name = "q") String q,
      @RequestParam(name = "level", required = false, defaultValue = "All") String level,
      @RequestParam(name = "limit", required = false) Integer limit) {

    String s = q == null ? "" : q.trim().toLowerCase();
    if (s.isEmpty()) {
      return ResponseEntity.ok(List.of());
    }

    int max = (limit == null) ? DEFAULT_MAX : Math.min(limit, DEFAULT_MAX);

    List<DictionaryEntry> candidates = new ArrayList<>();
    if (!"All".equalsIgnoreCase(level)) {
      try {
        ReadingLevel rl = ReadingLevel.valueOf(level);
        candidates.addAll(dictionary.getWordsForLevel(rl));
      } catch (IllegalArgumentException ex) {
        // unknown level - treat as all
        for (ReadingLevel rl : ReadingLevel.values()) {
          candidates.addAll(dictionary.getWordsForLevel(rl));
        }
      }
    } else {
      for (ReadingLevel rl : ReadingLevel.values()) {
        candidates.addAll(dictionary.getWordsForLevel(rl));
      }
    }

    List<DictionaryEntry> matches =
        candidates.stream()
            .filter(w -> w != null && w.word().toLowerCase().contains(s))
            .sorted()
            .toList();

    List<DictionaryEntry> out = new ArrayList<>();
    int shown = 0;
    for (DictionaryEntry m : matches) {
      if (shown >= max) break;
      out.add(m);
      shown++;
    }

    return ResponseEntity.ok(out);
  }
}
