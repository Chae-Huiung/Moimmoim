package moimmoimProject.controller.testPageController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class testHomeController {

    @GetMapping("/")
    public String testHome() {
        return "/testHome";
    }

    @GetMapping("/ticket")
    public String testTicket() {
        return "/ticketService/ticket";
    }
}
