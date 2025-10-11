$(document).ready(function() {

    // --- XỬ LÝ QUICK VIEW ---
    $(document).on('click', '.quick-view-btn', function(e) {
        e.preventDefault();
        var productId = $(this).data('product-id');

        $.ajax({
            url: '/api/product/' + productId,
            type: 'GET',
            success: function(product) {
                var modal = $('#quick-view-modal');
                // Cập nhật thông tin cơ bản
                modal.find('.product-title').text(product.tenSanPham);
                modal.find('.price-amount').text(product.giaBan.toLocaleString('vi-VN') + ' ₫');
                modal.find('.description').html(product.moTa);
                modal.find('.product-large-thumbnail .thumbnail:first img').attr('src', '/uploads/' + product.hinhAnh);
                modal.find('.product-small-thumb .small-thumb-img:first img').attr('src', '/uploads/' + product.hinhAnh);

                // Cập nhật form Thêm vào giỏ trong modal
                var cartForm = modal.find('.add-to-cart-form');
                if (cartForm.length) {
                    cartForm.find('input[name="productId"]').val(product.maSanPham);
                }
            },
            error: function() {
                alert('Không thể tải thông tin sản phẩm.');
            }
        });
    });

    // --- XỬ LÝ WISHLIST ---
    $(document).on('click', '.toggle-wishlist-btn', function(e) {
        e.preventDefault();
        var button = $(this);
        var productId = button.data('product-id');

        $.ajax({
            url: '/wishlist/toggle/' + productId,
            type: 'POST',
            success: function(response) {
                if (response.status === 'added') {
                    button.addClass('wishlist-added');
                } else if (response.status === 'removed') {
                    button.removeClass('wishlist-added');
                }
                $('.wishlist-count').text(response.count);
            },
            error: function(xhr) {
                if(xhr.status === 401) {
                    alert('Vui lòng đăng nhập để sử dụng chức năng này.');
                    window.location.href = '/sign-in';
                } else {
                    alert('Đã xảy ra lỗi. Vui lòng thử lại.');
                }
            }
        });
    });

    $(document).on('click', '.add-to-cart-btn-ajax', function(e) {
        e.preventDefault();
        var productId = $(this).data('product-id');

        // Tạo một form ẩn và submit nó
        var form = $('<form>', {
            'action': '/cart/add',
            'method': 'POST'
        }).append($('<input>', {
            'name': 'productId',
            'value': productId,
            'type': 'hidden'
        })).append($('<input>', {
            'name': 'quantity',
            'value': 1,
            'type': 'hidden'
        }));

        $('body').append(form);
        form.submit();
    });

});