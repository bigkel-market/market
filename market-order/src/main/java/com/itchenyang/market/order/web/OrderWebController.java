package com.itchenyang.market.order.web;

import com.itchenyang.common.exception.NoStockException;
import com.itchenyang.market.order.service.OrderService;
import com.itchenyang.market.order.vo.OrderConfirmVo;
import com.itchenyang.market.order.vo.OrderSubmitVo;
import com.itchenyang.market.order.vo.SubmitOrderResponseVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @author BigKel
 * @createTime 2022/11/30
 */
@Controller
public class OrderWebController {

    @Resource
    private OrderService orderService;

    @GetMapping(value = "/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo vo = orderService.confirmOrder();
        model.addAttribute("confirmOrderData", vo);
        return "confirm";
    }

    @PostMapping(value = "/submitOrder")
    public String orderSubmit(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {
        try {
            SubmitOrderResponseVo responseVo = orderService.orderSubmit(vo);
            if (responseVo.getCode() == 0) {
                model.addAttribute("submitOrderResp", responseVo);
                return "pay";
            } else {
                String msg = "下单失败, ";
                switch (responseVo.getCode()) {
                    case 1: msg += "令牌过期"; break;
                    case 2: msg += "验价失败"; break;
                    default: msg += "商品无库存"; break;
                }
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.bigkel.com/toTrade";
            }
        } catch (NoStockException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:http://order.bigkel.com/toTrade";
        }
    }
}
