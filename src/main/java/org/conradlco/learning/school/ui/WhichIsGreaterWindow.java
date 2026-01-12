package org.conradlco.learning.school.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class WhichIsGreaterWindow extends JFrame implements ActionListener {

  private final ExerciseSelectorWindow parentSelector;

  private static final int TOTAL_QUESTIONS = 10;

  private int currentQuestionIndex = 0;
  private int score = 0;

  private final JLabel leftNumberLabel = new JLabel("", SwingConstants.CENTER);
  private final JLabel rightNumberLabel = new JLabel("", SwingConstants.CENTER);
  private final JLabel scoreLabel = new JLabel("Score: 0");
  private final JLabel counterLabel = new JLabel("0 / " + TOTAL_QUESTIONS, SwingConstants.CENTER);

  private final JButton greaterButton = new JButton("Left > Right");
  private final JButton equalButton = new JButton("Equal");
  private final JButton lessButton = new JButton("Left < Right");
  private final JButton quitButton = new JButton("Quit");
  private final JButton settingsButton = new JButton("Settings");

  private final Random rnd = new Random();

  private int currentLeft;
  private int currentRight;

  // Configurable weighted ranges and weights (percent)
  private int rangeA = 20;
  private int rangeB = 50;
  private int rangeC = 200;
  private int weightA = 50; // percent for rangeA
  private int weightB = 25; // percent for rangeB
  private int weightC = 25; // percent for rangeC

  public WhichIsGreaterWindow(ExerciseSelectorWindow parentSelector) {
    super("Greater Than Game");
    this.parentSelector = parentSelector;

    setBounds(400, 200, 600, 400);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    buildLayout(this.getContentPane());

    // Start the first game automatically
    SwingUtilities.invokeLater(this::startGame);
  }

  private void buildLayout(final Container panel) {
    panel.setLayout(new BorderLayout(10, 10));

    // Top: score and counter
    JPanel topPanel = new JPanel(new BorderLayout());

    // Add a small padding so score isn't flush with the window edge
    scoreLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
    topPanel.add(scoreLabel, BorderLayout.WEST);
    topPanel.add(counterLabel, BorderLayout.EAST);

    panel.add(topPanel, BorderLayout.NORTH);

    // Center: two large numbers side by side
    JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
    leftNumberLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
    rightNumberLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
    center.add(leftNumberLabel);
    center.add(rightNumberLabel);
    panel.add(center, BorderLayout.CENTER);

    // Bottom: answer buttons and quit/settings
    JPanel bottom = new JPanel(new BorderLayout());

    JPanel answers = new JPanel(new GridLayout(1, 3, 10, 0));
    greaterButton.addActionListener(this);
    equalButton.addActionListener(this);
    lessButton.addActionListener(this);
    answers.add(greaterButton);
    answers.add(equalButton);
    answers.add(lessButton);

    bottom.add(answers, BorderLayout.CENTER);

    // Settings button opens configuration dialog
    settingsButton.addActionListener(e -> openSettingsDialog());

    JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightControls.add(settingsButton);
    rightControls.add(quitButton);

    quitButton.addActionListener(
        e -> {
          // Return to selector when quitting mid-game
          dispose();
          if (parentSelector != null) {
            parentSelector.setVisible(true);
          }
        });

    bottom.add(rightControls, BorderLayout.EAST);

    panel.add(bottom, BorderLayout.SOUTH);
  }

  private void startGame() {
    currentQuestionIndex = 0;
    score = 0;
    updateScoreCounter();
    nextQuestion();
  }

  private void updateScoreCounter() {
    scoreLabel.setText("Score: " + score);
    counterLabel.setText((currentQuestionIndex + 1) + " / " + TOTAL_QUESTIONS);
  }

  private void nextQuestion() {
    if (currentQuestionIndex >= TOTAL_QUESTIONS) {
      showEndOfGameDialog();
      return;
    }

    // Generate two random numbers using configured weighted ranges
    currentLeft = randomNumberWithWeightedRange();
    currentRight = randomNumberWithWeightedRange();

    leftNumberLabel.setText(String.valueOf(currentLeft));
    rightNumberLabel.setText(String.valueOf(currentRight));

    updateScoreCounter();

    // Ensure buttons enabled
    greaterButton.setEnabled(true);
    equalButton.setEnabled(true);
    lessButton.setEnabled(true);
  }

  /** Return a random integer according to the configured weighted distribution. */
  private int randomNumberWithWeightedRange() {
    int p = rnd.nextInt(100); // 0..99
    int cumulative = 0;
    cumulative += weightA;
    if (p < cumulative) {
      return rnd.nextInt(rangeA + 1);
    }
    cumulative += weightB;
    if (p < cumulative) {
      return rnd.nextInt(rangeB + 1);
    }
    // fallback to C
    return rnd.nextInt(rangeC + 1);
  }

  private void openSettingsDialog() {
    // Create spinners for ranges and weights
    JSpinner rangeASpinner = new JSpinner(new SpinnerNumberModel(rangeA, 1, Integer.MAX_VALUE, 1));
    JSpinner rangeBSpinner = new JSpinner(new SpinnerNumberModel(rangeB, 1, Integer.MAX_VALUE, 1));
    JSpinner rangeCSpinner = new JSpinner(new SpinnerNumberModel(rangeC, 1, Integer.MAX_VALUE, 1));

    JSpinner weightASpinner = new JSpinner(new SpinnerNumberModel(weightA, 0, 100, 1));
    JSpinner weightBSpinner = new JSpinner(new SpinnerNumberModel(weightB, 0, 100, 1));
    JSpinner weightCSpinner = new JSpinner(new SpinnerNumberModel(weightC, 0, 100, 1));

    JPanel panel = new JPanel(new GridLayout(4, 3, 8, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    panel.add(new JLabel("Range A (0..N):"));
    panel.add(rangeASpinner);
    panel.add(new JLabel("Weight A (%):"));

    panel.add(new JLabel("Range B (0..N):"));
    panel.add(rangeBSpinner);
    panel.add(new JLabel("Weight B (%):"));

    panel.add(new JLabel("Range C (0..N):"));
    panel.add(rangeCSpinner);
    panel.add(new JLabel("Weight C (%):"));

    // show current weight spinners in the last row for clarity
    panel.add(new JLabel(" "));
    JPanel weightsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    weightsRow.add(weightASpinner);
    weightsRow.add(weightBSpinner);
    weightsRow.add(weightCSpinner);
    panel.add(weightsRow);
    panel.add(new JLabel(" "));

    int result =
        JOptionPane.showConfirmDialog(
            this,
            panel,
            "Configure Weighted Ranges",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      int newRangeA = (Integer) rangeASpinner.getValue();
      int newRangeB = (Integer) rangeBSpinner.getValue();
      int newRangeC = (Integer) rangeCSpinner.getValue();
      int newWeightA = (Integer) weightASpinner.getValue();
      int newWeightB = (Integer) weightBSpinner.getValue();
      int newWeightC = (Integer) weightCSpinner.getValue();

      int sum = newWeightA + newWeightB + newWeightC;
      if (sum != 100) {
        JOptionPane.showMessageDialog(
            this,
            "Weights must sum to 100%. Current sum: " + sum,
            "Invalid weights",
            JOptionPane.ERROR_MESSAGE);
        // Re-open settings so user can correct mistakes
        SwingUtilities.invokeLater(this::openSettingsDialog);
        return;
      }

      // Apply new configuration
      this.rangeA = newRangeA;
      this.rangeB = newRangeB;
      this.rangeC = newRangeC;
      this.weightA = newWeightA;
      this.weightB = newWeightB;
      this.weightC = newWeightC;

      // Inform the user of successful update
      JOptionPane.showMessageDialog(
          this, "Weighted ranges updated.", "Settings Saved", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    int chosen = -1; // 0: greater, 1: equal, 2: less
    if (src == greaterButton) chosen = 0;
    else if (src == equalButton) chosen = 1;
    else if (src == lessButton) chosen = 2;
    else return;

    // Disable to prevent double clicks
    greaterButton.setEnabled(false);
    equalButton.setEnabled(false);
    lessButton.setEnabled(false);

    int correct;
    if (currentLeft > currentRight) correct = 0;
    else if (currentLeft == currentRight) correct = 1;
    else correct = 2;

    boolean isCorrect = (chosen == correct);
    if (isCorrect) score++;

    // Show a simple dialog with result and current score
    String resultMessage =
        (isCorrect ? "Correct!" : "Wrong.") + "\nCurrent score: " + score + " / " + TOTAL_QUESTIONS;
    JOptionPane.showMessageDialog(
        this,
        resultMessage,
        "Answer Result",
        isCorrect ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

    currentQuestionIndex++;

    if (currentQuestionIndex >= TOTAL_QUESTIONS) {
      showEndOfGameDialog();
    } else {
      // Move to next question
      nextQuestion();
    }
  }

  private void showEndOfGameDialog() {
    String summary =
        "You scored " + score + " out of " + TOTAL_QUESTIONS + ".\nWhat would you like to do?";
    String[] options = {"Play Again", "Return to Exercises"};
    int choice =
        JOptionPane.showOptionDialog(
            this,
            summary,
            "Game Over",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);

    if (choice == 0) { // Play Again
      startGame();
    } else {
      // Return to exercises (or close)
      dispose();
      if (parentSelector != null) {
        parentSelector.setVisible(true);
      }
    }
  }
}
