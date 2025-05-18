let appliedDiscount = 0;

function formatVND(number) {
    return number.toLocaleString("vi-VN") + "VND";
}
document.getElementById("apply-coupon").addEventListener("click", function () {
    const code = document.getElementById("coupon-code").value.trim().toUpperCase();

    if (code === "GIAM10") {
        appliedDiscount = 100000;
        alert("Áp dụng mã giảm thành công: GIAM10 (-100.000đ)");
    } else if (code === "SALE50") {
        appliedDiscount = 50000;
        alert("Áp dụng mã SALE50 (-50.000đ)");
    } else {
        appliedDiscount = 0;
        alert("Mã không hợp lệ hoặc đã hết hạn.");
    }

    updateCartTotal();
});

function updateCartTotal() {
    let subtotal = 0;
    document.querySelectorAll(".product-item").forEach((item) => {
        const quantity = parseInt(item.querySelector(".quantity-select").value);
        const unitPrice = 325000;
        subtotal += quantity * unitPrice;
    });

    let totalAfterDiscount = subtotal - appliedDiscount;
    if (totalAfterDiscount < 0) totalAfterDiscount = 0;

    document.getElementById("subtotal").innerText = formatVND(subtotal);
    document.getElementById("discount").innerText = formatVND(appliedDiscount);
    document.getElementById("cart-total").innerText = formatVND(totalAfterDiscount);
}

function checkCartEmpty() {
    const productItems = document.querySelectorAll(".product-item");
    const cartContainer = document.getElementById("cart-container");
    const emptyCart = document.getElementById("empty-cart");

    if (productItems.length === 0) {
        cartContainer.style.display = "none";
        emptyCart.style.display = "block";
    } else {
        cartContainer.style.display = "block";
        emptyCart.style.display = "none";
    }
}

// Gán sự kiện cho các dropdown chọn số lượng
document.querySelectorAll(".quantity-select").forEach((select) => {
    select.addEventListener("change", function () {
        const quantity = parseInt(this.value);
        const productItem = this.closest(".product-item");
        const priceDisplay = productItem.querySelector(".price");
        const unitPrice = 325000;
        const total = quantity * unitPrice;
        priceDisplay.innerText = formatVND(total);
        updateCartTotal();
    });
});

// Gán sự kiện cho nút xoá sản phẩm
document.querySelectorAll(".btn-remove").forEach((btn) => {
    btn.addEventListener("click", function () {
        const productItem = this.closest(".product-item");
        if (productItem) {
            productItem.remove();
            updateCartTotal();
            checkCartEmpty();
        }
    });
});

// Tính tổng và kiểm tra khi trang load
updateCartTotal();
checkCartEmpty();