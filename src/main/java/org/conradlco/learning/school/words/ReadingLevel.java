package org.conradlco.learning.school.words;

public enum ReadingLevel {
  A1(1),
  A2(2),
  B1(3),
  B2(4);

  private final int level;

  ReadingLevel(int level) {
    this.level = level;
  }

  public int getLevel() {
    return this.level;
  }
}
