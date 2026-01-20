package org.conradlco.learning.school.ui;

import org.conradlco.learning.school.words.Dictionary;
import org.conradlco.learning.school.words.ReadingLevel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordsBySound extends JFrame {
  private final ExerciseSelectorWindow parentSelector;
  private final List<String> sounds = List.of("oo", "oi");

  private Dictionary dictionary;

  // UI components
  private JComboBox<Object> levelCombo;
  private JTextField soundField;
  private JButton showButton;

  // Results as a panel with 3 columns and limited rows (no scroll)
  private JPanel resultsPanel;
  private final int OPTION_FONT_SIZE = 16; // slightly larger
  private final int MAX_ROWS = 8; // controls maximum visible rows; capacity = MAX_ROWS * 3

  public WordsBySound(ExerciseSelectorWindow parentSelector) {
    super("Word By Sounds");
    this.parentSelector = parentSelector;

    this.dictionary = Dictionary.getInstance();

    setBounds(400, 200, 600, 400);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    buildLayout(this.getContentPane());
  }

  private void buildLayout(Container container) {
    container.setLayout(new BorderLayout(10, 10));

    // Top panel: level selection and sound input
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
    for (ReadingLevel rl : ReadingLevel.values()) {
      model.addElement(rl);
    }
    model.addElement("All");
    levelCombo = new JComboBox<>(model);

    soundField = new JTextField(10);
    soundField.setToolTipText("Enter the sound to search for (e.g. 'oo')");
    // Slightly larger, bold font for the sound input as requested
    soundField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

    showButton = new JButton("Show");

    topPanel.add(new JLabel("Level:"));
    topPanel.add(levelCombo);
    topPanel.add(new JLabel("Sound of the Week:"));
    topPanel.add(soundField);
    topPanel.add(showButton);

    container.add(topPanel, BorderLayout.NORTH);

    // Center: results panel (3 columns, limited rows) - no scroll pane
    resultsPanel = new JPanel();
    resultsPanel.setLayout(new GridLayout(MAX_ROWS, 3, 8, 8));
    // initialize with empty placeholders so layout size is stable
    for (int i = 0; i < MAX_ROWS * 3; i++) {
      resultsPanel.add(new JLabel(""));
    }
    container.add(resultsPanel, BorderLayout.CENTER);

    // Wire action for Show button
    showButton.addActionListener(e -> onShow());

    // Live validation: enable/disable Show button depending on whether any matches exist
    soundField.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        updateShowButtonEnabled();
      }

      public void removeUpdate(DocumentEvent e) {
        updateShowButtonEnabled();
      }

      public void changedUpdate(DocumentEvent e) {
        updateShowButtonEnabled();
      }
    });

    levelCombo.addActionListener(e -> updateShowButtonEnabled());

    // initialize button state
    updateShowButtonEnabled();
  }

  private List<String> findMatches(String sound) {
    if (sound == null) return List.of();
    String s = sound.trim().toLowerCase();
    if (s.isEmpty()) return List.of();

    Object selected = levelCombo.getSelectedItem();
    List<String> candidates = new ArrayList<>();

    if (selected instanceof ReadingLevel) {
      ReadingLevel rl = (ReadingLevel) selected;
      candidates.addAll(dictionary.getWordsForLevel(rl));
    } else {
      for (ReadingLevel rl : ReadingLevel.values()) {
        candidates.addAll(dictionary.getWordsForLevel(rl));
      }
    }

    List<String> matches = new ArrayList<>();
    for (String w : candidates) {
      if (w != null && w.toLowerCase().contains(s)) {
        matches.add(w);
      }
    }

    Collections.sort(matches, String.CASE_INSENSITIVE_ORDER);
    return matches;
  }

  private void updateShowButtonEnabled() {
    String sound = soundField.getText();
    List<String> matches = findMatches(sound);
    showButton.setEnabled(!matches.isEmpty());
  }

  private JButton makeOptionButton(String word) {
    JButton b = new JButton(word);
    b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, OPTION_FONT_SIZE));
    b.addActionListener(ae -> {
      ReadingLevel rl = dictionary.getLevelOfWord(word);
      String lvl = rl != null ? rl.name() : "Unknown";
      JOptionPane.showMessageDialog(WordsBySound.this, word + " (" + lvl + ")", "Word", JOptionPane.INFORMATION_MESSAGE);
    });
    return b;
  }

  private void onShow() {
    String sound = soundField.getText();
    if (sound == null || sound.trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Please enter a sound to search for.", "Input required", JOptionPane.WARNING_MESSAGE);
      return;
    }

    List<String> matches = findMatches(sound);

    if (matches.isEmpty()) {
      // Shouldn't normally happen because updateShowButtonEnabled prevents pressing, but guard anyway
      // clear the results and inform the user
      resultsPanel.removeAll();
      for (int i = 0; i < MAX_ROWS * 3; i++) {
        resultsPanel.add(new JLabel(""));
      }
      resultsPanel.revalidate();
      resultsPanel.repaint();
      JOptionPane.showMessageDialog(this, "No words found containing '" + sound + "' for the selected level.", "No Matches", JOptionPane.INFORMATION_MESSAGE);
      showButton.setEnabled(false);
      return;
    }

    // Populate up to capacity (MAX_ROWS * 3). Ignore overflow (no scroll panes per requirement)
    int capacity = MAX_ROWS * 3;
    resultsPanel.removeAll();
    int shown = 0;
    for (String m : matches) {
      if (shown >= capacity) break;
      resultsPanel.add(makeOptionButton(m));
      shown++;
    }
    // Fill remaining slots with empty labels to keep a tidy grid
    for (int i = shown; i < capacity; i++) {
      resultsPanel.add(new JLabel(""));
    }

    resultsPanel.revalidate();
    resultsPanel.repaint();
  }
}
