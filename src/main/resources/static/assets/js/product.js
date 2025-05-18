 const minPrice = document.getElementById("minPrice");
    const maxPrice = document.getElementById("maxPrice");
    const rangeTrack = document.querySelector(".range-track");

    const minGap = 50; // Khoảng cách tối thiểu giữa hai nút
    const maxValue = parseInt(maxPrice.max);

function updateTrack() {
  let minValue = parseInt(minPrice.value);
  let maxValueCurrent = parseInt(maxPrice.value);

  if (maxValueCurrent - minValue < minGap) {
    minPrice.value = maxValueCurrent - minGap;
  }
  let minPercent = (minPrice.value / maxValue) * 100;
  let maxPercent = (maxPrice.value / maxValue) * 100;

  rangeTrack.style.left = minPercent + "%";
  rangeTrack.style.width = maxPercent - minPercent + "%";

  // Cập nhật giá trị hiển thị
  document.getElementById("minValue").innerText = minPrice.value;
  document.getElementById("maxValue").innerText = maxPrice.value;
}

function updateMax() {
  let minValueCurrent = parseInt(minPrice.value);
  let maxValueCurrent = parseInt(maxPrice.value);

  if (maxValueCurrent - minValueCurrent < minGap) {
    maxPrice.value = minValueCurrent + minGap;
  }

  updateTrack();
}

// Gán sự kiện
minPrice.addEventListener("input", updateTrack);
maxPrice.addEventListener("input", updateMax);

// Khởi động ban đầu
updateTrack();

document.querySelectorAll(".dropdown-toggle-custom").forEach((button) => {
  button.addEventListener("click", function () {
    let dropdown = this.nextElementSibling; // Lấy phần dropdown ngay sau button
    let icon = this.querySelector("i");

    if (dropdown.style.display === "block") {
      dropdown.style.display = "none";
      icon.style.transform = "rotate(0deg)";
    } else {
      dropdown.style.display = "block";
      icon.style.transform = "rotate(90deg)";
    }
  });
});

const searchForm = document.getElementById("searchForm");
const searchIcon = document.querySelector(".search-icon");

searchForm.addEventListener("submit", function (event) {
  event.preventDefault();

  const keywordInput = document.querySelector('input[name="keyword"]');
  const minPriceInput = document.querySelector('input[name="minPrice"]');
  const maxPriceInput = document.querySelector('input[name="maxPrice"]');

  let url = "/products/searchByQuery?";
  let params = [];

  if (keywordInput.value.trim()) {
    params.push(`keyword=${encodeURIComponent(keywordInput.value.trim())}`);
  }

  params.push(`minPrice=${minPriceInput.value}`);
  params.push(`maxPrice=${maxPriceInput.value}`);

  document
    .querySelectorAll('input[name="category"]:checked')
    .forEach((checkbox) => {
      params.push(`category=${encodeURIComponent(checkbox.value)}`);
    });

  url += params.join("&");
  window.location.href = url;
});

searchIcon.addEventListener("click", function () {
  searchForm.dispatchEvent(new Event("submit"));
});

window.addEventListener("load", function () {
  document.getElementById("loading").style.display = "none";
});
function increaseQty(btn) {
    const input = btn.parentElement.querySelector('input[type="number"]');
    let current = parseInt(input.value);
    if (current < 99) {
        input.value = current + 1;
    }
}

function decreaseQty(btn) {
    const input = btn.parentElement.querySelector('input[type="number"]');
    let current = parseInt(input.value);
    if (current > 1) {
        input.value = current - 1;
    }
}

function addToCart(btn) {
    const card = btn.closest('.card-body');
    const qtyContainer = card.querySelector('#quantityContainer');
    const qtyInput = qtyContainer.querySelector('input');

    if (qtyContainer.style.display === 'none') {
        qtyContainer.style.display = 'flex'; // Hiện bộ chọn số lượng
        btn.textContent = 'Confirm'; // Đổi tên nút
    } else {
        const quantity = parseInt(qtyInput.value);
        Swal.fire({
    title: 'Added to Cart!',
    text: `Quantity: ${quantity} item(s)`,
    icon: 'success',
    confirmButtonText: 'OK',
    timer: 2000,
    showConfirmButton: false
});
        // Thêm xử lý gửi dữ liệu về server nếu cần
        btn.textContent = 'Add to Cart'; // Reset nút
        qtyContainer.style.display = 'none'; // Ẩn lại
        qtyInput.value = 1; // Reset về 1
    }
    
}
 let quantityVisible = false;

    function toggleQuantity(button) {
        const qtyContainer = document.getElementById("quantityContainer");
        if (!quantityVisible) {
            qtyContainer.style.display = "flex";
            quantityVisible = true;
            button.innerText = "✔ Added";
        }
    }

    function changeQty(delta) {
        const input = document.getElementById("quantityInput");
        let current = parseInt(input.value);
        current = isNaN(current) ? 1 : current + delta;
        input.value = current < 1 ? 1 : current;
    }

    // Thêm sự kiện click cho nút "Buy Now"
    function buyNow() {
    Swal.fire({
        icon: 'success',
        title: 'Added to Cart',
        text: 'The product has been added to your cart.',
        timer: 2000,
        showConfirmButton: false
    });
}


function cancelQuantity() {
    const qtyContainer = document.getElementById("quantityContainer");
    const card = qtyContainer.closest('.card-body');
    const addToCartBtn = card.querySelector('.btn-add-to-cart');

    qtyContainer.style.display = 'none';     // Ẩn phần nhập số lượng
    addToCartBtn.textContent = 'Add to Cart'; // Đổi lại tên nút
}
