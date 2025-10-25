document.addEventListener('DOMContentLoaded', function () {

    const suppliersTable = document.getElementById('suppliersTable');
    if (suppliersTable) {
        const editModal = document.getElementById('editSupplierModal');
        editModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name');
            const phone = button.getAttribute('data-phone');
            const address = button.getAttribute('data-address');

            const modalForm = editModal.querySelector('form');
            modalForm.querySelector('[name="maNCC"]').value = id;
            modalForm.querySelector('[name="tenNCC"]').value = name;
            modalForm.querySelector('[name="sdt"]').value = phone;
            modalForm.querySelector('[name="diaChi"]').value = address;
        });
    }

    function formatNumber(numStr) {
        if (!numStr) return '';
        const num = parseInt(numStr.toString().replace(/[^0-9]/g, ''), 10);
        if (isNaN(num)) return '';
        return new Intl.NumberFormat('vi-VN').format(num);
    }

    const importForm = document.getElementById('import-form');
    if (importForm) {
        const container = document.getElementById('details-container');
        const template = document.getElementById('detail-row-template');
        const addButton = document.getElementById('add-detail-row');
        let rowIndex = 0;

        // Hàm để thêm một dòng mới vào bảng
        function addRow(detail) {
            const newRowFragment = template.content.cloneNode(true);
            const tr = newRowFragment.querySelector('tr');

            // Lấy các thẻ input/select trong dòng mới
            const productSelect = tr.querySelector('.product-select');
            const quantityInput = tr.querySelector('.quantity-input');
            const priceInput = tr.querySelector('.price-input');
            const priceDisplay = tr.querySelector('.current-price-display');

            // THÊM MỚI: Lấy thẻ <a> "Xem lịch sử"
            const historyLink = tr.querySelector('.history-link');

            // Cập nhật thuộc tính 'name'
            productSelect.name = `chiTietPhieuNhapList[${rowIndex}].maSanPham`;
            quantityInput.name = `chiTietPhieuNhapList[${rowIndex}].soLuong`;
            priceInput.name = `chiTietPhieuNhapList[${rowIndex}].giaNhap`;

            // Nếu có dữ liệu ban đầu (chế độ Sửa)
            if(detail) {
                productSelect.value = detail.maSanPham;
                quantityInput.value = detail.soLuong;
                priceInput.value = detail.giaNhap;

                // Xử lý giá bán
                const selectedOption = productSelect.querySelector(`option[value="${detail.maSanPham}"]`);
                if (priceDisplay && selectedOption) {
                    priceDisplay.value = formatNumber(selectedOption.dataset.price);
                }

                // THÊM MỚI: Xử lý nút "Xem lịch sử" khi tải trang
                if (historyLink && detail.maSanPham) {
                    // Lấy link gốc từ 'data-base-href'
                    const baseUrl = historyLink.dataset.baseHref;
                    historyLink.href = baseUrl + detail.maSanPham;
                    historyLink.style.display = 'inline-block'; // Hiển thị nút
                }
            }

            container.appendChild(newRowFragment);
            rowIndex++;
        }

        // Code xử lý tải trang (chế độ Sửa hoặc Thêm mới)
        if (typeof initialDetails !== 'undefined' && initialDetails && initialDetails.length > 0) {
            initialDetails.forEach(detail => addRow(detail));
        } else {
            addRow(null);
        }

        // Nút "Thêm sản phẩm"
        addButton.addEventListener('click', () => addRow(null));

        // Nút "Xóa"
        container.addEventListener('click', function (event) {
            if (event.target && event.target.closest('.remove-detail-row')) {
                event.target.closest('tr').remove();
            }
        });

        container.addEventListener('change', function(event) {
        if (event.target && event.target.classList.contains('product-select')) {
            const selectedOption = event.target.options[event.target.selectedIndex];
            const price = selectedOption.dataset.price;
            const row = event.target.closest('tr');
            const priceDisplay = row.querySelector('.current-price-display');

            // THÊM MỚI: Lấy nút "Xem" và ID sản phẩm
            const historyLink = row.querySelector('.history-link');
            const productId = selectedOption.value; // Lấy ID

            // Cập nhật giá bán
            if (priceDisplay) {
                priceDisplay.value = formatNumber(price);
            }

            // THÊM MỚI: Logic hiển thị / ẩn nút "Xem"
            if (historyLink) {
                if (productId) {
                    // 1. Nếu có ID (đã chọn 1 sản phẩm)
                    const baseUrl = historyLink.dataset.baseHref;
                    historyLink.href = baseUrl + productId;
                    historyLink.style.display = 'inline-block'; // HIỆN
                } else {
                    // 2. Nếu không có ID (chọn "-- Chọn sản phẩm --")
                    historyLink.href = '#';
                    historyLink.style.display = 'none'; // ẨN
                }
            }
        }
    });
}
});