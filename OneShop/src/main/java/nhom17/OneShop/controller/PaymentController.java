package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.NoSuchElementException;

@Controller
public class PaymentController {

    @Autowired
    private OrderRepository orderRepository;

    // Inject các giá trị từ application.properties
    @Value("${shop.vietqr.bank-bin}")
    private String SHOP_BANK_BIN;

    @Value("${shop.vietqr.account-no}")
    private String SHOP_ACCOUNT_NO;

    @Value("${shop.vietqr.account-name}")
    private String SHOP_ACCOUNT_NAME;


    /**
     * HÀM MỚI: Hiển thị trang thanh toán VietQR
     */
    @GetMapping("/thanh-toan/qr")
    public String showVietQRPage(@RequestParam("orderId") Long orderId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy đơn hàng " + orderId));

            // Kiểm tra xem đơn hàng đã thanh toán chưa
            if ("Đã thanh toán".equalsIgnoreCase(order.getTrangThaiThanhToan())) {
                redirectAttributes.addFlashAttribute("info", "Đơn hàng này đã được thanh toán.");
                return "redirect:/order-details/" + orderId;
            }

            // Chỉ cho phép thanh toán QR nếu PTTT là VIETQR và Chưa thanh toán
            if (!"ONLINE".equalsIgnoreCase(order.getPhuongThucThanhToan()) || !"Chưa thanh toán".equalsIgnoreCase(order.getTrangThaiThanhToan())) {
                redirectAttributes.addFlashAttribute("error", "Đơn hàng này không hợp lệ để thanh toán QR.");
                return "redirect:/order-details/" + orderId;
            }

            // Nội dung chuyển khoản (VD: "DH123")
            String paymentMemo = "DH" + order.getMaDonHang();

            // Truyền thông tin ra view
            model.addAttribute("order", order);
            model.addAttribute("shopBankBin", SHOP_BANK_BIN);
            model.addAttribute("shopAccountNo", SHOP_ACCOUNT_NO);
            model.addAttribute("shopAccountName", SHOP_ACCOUNT_NAME);
            model.addAttribute("paymentMemo", paymentMemo); // Nội dung chuyển khoản

            return "user/shop/payment-vietqr"; // Tên của file HTML mới (Bước 5)

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/my-orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi tạo mã QR.");
            return "redirect:/checkout";
        }
    }

    /**
     * HÀM MỚI: Xử lý khi người dùng nhấn "Tôi đã thanh toán" (Giải pháp 1)
     */
    @PostMapping("/payment/confirm-transfer")
    public String confirmTransfer(@RequestParam("orderId") Long orderId, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy đơn hàng " + orderId));

            // Chỉ chuyển hướng đến trang chi tiết đơn hàng
            redirectAttributes.addFlashAttribute("success", "Đã gửi yêu cầu xác nhận. Chúng tôi sẽ kiểm tra và xử lý đơn hàng của bạn sớm nhất.");
            return "redirect:/order-details/" + orderId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/my-orders";
        }
    }
}