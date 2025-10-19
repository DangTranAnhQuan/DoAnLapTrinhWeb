(function ($) {
    'use strict';

    // Xử lý khi nhấn nút Quick View
    $('body').on('click', '.quick-view-btn', function (e) {
        e.preventDefault();
        var productId = $(this).data('product-id');
        var quickViewModal = $('#quick-view-modal');

        quickViewModal.find('.modal-body').html('<div class="text-center p-5"><div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div></div>');
        quickViewModal.modal('show');

        $.ajax({
            url: '/api/quick-view/' + productId,
            type: 'GET',
            success: function (product) {
                var formContent = '';
                // Nút yêu thích sẽ dùng chung cho cả 2 trường hợp
                var wishlistBtn = `
                    <a href="javascript:void(0)" class="qv-wishlist-btn toggle-wishlist-btn" data-product-id="${product.id}" title="Thêm vào danh sách yêu thích">
                        <i class="far fa-heart"></i>
                    </a>
                `;

                if (product.soLuongTon > 0) {
                    // Nếu CÒN HÀNG
                    formContent = `
                        <form action="/cart/add" method="POST" class="d-flex align-items-center gap-3">
                            <input type="hidden" name="productId" value="${product.id}">
                            <div class="product-variation">
                                <div class="cart-quantity">
                                    <div class="pro-qty">
                                        <span class="dec qtybtn">-</span>
                                        <input type="number" name="quantity" class="cart-plus-minus-box" value="1" min="1" max="${product.soLuongTon}">
                                        <span class="inc qtybtn">+</span>
                                    </div>
                                </div>
                            </div>
                            <div class="product-add-to-cart d-flex align-items-center gap-3">
                                <button type="submit" class="axil-btn btn-bg-primary">Thêm vào giỏ</button>
                                ${wishlistBtn}
                            </div>
                        </form>
                    `;
                } else {
                    // Nếu HẾT HÀNG
                    formContent = `
                         <div class="product-add-to-cart d-flex align-items-center gap-3">
                            <button type="button" class="axil-btn btn-bg-secondary" disabled>Hết hàng</button>
                            ${wishlistBtn}
                        </div>
                    `;
                }

                var modalBody = `
                    <div class="row">
                        <div class="col-lg-6">
                             <div class="pro-large-img">
                                <img src="${product.images[0]}" alt="${product.name}" />
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="product-details-content">
                                <h2 class="product-details-title">${product.name}</h2>
                                <div class="product-details-rating">
                                    <div class="rating-list d-inline-block">${renderStars(product.rating)}</div>
                                    <span class="review-count">(${product.reviewCount} lượt đánh giá)</span>
                                </div>
                                <div class="product-details-price-wrapper">
                                    <span class="price current-price">${formatCurrency(product.price)}</span>
                                    ${product.oldPrice > product.price ? `<span class="price old-price">${formatCurrency(product.oldPrice)}</span>` : ''}
                                </div>
                                <div class="product-details-meta"><ul>
                                    <li><span class="meta-title">THƯƠNG HIỆU:</span> ${product.brandName || 'N/A'}</li>
                                    <li><span class="meta-title">Tình trạng:</span> <span class="${product.inStock ? 'text-success' : 'text-danger'}">${product.inStock ? 'Còn hàng' : 'Hết hàng'}</span></li>
                                </ul></div>
                                <p class="product-details-short-desc">${product.shortDesc || ''}</p>
                                ${formContent}
                                <div class="product-details-action-wrapper mt-4">
                                    <a href="/product/${product.id}" class="btn-link">Xem chi tiết sản phẩm <i class="far fa-long-arrow-right"></i></a>
                                </div>
                            </div>
                        </div>
                    </div>
                `;

                quickViewModal.find('.modal-body').html(modalBody);

                if ($('.pro-qty').length) {
                    $('.qtybtn').on('click', function () {
                        var $button = $(this);
                        var input = $button.parent().find('input');
                        var oldValue = parseFloat(input.val());
                        var max = parseFloat(input.attr('max'));
                        var newVal;
                        if ($button.hasClass('inc')) {
                            newVal = oldValue + 1;
                            if (newVal > max) newVal = max;
                        } else {
                            newVal = (oldValue > 1) ? oldValue - 1 : 1;
                        }
                        input.val(newVal);
                    });
                }
            },
            error: function (error) {
                quickViewModal.find('.modal-body').html('<p class="text-center text-danger p-5">Lỗi tải thông tin sản phẩm.</p>');
                console.error("Quick View Error:", error);
            }
        });
    });

    // Dùng event delegation để xử lý click cho nút trái tim được tạo động
    $('body').on('click', '#quick-view-modal .toggle-wishlist-btn', function(e) {
        e.preventDefault();
        var $this = $(this);
        var icon = $this.find('i');

        // Chuyển đổi trạng thái active
        $this.toggleClass('active');

        // Chuyển đổi icon giữa rỗng (far) và đặc (fas)
        if ($this.hasClass('active')) {
            icon.removeClass('far').addClass('fas');
        } else {
            icon.removeClass('fas').addClass('far');
        }
    });

    function formatCurrency(number) {
        if (isNaN(number)) return "0 ₫";
        return number.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }).replace(/\s/g, '');
    }

    function renderStars(rating) {
        if (rating == null || rating <= 0) return Array(5).fill('<span><i class="far fa-star"></i></span>').join('');
        let stars = '', fullStars = Math.floor(rating), halfStar = rating % 1 >= 0.5;
        let emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
        for (let i = 0; i < fullStars; i++) stars += '<span><i class="fas fa-star text-warning"></i></span>';
        if (halfStar) stars += '<span><i class="fas fa-star-half-alt text-warning"></i></span>';
        for (let i = 0; i < emptyStars; i++) stars += '<span><i class="far fa-star text-warning"></i></span>';
        return stars;
    }
})(jQuery);