package moimmoimProject.controller.ticketController;

import moimmoimProject.domain.moimDomain.MoimDo;
import moimmoimProject.domain.ticketDomain.OrderDo;
import moimmoimProject.domain.userDomain.UserDo;
import moimmoimProject.mapper.MoimMapper;
import moimmoimProject.mapper.OrderMapper;
import moimmoimProject.mapper.UserMapper;
import moimmoimProject.service.ticketService.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.nio.file.Files;


@Controller
@RequestMapping
public class BuyPageController {

    private final OrderService orderService;
    private final MoimMapper moimMapper;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;


    public BuyPageController(OrderService orderService, MoimMapper moimMapper, UserMapper userMapper, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.moimMapper = moimMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;

    }
    @GetMapping("/buyPage/{moimNum}")
    public String showBuyPage(@PathVariable Long moimNum, @RequestParam Long userIdNum, Model model) {

        UserDo userDo = userMapper.findByUserIdNum(userIdNum);
        MoimDo moimDo = moimMapper.findAllByMoimNum(moimNum);

        int maximumCapacity = moimDo.getMoimMemberMax() - moimDo.getMoimMemberCount();



        //정원이 다찼는지 비교
        if(moimDo.getMoimMemberMax() == moimDo.getMoimMemberCount()){
           // 정원이 다찼으면 구매 불가능
            model.addAttribute("errorMsg", "해당 모임은 정원이 가득 찼습니다.");
            return "/ticketService/ticketErrorPage";
        }
        else {
            // 결제 진행중인 주문 목록의 수를 가져옵니다.
            int countOfOrderInProgress = orderMapper.countByMoimNumAndOrderStatus(moimNum, "결제 진행중");

            // 결제 진행중인 주문 목록의 수와 최대 정원 수를 비교합니다.
            if (countOfOrderInProgress >= maximumCapacity) {
                // 결제 진행중인 주문 목록의 수가 남는 티켓 수보다 크면 구매가 불가능합니다.
                model.addAttribute("errorMsg", "다른 회원이 결제중입니다. 나중에 다시 시도해주세요.");
                return "/ticketService/ticketErrorPage";
            }
            else{
                // createOrderDo() 호출하여 OrderDo 객체 생성 후 orderNum 값을 가져와서 세션에 저장
                OrderDo orderDo = orderService.createOrderDo(moimDo.getMoimNum(), userDo.getUserIdNum(), moimDo.getMoimPrice());
                model.addAttribute("orderNum", orderDo.getOrderNum()); // 세션 대신 모델에 주문 번호 추가
            }
        }


        model.addAttribute("userDo", userDo);
        model.addAttribute("moimDo", moimDo);

        return "ticketService/buyPage";
    }


    @PostMapping("/cancelOrder")
    @ResponseBody
    public String cancelOrder(@RequestParam String orderNum) {
        if (orderNum != null) {
            orderService.cancelOrder(orderNum);
            return "주문이 취소되었습니다.";
        } else {
            return "주문이 존재하지 않습니다.";
        }
    }
}
