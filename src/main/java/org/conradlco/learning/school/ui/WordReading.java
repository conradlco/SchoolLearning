package org.conradlco.learning.school.ui;

import javax.swing.*;
import java.awt.*;

public class WordReading extends JFrame {

  private final ExerciseSelectorWindow parentSelector;

  public WordReading(ExerciseSelectorWindow parentSelector) {
    super("Word Reading Game");
    this.parentSelector = parentSelector;

    setBounds(400, 200, 600, 400);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    buildLayout(this.getContentPane());

    // Start the first game automatically
    SwingUtilities.invokeLater(this::startGame);
  }

  private void buildLayout(Container container) {}

  private void startGame() {}
}
