// Quick View: đọc DB -> đổ vào modal
(function($){
  function formatVND(n){
    if (n == null) return "";
    return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") + " ₫";
  }

  function renderStars(avg){
    if (avg == null) return "Chưa có đánh giá";
    var full = Math.floor(avg), half = (avg - full) >= 0.5;
    var s = "";
    for (var i=0;i<full;i++) s += "★ ";
    if (half) s += "☆ "; // hoặc nửa sao nếu bạn có icon
    for (var i=s.trim().split(" ").length; i<5; i++) s += "☆ ";
    return s.trim();
  }

  $(document).on('click', '.quick-view-btn', function(e){
    e.preventDefault();
    var id = $(this).data('product-id');

    // reset nhanh
    $('#qv-image').attr('src','');
    $('#qv-title,#qv-price,#qv-oldprice,#qv-desc,#qv-review-count').text('');
    $('#qv-stock').removeClass('text-bg-danger').addClass('text-bg-success').text('In stock');

    $.getJSON('/api/quick-view/' + id, function(d){
      // Ảnh
      if (d.images && d.images.length) $('#qv-image').attr('src', d.images[0]);

      // Text
      $('#qv-title').text(d.name);
      $('#qv-price').text(formatVND(d.price));
      $('#qv-oldprice').text(d.oldPrice && d.oldPrice > d.price ? formatVND(d.oldPrice) : '');
      $('#qv-desc').text(d.shortDesc || '');
      $('#qv-review-count').text(d.reviewCount ? '(' + d.reviewCount + ' đánh giá)' : '');

      // Stock
      if (!d.inStock){
        $('#qv-stock').removeClass('text-bg-success').addClass('text-bg-danger').text('Hết hàng');
      }

      // Rating
      $('#qv-rating').text(renderStars(d.rating));

      // Link sang trang chi tiết
      $('#qv-detail-link').attr('href', '/product/' + d.id);

      // Add-to-cart: gắn id để script cart của bạn xử lý
      $('#qv-add-cart').data('product-id', d.id);
    }).fail(function(){
      $('#qv-title').text('Không tải được dữ liệu sản phẩm.');
    });
  });

  // Ví dụ: nút "Thêm vào giỏ" trong modal (nếu bạn đã có handler chung thì bỏ phần này)
  $(document).on('click', '#qv-add-cart', function(){
    var id = $(this).data('product-id');
    // Gọi AJAX add-to-cart hiện có của bạn
    $('.add-to-cart-btn-ajax[data-product-id="'+id+'"]').trigger('click');
  });

})(jQuery);
