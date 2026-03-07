package org.conradlco.learning.school.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ExerciseSelectorController {

  @GetMapping
  public String exercises(Model model) {
    // The template will fetch levels and search via AJAX
    return "exercise_list";
  }
}
