// File: assets/js/products.js

document.addEventListener('DOMContentLoaded', function () {
    console.log("Custom JS for Admin pages loaded.");

    // === XỬ LÝ CHO TRANG DANH MỤC (CATEGORY) ===
    const categoriesTable = document.getElementById('categoriesTable');
    if (categoriesTable) {
        // Modal Sửa danh mục
        const editCategoryModal = document.getElementById('editCategoryModal');
        if (editCategoryModal) {
            editCategoryModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const id = button.getAttribute('data-id');
                const name = button.getAttribute('data-name');
                const image = button.getAttribute('data-image');
                const status = button.getAttribute('data-status') === 'true';

                // Gán dữ liệu vào các trường input trong form
                editCategoryModal.querySelector('[name="maDanhMuc"]').value = id;
                editCategoryModal.querySelector('[name="tenDanhMuc"]').value = name;
                editCategoryModal.querySelector('[name="hinhAnh"]').value = image || '';
                editCategoryModal.querySelector('[name="kichHoat"]').checked = status;

                // Xử lý hiển thị ảnh preview
                const imagePreview = editCategoryModal.querySelector('#editImagePreview');
                const noImageText = editCategoryModal.querySelector('#noImageText');
                if (image && image.trim() !== '') {
                    imagePreview.src = '/uploads/' + image;
                    imagePreview.style.display = 'block';
                    noImageText.style.display = 'none';
                } else {
                    imagePreview.style.display = 'none';
                    noImageText.style.display = 'block';
                }
            });
        }
    }

    // === XỬ LÝ CHO TRANG THƯƠNG HIỆU (BRAND) ===
    const brandsTable = document.getElementById('brandsTable');
    if (brandsTable) {
        // Modal Sửa thương hiệu
        const editBrandModal = document.getElementById('editBrandModal');
        if (editBrandModal) {
            editBrandModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const id = button.getAttribute('data-id');
                const name = button.getAttribute('data-name');
                const description = button.getAttribute('data-description');
                const image = button.getAttribute('data-image');
                const status = button.getAttribute('data-status') === 'true';

                // Gán dữ liệu vào các trường input trong form
                const modalForm = editBrandModal.querySelector('form');
                modalForm.querySelector('[name="maThuongHieu"]').value = id;
                modalForm.querySelector('[name="tenThuongHieu"]').value = name;
                modalForm.querySelector('textarea[name="moTa"]').value = description;
                modalForm.querySelector('[name="kichHoat"]').checked = status;
                modalForm.querySelector('input[name="hinhAnh"]').value = image || '';

                // Xử lý hiển thị ảnh preview
                const imagePreview = modalForm.querySelector('#editImagePreview');
                const noImageText = modalForm.querySelector('#noImageText');
                if (image && image.trim() !== '') {
                    imagePreview.src = '/uploads/' + image;
                    imagePreview.style.display = 'block';
                    noImageText.style.display = 'none';
                } else {
                    imagePreview.style.display = 'none';
                    noImageText.style.display = 'block';
                }
            });
        }
    }
});