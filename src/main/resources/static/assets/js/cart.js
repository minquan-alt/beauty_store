let appliedDiscount = 0;

function formatVND(number) {
    return number.toLocaleString("vi-VN") + "VND";
}

document.getElementById("apply-coupon").addEventListener("click", async function () {
    const code = document.getElementById("coupon-code").value.trim().toUpperCase();
    var shippingFee;
    var discountTotalCart;
    var totalCart = await fetch(`http://localhost:8080/cart`, {
            method: "GET",
        })
        .then(res => res.json())
        .then(data => {
            if (data.code == 1000) {
                return data.result.totalCartPrice;
            } else {
                return null;
            }
        })
        .catch(e => {
            console.log("Error: ", e);
        })
    console.log("totalCart: ", totalCart)

    if (code != null && code != "") {
        await fetch(`http://localhost:8080/coupons/${code}`, {
                method: "GET",
            })
            .then(res => res.json())
            .then(data => {
                console.log(data.type);
                const result = data.result;
                if (data.code == 1000) {
                    if (result.type == "PERCENTAGE") {
                        discountTotalCart = totalCart * (100 - result.discountValue) / 100;
                        alert(`Applied coupon successfully: ${code} (-${totalCart - discountTotalCart} $)`);

                    } else if (result.type == "FIXED_AMOUNT") {
                        discountTotalCart = totalCart - result.discountValue;
                        alert(`Applied coupon successfully: ${code} (-${totalCart - discountTotalCart} $)`);

                    } else if (result.type == "FREE_SHIPPING") {
                        discountTotalCart = totalCart - shippingFee;
                        alert(`Applied coupon successfully: ${code} (-${totalCart - discountTotalCart} $)`);

                    } else {
                        alert("Coupon is expired or not existed.");
                    }
                }
            })
            .catch(e => {
                console.log("Error: ", e);
            })
        console.log("Final discount total cart: ", discountTotalCart);
    }

    document.getElementById("subtotal").innerText = '$' + totalCart.toFixed(2);
    document.getElementById("discount").innerText = '$' + (totalCart - discountTotalCart).toFixed(2);
    document.getElementById("cart-total").innerText = '$' + discountTotalCart.toFixed(2);
});

async function updateCartTotal() {
    var totalCart = await fetch(`http://localhost:8080/cart`, {
            method: "GET",
        })
        .then(res => res.json())
        .then(data => {
            if (data.code == 1000) {
                return data.result.totalCartPrice;
            } else {
                return null;
            }
        })
        .catch(e => {
            console.log("Error: ", e);
        })

    document.getElementById("subtotal").innerText = '$' + totalCart.toFixed(2);
    // document.getElementById("discount").innerText = totalCart - discountTotalCart.toFixed(2);
    document.getElementById("cart-total").innerText = '$' + totalCart.toFixed(2);

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
document.querySelectorAll(".quantity-select").forEach((input) => {
    input.addEventListener("change", function () {
        const quantity = parseInt(this.value) || 1;
        const unitPrice = parseFloat(this.dataset.unitPrice);
        const itemId = this.dataset.itemId;

        const total = unitPrice * quantity;

        // Cập nhật UI
        const priceDisplay = this.closest(".row").querySelector(".products-price");
        priceDisplay.innerText = "$" + total.toFixed(2);

        // Gửi dữ liệu cập nhật lên server
        fetch(`/cart/update/${itemId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    quantity: quantity
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.code === 1000) {
                    updateCartTotal();
                    document.getElementById("discount").innerText = '$0.00';
                    document.getElementById("coupon-code").value = '';
                } else {
                    console.error("Lỗi cập nhật giỏ hàng:", data.message);
                }
            })
            .catch(error => {
                console.error("Lỗi fetch:", error);
            });
    });
});

// Gán sự kiện cho nút xoá sản phẩm
document.querySelectorAll(".btn-remove").forEach((btn) => {
    btn.addEventListener("click", function () {
        Swal.fire({
            title: "Delete this product from your cart ?",
            text: "You won't be able to revert this!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!"
        }).then((result) => {
            if (result.isConfirmed) {

                const productItem = this.closest(".product-item");
                const productId = this.dataset.itemId;

                fetch(`http://localhost:8080/cart/clear/${productId}`, {
                        method: "DELETE"
                    })
                    .then(res => res.json())
                    .then(data => {
                        if (data.code == 1000) {
                            console.log("Deleted cart item success");
                            Swal.fire({
                                title: "Deleted!",
                                text: "Your file has been deleted.",
                                icon: "success"
                            });
                            productItem.remove();
                            updateCartTotal();
                            checkCartEmpty();
                        }
                    })
            }
        });
    });
});

// Tính tổng và kiểm tra khi trang load
updateCartTotal();
checkCartEmpty();