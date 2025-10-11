package nhom17.OneShop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nhom17.OneShop.request.DashboardDataDTO;
import nhom17.OneShop.service.OrderService;
import nhom17.OneShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;

    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(@RequestParam(required = false) Integer year,
                                @RequestParam(required = false) Integer month,
                                Model model) {

        LocalDate now = LocalDate.now();
        // Nếu không có năm hoặc tháng được chọn, lấy năm và tháng hiện tại làm mặc định
        int selectedYear = (year == null || year < 1970) ? now.getYear() : year;
        int selectedMonth = (month == null || month < 1 || month > 12) ? now.getMonthValue() : month;

        // Gọi service để lấy toàn bộ dữ liệu thống kê
        DashboardDataDTO dashboardData = orderService.getDashboardData(selectedYear, selectedMonth);

        // Truyền dữ liệu ra cho view
        model.addAttribute("dashboardData", dashboardData);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("selectedMonth", selectedMonth);

        return "admin/dashboard";
    }
}