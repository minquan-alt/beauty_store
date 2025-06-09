/* --- helpers & cart total --- */
let appliedDiscount = 0;

function formatVND(num) {
    return num.toLocaleString("vi-VN", {style: "currency", currency: "VND"});
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
    checkoutBtn.addEventListener("click", () => {
        // Lấy payment method đã chọn
        const method = paymentSelect ? paymentSelect.value : "";
        
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
        
        switch(method) {
            case "cod":
                success = "Your order will be paid on delivery. Thank you!";
                break;
            case "bank":
                success = "Your order will be confirmed once the payment is successfully received. Please check the bank transfer details above.";
                break;
            case "momo":
                success = "You will be redirected to Momo to complete payment.";
                fetch("/api/momo/create", {
                    method: "POST",
                })
                .then(response => response.json())
                .then(data => {
                    console.log(data);
                    if(data.code == 1000) {
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
document.addEventListener("DOMContentLoaded", function() {
    updateTotal();
    initializeOrderId();
    
    if (paymentSelect && paymentSelect.value === "Select method") {
        paymentSelect.selectedIndex = 0;
    }
});