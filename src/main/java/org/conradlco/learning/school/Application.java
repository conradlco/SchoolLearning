package org.conradlco.learning.school;

import javax.swing.*;
import org.conradlco.learning.school.ui.ExerciseSelectorWindow;

public class Application {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(Application::createAndShowGUI);
  }

  private static void createAndShowGUI() {
    ExerciseSelectorWindow exerciseSelector = new ExerciseSelectorWindow();
    exerciseSelector.setVisible(true);
  }
}
