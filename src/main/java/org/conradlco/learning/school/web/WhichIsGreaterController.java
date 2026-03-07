package org.conradlco.learning.school.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WhichIsGreaterController {
  @GetMapping("/which-is-greater")
  public String whichIsGreater(Model model) {
    return "which_is_greater";
  }
}
