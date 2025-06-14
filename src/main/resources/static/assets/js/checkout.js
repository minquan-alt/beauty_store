/* --- helpers & cart total --- */
let appliedDiscount = 0;
let paid = false;

function formatVND(num) {
    return num.toLocaleString("vi-VN", {
        style: "currency",
        currency: "VND"
    });
}

function formatUSD(num) {
    return '$' + num.toFixed(2);
}

function updateTotal() {
    let subtotal = 0;
    document.querySelectorAll(".product-item").forEach(item => {
        const qty = +item.dataset.quantity;
        // Sửa cách lấy giá - loại bỏ ký tự $ và chỉ lấy số
        const priceText = item.querySelector(".product-price").innerText;
        const price = parseFloat(priceText.replace(/[$,]/g, "")) || 0;
        subtotal += qty * price;
    });

    const total = Math.max(subtotal - appliedDiscount + 2, 0);

    // Sử dụng format USD thay vì VND để phù hợp với HTML
    document.getElementById("subtotal").innerText = formatUSD(subtotal);
    document.getElementById("discount").innerText = `-${formatUSD(appliedDiscount)}`;
    document.getElementById("cart-total").innerText = formatUSD(total);
}


/* toggle bank info */
const paymentSelect = document.getElementById("payment-method");
const bankInfo = document.getElementById("bank-info");

if (paymentSelect && bankInfo) {
    paymentSelect.addEventListener("change", () => {
        bankInfo.style.display = paymentSelect.value === "bank" ? "block" : "none";
    });
}

/* checkout validation */
const checkoutBtn = document.getElementById("checkout-btn");
if (checkoutBtn) {
    checkoutBtn.addEventListener("click", async () => {
        // Lấy payment method đã chọn
        const method = paymentSelect ? paymentSelect.value : "";
        let orderId;

        // Validate payment method
        if (!method || method === "Select method") {
            Swal.fire({
                icon: "warning",
                title: "Payment Method Required",
                text: "Please select a payment method before proceeding.",
                confirmButtonColor: "#000"
            });
            return;
        }

        let success = "Your order has been placed successfully!";

        switch (method) {
            case "cod":
                success = "Your order will be paid on delivery. Thank you!";
                break;
            case "bank":
                const transactionId = "BANK" + Date.now() + Math.floor(Math.random() * 1000);
                console.log("Generated Transaction ID:", transactionId);
                success = "Your order will be confirmed once the payment is successfully received. Please check the bank transfer details above.";
                var jsonData = {}
                jsonData["transactionId"] = transactionId;
                jsonData["paymentMethod"] = "VNPay";

                console.log(jsonData)
                Swal.fire({
                    title: "Have you completed the bank transfer?",
                    text: "Click 'Yes' only if you've already transferred the money.",
                    icon: "question",
                    showCancelButton: true,
                    confirmButtonText: "Yes, I’ve paid",
                    cancelButtonText: "Not yet"
                }).then((result) => {
                    if (result.isConfirmed) {
                        fetch("api/payment", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                body: JSON.stringify(jsonData)
                            })
                            .then(response => response.json())
                            .then(data => {
                                paid = true;
                                console.log(data)
                                if (data.code == 1000) {
                                    console.log("Successfully")
                                }
                            })
                            .then(() => {
                                Swal.fire({
                                    icon: "success",
                                    title: "Successfully",
                                    text: "Your order is being processed!",
                                    timer: 1500,
                                    showConfirmButton: false
                                })
                                .then(() => {
                                    window.location.href = "/product";
                                });
                            })
                    }
                })
                break;
            case "momo":
                success = "You will be redirected to Momo to complete payment.";
                fetch("/api/momo/create", {
                        method: "POST",
                    })
                    .then(response => response.json())
                    .then(data => {
                        console.log(data);
                        if (data.code == 1000) {
                            window.location.href = data.result.payUrl;
                        } else {
                            Swal.fire({
                                icon: "error",
                                title: "Payment Error",
                                text: "Failed to initiate Momo payment.",
                                confirmButtonColor: "#000"
                            });
                        }
                    })
                break;
        }


    });
}

/* Load provinces API - Kiểm tra element tồn tại */
const provinceSel = document.getElementById("province-select");
if (provinceSel) {
    fetch("https://provinces.open-api.vn/api/p/")
        .then(res => res.json())
        .then(data => {
            data.forEach(p => {
                const o = document.createElement("option");
                o.value = p.code;
                o.textContent = p.name;
                provinceSel.appendChild(o);
            });
        })
        .catch(error => {
            console.error("Error loading provinces:", error);
        });
}

/* Clipboard functions */
function copyToClipboard(elementId) {
    const element = document.getElementById(elementId);
    if (!element) return;

    const text = element.innerText.trim();
    navigator.clipboard.writeText(text)
        .then(() => {
            // Show success feedback
            Swal.fire({
                icon: "success",
                title: "Copied!",
                text: "Text copied to clipboard",
                timer: 1500,
                showConfirmButton: false
            });
        })
        .catch(error => {
            console.error("Copy failed:", error);
            // Fallback for older browsers
            const textArea = document.createElement("textarea");
            textArea.value = text;
            document.body.appendChild(textArea);
            textArea.select();
            document.execCommand('copy');
            document.body.removeChild(textArea);
        });
}

function copyNote() {
    const orderIdElement = document.getElementById("order-id-note");
    if (!orderIdElement) return;

    const orderId = orderIdElement.innerText.trim();
    const fullNote = `Payment for Order ${orderId}`;

    navigator.clipboard.writeText(fullNote)
        .then(() => {
            Swal.fire({
                icon: "success",
                title: "Note Copied!",
                text: "Payment note copied to clipboard",
                timer: 1500,
                showConfirmButton: false
            });
        })
        .catch(error => {
            console.error("Copy failed:", error);
        });
}

/* Initialize Order ID */
function initializeOrderId() {
    // Get order ID from server or generate one
    const orderId = "#" + Math.random().toString(36).substr(2, 8).toUpperCase();

    const orderIdElements = [
        document.getElementById("order-id"),
        document.getElementById("order-id-note")
    ];

    orderIdElements.forEach(element => {
        if (element) element.innerText = orderId;
    });
}

/* Initialize on page load */
document.addEventListener("DOMContentLoaded", function () {
    initializeOrderId();

    if (paymentSelect && paymentSelect.value === "Select method") {
        paymentSelect.selectedIndex = 0;
    }
});