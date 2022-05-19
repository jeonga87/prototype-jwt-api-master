package kr.co.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Class Name : IndexController.java
 * Description : dashboard
 * Writer : lee.j
 */

@Controller
public class IndexController {

  @RequestMapping({"", "/"})
  public String index() {
    return "redirect:/index";
  }

}
