package org.conradlco.learning.school.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.conradlco.learning.school.words.Dictionary;
import org.conradlco.learning.school.words.ReadingLevel;

public class WordReading extends JFrame {

  private final ExerciseSelectorWindow parentSelector;

  private Dictionary dictionary;

  // UI components
  private JComboBox<Object> levelCombo; // changed to Object to allow an "All" entry
  private JButton playButton;
  private JButton stopButton; // new stop button
  private JButton quitButton;
  private JLabel wordLabel;
  private JButton correctButton;
  private JButton wrongButton;
  private JLabel scoreLabel;
  private JSpinner questionsSpinner; // new spinner to select number of questions
  private JLabel remainingLabel; // shows questions remaining during the game
  private JLabel progressLabel; // shows per-level progress percentage

  // Game state
  private List<String> gameWords = new ArrayList<>();
  private List<String> wrongWords = new ArrayList<>(); // track words answered incorrectly
  private final java.util.Map<ReadingLevel, java.util.Set<String>> correctWordsPerLevel =
      new java.util.EnumMap<>(ReadingLevel.class);
  private ReadingLevel activeLevel = null; // current game's level, null for "All"
  private int totalQuestions = 10;
  private int currentIndex = 0;
  private int score = 0;
  private final Random random = new Random();

  public WordReading(ExerciseSelectorWindow parentSelector) {
    super("Word Reading Game");
    this.parentSelector = parentSelector;

    this.dictionary = Dictionary.getInstance();
    // initialize per-level tracking sets
    for (ReadingLevel rl : ReadingLevel.values()) {
      correctWordsPerLevel.put(rl, new java.util.HashSet<>());
    }

    setBounds(400, 200, 600, 400);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    buildLayout(this.getContentPane());

    // Do not start a game automatically; wait for user to press Play
  }

  private void buildLayout(Container container) {
    container.setLayout(new BorderLayout(10, 10));

    // Top controls: level selection and play/quit
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    // Create combo model with enum values in order, and an "All" option at the end
    DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
    for (ReadingLevel rl : ReadingLevel.values()) {
      model.addElement(rl);
    }
    model.addElement("All");
    levelCombo = new JComboBox<>(model);

    // Questions spinner (configurable totalQuestions)
    questionsSpinner = new JSpinner(new SpinnerNumberModel(totalQuestions, 1, 1000, 1));
    questionsSpinner.setToolTipText("Number of questions per game");

    playButton = new JButton("Play");
    stopButton = new JButton("Stop Game");
    quitButton = new JButton("Quit to Menu");

    topPanel.add(new JLabel("Level:"));
    topPanel.add(levelCombo);
    topPanel.add(new JLabel("Questions:"));
    topPanel.add(questionsSpinner);
    topPanel.add(playButton);
    topPanel.add(stopButton);
    topPanel.add(quitButton);

    container.add(topPanel, BorderLayout.NORTH);

    // Center: large word display
    wordLabel = new JLabel("", SwingConstants.CENTER);
    wordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
    wordLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
    container.add(wordLabel, BorderLayout.CENTER);

    // Bottom: correct/wrong buttons and score label
    JPanel bottomPanel = new JPanel(new BorderLayout());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    correctButton = new JButton("Correct");
    wrongButton = new JButton("Wrong");
    correctButton.setEnabled(false);
    wrongButton.setEnabled(false);
    buttonPanel.add(correctButton);
    buttonPanel.add(wrongButton);

    bottomPanel.add(buttonPanel, BorderLayout.CENTER);

    scoreLabel = new JLabel("Score: 0/0");
    // Add a bit of padding so the score isn't flush against the window edge
    scoreLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
    bottomPanel.add(scoreLabel, BorderLayout.WEST);

    // Remaining label on the right
    remainingLabel = new JLabel(String.format("Remaining: %d", totalQuestions));
    remainingLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

    // Progress label for per-level distinct correct words
    progressLabel = new JLabel("Progress: 0%");
    progressLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

    JPanel eastPanel = new JPanel();
    eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
    eastPanel.add(remainingLabel);
    eastPanel.add(progressLabel);
    bottomPanel.add(eastPanel, BorderLayout.EAST);

    container.add(bottomPanel, BorderLayout.SOUTH);

    // Wire actions
    playButton.addActionListener(e -> initializeGame());

    stopButton.addActionListener(e -> stopGame());

    quitButton.addActionListener(
        e -> {
          // Stop current game and return to menu
          endGameAndReturnToMenu();
        });

    correctButton.addActionListener(e -> markAnswer(true));
    wrongButton.addActionListener(e -> markAnswer(false));

    // Update progress label whenever the level selection changes
    levelCombo.addActionListener(e -> updateProgressLabel());
  }

  private void initializeGame() {
    Object selected = levelCombo.getSelectedItem();

    // set activeLevel for this game (null when "All")
    if (selected instanceof ReadingLevel) {
      activeLevel = (ReadingLevel) selected;
    } else {
      activeLevel = null;
    }

    // Read totalQuestions from spinner in case user changed it
    try {
      totalQuestions = (Integer) questionsSpinner.getValue();
      if (totalQuestions < 1) totalQuestions = 1;
    } catch (Exception ex) {
      totalQuestions = 20;
    }

    gameWords.clear();
    wrongWords.clear();

    if (selected instanceof ReadingLevel) {
      ReadingLevel level = (ReadingLevel) selected;

      List<String> words = new ArrayList<>(dictionary.getWordsForLevel(level));
      Collections.shuffle(words, random);

      if (words.size() >= totalQuestions) {
        gameWords.addAll(words.subList(0, totalQuestions));
      } else if (!words.isEmpty()) {
        // If there are fewer words than needed, pick randomly until we have enough (allow repeats)
        gameWords.addAll(words);
        while (gameWords.size() < totalQuestions) {
          gameWords.add(words.get(random.nextInt(words.size())));
        }
      } else {
        // No words available for that level
        JOptionPane.showMessageDialog(
            this,
            "No words available for selected level.",
            "No Words",
            JOptionPane.WARNING_MESSAGE);
        return;
      }
    } else {
      // "All" selected (or unknown selection): pick random words across all levels
      try {
        for (int i = 0; i < totalQuestions; i++) {
          gameWords.add(dictionary.getRandomWord());
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(
            this, "No words available to play.", "No Words", JOptionPane.WARNING_MESSAGE);
        return;
      }
    }

    currentIndex = 0;
    score = 0;
    updateScoreLabel();
    updateProgressLabel();

    // UI state
    playButton.setEnabled(false);
    questionsSpinner.setEnabled(false);
    levelCombo.setEnabled(false);
    stopButton.setEnabled(true);
    correctButton.setEnabled(true);
    wrongButton.setEnabled(true);

    showCurrentWord();
  }

  private void stopGame() {
    // Cancel the current game but stay on the game window ready to start another
    gameWords.clear();
    wrongWords.clear();
    currentIndex = 0;
    score = 0;
    // UI state: allow user to start a new game
    playButton.setEnabled(true);
    questionsSpinner.setEnabled(true);
    levelCombo.setEnabled(true);
    stopButton.setEnabled(false);
    correctButton.setEnabled(false);
    wrongButton.setEnabled(false);
    wordLabel.setText("");
    updateScoreLabel();
    updateProgressLabel();
  }

  private void showCurrentWord() {
    if (currentIndex < gameWords.size()) {
      wordLabel.setText(gameWords.get(currentIndex));
      updateScoreLabel();
    } else {
      finishGame();
    }
  }

  private void markAnswer(boolean wasCorrect) {
    // record the current word if incorrect
    String currentWord = null;
    if (currentIndex < gameWords.size()) {
      currentWord = gameWords.get(currentIndex);
    }
    if (wasCorrect) {
      score++;
      // record distinct correct word for the appropriate level
      if (currentWord != null) {
        if (activeLevel != null) {
          correctWordsPerLevel.get(activeLevel).add(currentWord);
        } else {
          // when playing All, try to resolve the word's level
          ReadingLevel rl = dictionary.getLevelOfWord(currentWord);
          if (rl != null) {
            correctWordsPerLevel.get(rl).add(currentWord);
          }
        }
      }
    } else if (currentWord != null) {
      wrongWords.add(currentWord);
    }
    currentIndex++;

    // Update per-level progress immediately after answering
    updateProgressLabel();

    if (currentIndex < gameWords.size()) {
      showCurrentWord();
    } else {
      finishGame();
    }
  }

  private void finishGame() {
    // Disable answer buttons
    correctButton.setEnabled(false);
    wrongButton.setEnabled(false);

    // Show the list of wrong words (if any) so the user knows what to practice
    if (wrongWords.isEmpty()) {
      JOptionPane.showMessageDialog(
          this,
          "Great! You got all words correct.",
          "Practice Results",
          JOptionPane.INFORMATION_MESSAGE);
    } else {
      // Show up to 5 wrong words without scrolling, each on its own bold line
      int displayCount = Math.min(5, wrongWords.size());
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(new EmptyBorder(8, 12, 8, 12));
      // Header with final score
      JLabel header =
          new JLabel(
              String.format(
                  "Your final score was %d/%d, these are the words you need to practice:",
                  score, totalQuestions));
      // Slightly larger than the incorrect-word font so the summary stands out
      header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
      panel.add(header);
      panel.add(Box.createRigidArea(new Dimension(0, 8)));
      for (int i = 0; i < displayCount; i++) {
        JLabel label = new JLabel(wrongWords.get(i));
        // Incorrect words in a very dark red to distinguish them from the header
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        label.setForeground(new Color(139, 0, 0));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
      }
      if (wrongWords.size() > displayCount) {
        JLabel more = new JLabel(String.format("...and %d more", wrongWords.size() - displayCount));
        more.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        panel.add(more);
      }
      JOptionPane.showMessageDialog(
          this, panel, "Words to practice", JOptionPane.INFORMATION_MESSAGE);
    }

    String message = String.format("You scored %d out of %d.\nPlay again?", score, totalQuestions);
    int choice =
        JOptionPane.showConfirmDialog(
            this, message, "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

    if (choice == JOptionPane.YES_OPTION) {
      // Reset UI for another play; keep same level selected
      playButton.setEnabled(true);
      questionsSpinner.setEnabled(true);
      levelCombo.setEnabled(true);
      stopButton.setEnabled(false);
      // Start immediately if you want to auto-start again; here we'll start immediately
      initializeGame();
    } else {
      // Keep the user on the game window so they can choose to Play again or Quit to Menu manually
      playButton.setEnabled(true);
      questionsSpinner.setEnabled(true);
      levelCombo.setEnabled(true);
      stopButton.setEnabled(false);
      correctButton.setEnabled(false);
      wrongButton.setEnabled(false);
      // Show a final score message in the main area (they can still press Quit to Menu)
      wordLabel.setText(String.format("Final score: %d/%d", score, totalQuestions));
      updateScoreLabel();
      updateProgressLabel();
    }
  }

  private void endGameAndReturnToMenu() {
    // Reset UI state
    correctButton.setEnabled(false);
    wrongButton.setEnabled(false);
    playButton.setEnabled(true);
    questionsSpinner.setEnabled(true);
    levelCombo.setEnabled(true);
    updateProgressLabel();

    // Hide this window and show parent
    setVisible(false);
    if (parentSelector != null) {
      parentSelector.setVisible(true);
    }
  }

  private void updateScoreLabel() {
    scoreLabel.setText(
        String.format("Score: %d/%d", score, Math.min(currentIndex, totalQuestions)));
    // Update remaining
    int remaining = Math.max(0, totalQuestions - currentIndex);
    remainingLabel.setText(String.format("Remaining: %d", remaining));
  }

  private void updateProgressLabel() {
    Object selected = levelCombo.getSelectedItem();
    if (selected instanceof ReadingLevel) {
      ReadingLevel rl = (ReadingLevel) selected;
      int totalWords = dictionary.getWordsForLevel(rl).size();
      int correctCount = correctWordsPerLevel.getOrDefault(rl, java.util.Set.of()).size();
      int percent = totalWords > 0 ? (int) Math.round(100.0 * correctCount / totalWords) : 0;
      progressLabel.setText(
          String.format("Progress: %d/%d (%d%%)", correctCount, totalWords, percent));
    } else {
      // "All" selection — no single-level progress to show
      progressLabel.setText("Progress: N/A");
    }
  }
}
