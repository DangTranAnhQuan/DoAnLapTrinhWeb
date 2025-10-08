document.addEventListener('DOMContentLoaded', function() {
    // Khởi tạo date range picker
    if ($('#orderDateRange').length) {
        $('#orderDateRange').daterangepicker({
            locale: {
                format: 'DD/MM/YYYY',
                applyLabel: 'Áp dụng',
                cancelLabel: 'Hủy',
                fromLabel: 'Từ',
                toLabel: 'Đến',
                customRangeLabel: 'Tùy chọn',
                daysOfWeek: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
                monthNames: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'],
                firstDay: 1
            },
            opens: 'left',
            autoUpdateInput: false
        });

        $('#orderDateRange').on('apply.daterangepicker', function(ev, picker) {
            $(this).val(picker.startDate.format('DD/MM/YYYY') + ' - ' + picker.endDate.format('DD/MM/YYYY'));
            // Gọi hàm lọc đơn hàng
            // filterOrders();
        });

        $('#orderDateRange').on('cancel.daterangepicker', function(ev, picker) {
            $(this).val('');
            // Gọi hàm lọc đơn hàng
            // filterOrders();
        });
    }

    // Khởi tạo bảng đơn vị vận chuyển
    if ($('.datatables-shipping').length) {
        // initShippingTable();
    }

    // Xử lý form thêm/sửa đơn vị vận chuyển
    if ($('#shippingForm, #editShippingForm').length) {
        // initShippingForm();
    }

    // Xử lý nút xóa đơn vị vận chuyển
    $('.delete-shipping').on('click', function(e) {
        e.preventDefault();
        const shippingId = $(this).data('id');
        // confirmDeleteShipping(shippingId);
    });

    // Xử lý nút kích hoạt/vô hiệu hóa đơn vị vận chuyển
    $('.activate-shipping, .deactivate-shipping').on('click', function(e) {
        e.preventDefault();
        const shippingId = $(this).data('id');
        const activate = $(this).hasClass('activate-shipping');
        // toggleShippingStatus(shippingId, activate);
    });

    // Xử lý lọc đơn hàng
    $('#filterStatus, #filterOrderStatus').on('change', function() {
        // filterOrders();
    });

    // Xử lý xuất Excel
    $('#btnExport').on('click', function() {
        // exportToExcel();
    });

    // Logic cho nút tạo mã khuyến mãi
    const generateBtn = document.getElementById('generate-voucher-code-btn');
    const codeInput = document.querySelector('input[name="maKhuyenMai"]');

    if (generateBtn && codeInput && !codeInput.readOnly) {
        generateBtn.addEventListener('click', function() {
            const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
            let result = '';
            for (let i = 0; i < 10; i++) {
                result += chars.charAt(Math.floor(Math.random() * chars.length));
            }
            codeInput.value = result;
        });
    }
});