package org.conradlco.learning.school.words;

public record DictionaryEntry(String word, String detail, ReadingLevel level)
    implements Comparable<DictionaryEntry> {

  @Override
  public String toString() {
    return detail != null ? word + "(" + detail + ")" : word;
  }

  @Override
  public int compareTo(DictionaryEntry o) {
    return word.compareTo(o.word);
  }
}
