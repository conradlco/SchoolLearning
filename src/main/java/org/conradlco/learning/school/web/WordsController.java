package org.conradlco.learning.school.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WordsController {
  @GetMapping("/words-by-sound")
  public String wordsBySound(Model model) {
    // The template will fetch levels and search via AJAX
    return "words_by_sound";
  }
}
