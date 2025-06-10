let appliedDiscount = 0;

function formatVND(number) {
    return number.toLocaleString("vi-VN") + "VND";
}

async function goToPayment() {
    var addOrderRequest = {};
    var paymentInfo = {};

    const addressId = document.getElementById('delivery-address').value;
    const notes = document.getElementById('order-notes').value.trim();
    const couponCode = document.getElementById('coupon-code').value.trim();

    addOrderRequest["addressId"] = addressId;
    addOrderRequest["notes"] = notes;
    paymentInfo["couponCode"] = couponCode;
    addOrderRequest["paymentInfo"] = paymentInfo;

    await fetch(`/api/orders/add`, {
        method: "POST",
        headers: {
            'Content-Type' : 'application/json'
        },
        body: JSON.stringify(addOrderRequest)
    })
    .then(response => response.json())
    .then(data => {
        if(data.code == 1000) {
            console.log("Add order successfully")
            window.location.href = "/payment";
        } else {
            alert("Error: " + data.message);
        }
    })
    .catch()
    console.log(addOrderRequest);
}
document.getElementById("apply-coupon").addEventListener("click", async function () {
    const code = document.getElementById("coupon-code").value.trim().toUpperCase();
    
    // Lấy shipping fee từ DOM
    const shippingFeeText = document.getElementById("shippingFee").innerText;
    const shippingFee = parseFloat(shippingFeeText.replace('$', ''));
    
    var discountAmount = 0; // Số tiền được giảm
    var finalTotal = 0;     // Tổng cuối cùng sau giảm giá
    
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
            return null;
        })
    
    console.log("totalCart: ", totalCart);
    console.log("shippingFee: ", shippingFee);
    
    if (code != null && code != "" && totalCart != null) {
        await fetch(`http://localhost:8080/coupons/${code}`, {
                method: "GET",
            })
            .then(res => res.json())
            .then(data => {
                console.log("Coupon data:", data);
                const result = data.result;
                
                if (data.code == 1000) {
                    if (result.type == "PERCENTAGE") {
                        // Tính số tiền giảm
                        discountAmount = totalCart * (result.discountValue / 100);
                        finalTotal = totalCart - discountAmount + shippingFee;
                        
                        alert(`Applied coupon successfully: ${code} (-$${discountAmount.toFixed(2)})`);
                        
                    } else if (result.type == "FIXED_AMOUNT") {
                        // Giảm số tiền cố định
                        discountAmount = result.discountValue;
                        finalTotal = totalCart - discountAmount + shippingFee;
                        
                        alert(`Applied coupon successfully: ${code} (-$${discountAmount.toFixed(2)})`);
                        
                    } else if (result.type == "FREE_SHIPPING") {
                        // Miễn phí ship
                        discountAmount = shippingFee;
                        finalTotal = totalCart; // Không cộng shipping fee
                        
                        alert(`Applied coupon successfully: ${code} (Free Shipping -$${shippingFee.toFixed(2)})`);
                        
                    } else {
                        alert("Coupon is expired or not existed.");
                        return;
                    }
                    
                    // Cập nhật DOM
                    document.getElementById("subtotal").innerText = '$' + totalCart.toFixed(2);
                    document.getElementById("discount").innerText = '-$' + discountAmount.toFixed(2);
                    
                    // Cập nhật shipping fee nếu là free shipping
                    if (result.type == "FREE_SHIPPING") {
                        document.getElementById("shippingFee").innerText = '$0.00';
                    }
                    
                    document.getElementById("cart-total").innerText = '$' + finalTotal.toFixed(2);
                    
                } else {
                    alert("Coupon is expired or not existed.");
                }
            })
            .catch(e => {
                console.log("Error: ", e);
                alert("Error applying coupon. Please try again.");
            })
    } else {
        if (!code || code == "") {
            alert("Please enter a coupon code.");
        } else {
            alert("Unable to load cart information. Please refresh and try again.");
        }
    }
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
document.querySelectorAll("input[type='number']").forEach((input) => {
    input.addEventListener("change", function () {
        const quantity = parseInt(this.value) || 1;
        const unitPrice = parseFloat(this.getAttribute('data-unit-price'));
        const itemId = this.getAttribute('data-item-id'); 


        const total = unitPrice * quantity;

        // Cập nhật UI
        const priceDisplay = this.closest(".product-item").querySelector(".products-price");
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