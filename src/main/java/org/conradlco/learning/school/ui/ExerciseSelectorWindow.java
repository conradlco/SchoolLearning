package org.conradlco.learning.school.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ExerciseSelectorWindow extends JFrame implements ActionListener {

  private static final String GREATER_THAN_GAME = "Greater than Game";
  private static final String WORD_READING_GAME = "Word Reading";

  private JList<String> selectionList;
  private JButton playButton;

  public ExerciseSelectorWindow() {
    super("School Exercise Selector");

    setBounds(400, 200, 400, 400);

    setDefaultCloseOperation(EXIT_ON_CLOSE);

    buildLayout(this.getContentPane());
  }

  private void buildLayout(final Container mainContainer) {
    mainContainer.setLayout(new BorderLayout());

    JPanel selectionPanel = new JPanel();

    selectionList = new JList<>(getGamesList());
    selectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    selectionList.setVisibleRowCount(8);

    JScrollPane listScroll = new JScrollPane(selectionList);
    listScroll.setPreferredSize(new Dimension(300, 200));
    selectionPanel.setLayout(new BorderLayout());
    selectionPanel.add(listScroll, BorderLayout.CENTER);
    selectionPanel.setBorder(BorderFactory.createTitledBorder("Exercise Selector"));
    mainContainer.add(selectionPanel, BorderLayout.CENTER);

    playButton = new JButton("Play");
    playButton.addActionListener(this);
    mainContainer.add(playButton, BorderLayout.SOUTH);
  }

  private DefaultListModel<String> getGamesList() {
    DefaultListModel<String> gamesListModel = new DefaultListModel<>();
    gamesListModel.add(0, GREATER_THAN_GAME);
    gamesListModel.add(1, WORD_READING_GAME);

    return gamesListModel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == playButton && selectionList.getSelectedIndex() != -1) {
      this.setVisible(false);
      if (GREATER_THAN_GAME.equals(selectionList.getSelectedValue())) {
        SwingUtilities.invokeLater(() -> new WhichIsGreaterWindow(this).setVisible(true));
      } else if (WORD_READING_GAME.equals(selectionList.getSelectedValue())) {
        SwingUtilities.invokeLater(() -> new WordReading(this).setVisible(true));
      }
    }
  }
}
