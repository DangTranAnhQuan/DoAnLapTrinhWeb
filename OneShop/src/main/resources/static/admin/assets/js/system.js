document.addEventListener('DOMContentLoaded', function () {
    const carriersTable = document.getElementById('carriersTable');
    if (carriersTable) {
        const editModal = document.getElementById('editCarrierModal');
        editModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-id');
            const name = button.getAttribute('data-name');
            const phone = button.getAttribute('data-phone');
            const website = button.getAttribute('data-website');

            const modalForm = editModal.querySelector('form');
            modalForm.querySelector('[name="maNVC"]').value = id;
            modalForm.querySelector('[name="tenNVC"]').value = name;
            modalForm.querySelector('[name="soDienThoai"]').value = phone;
            modalForm.querySelector('[name="website"]').value = website;
        });
    }

    const tiersTable = document.getElementById('tiersTable');
        if (tiersTable) {
            const editModal = document.getElementById('editTierModal');
            editModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const id = button.getAttribute('data-id');
                const name = button.getAttribute('data-name');
                const points = button.getAttribute('data-points');
                const discount = button.getAttribute('data-discount');

                const modalForm = editModal.querySelector('form');
                modalForm.querySelector('[name="maHangThanhVien"]').value = id;
                modalForm.querySelector('[name="tenHang"]').value = name;
                modalForm.querySelector('[name="diemToiThieu"]').value = points;
                modalForm.querySelector('[name="phanTramGiamGia"]').value = discount;
            });
        }
});