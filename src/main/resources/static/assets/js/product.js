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
  const card = btn.closest(".card");
  const qtyContainer = card.querySelector(".quantity-container");
  const qtyInput = qtyContainer.querySelector(".quantity-input");

  if (qtyContainer.style.display === "none") {
    qtyContainer.style.display = "flex";
    btn.textContent = "Confirm";
  } else {
    const quantity = parseInt(qtyInput.value);
    const productId = card.dataset.id;

    fetch("http://localhost:8080/cart/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ productId, quantity })
    })
      .then(res => res.json())
      .then(data => console.log(data.result));

    Swal.fire({
      title: "Added to Cart!",
      text: `Quantity: ${quantity} item(s)`,
      icon: "success",
      timer: 2000,
      showConfirmButton: false,
    });

    btn.textContent = "Add to Cart";
    qtyContainer.style.display = "none";
    qtyInput.value = 1;
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

function changeQty(btn, delta) {
  const input = btn.closest(".quantity-container").querySelector(".quantity-input");
  let current = parseInt(input.value);
  current = isNaN(current) ? 1 : current + delta;
  input.value = current < 1 ? 1 : current;
}


// Thêm sự kiện click cho nút "Buy Now"
function buyNow() {
  Swal.fire({
    icon: "success",
    title: "Added to Cart",
    text: "The product has been added to your cart.",
    timer: 2000,
    showConfirmButton: false,
  });
}

function cancelQuantity(btn) {
  const qtyContainer = btn.closest(".quantity-container");
  const card = btn.closest(".card-body");
  const addToCartBtn = card.querySelector(".btn-add-to-cart");

  qtyContainer.style.display = "none";
  addToCartBtn.textContent = "Add to Cart";
}


// document.addEventListener("DOMContentLoaded", function () {
//   fetch("http://localhost:8080/products")
//     .then((res) => res.json())
//     .then((data) => {
//       const products = data.result.data;
//       const list = document.querySelector(".product-list");
//       products.forEach((product) => {
//         const card = document.createElement("div");
//         card.className = "col";
//         card.innerHTML = `
//           <div class="card shadow-sm">
//             <img
//               src="${
//                 product.imageUrls && product.imageUrls[0]
//                   ? product.imageUrls[0]
//                   : "http://localhost:8080/assets/images/product/loading.png"
//               }"
//               class="card-img-top"
//               style="height: 250px; object-fit: scale-down"
//               alt="Product Image"
//             />
//             <div class="card-body">
//               <div class="d-flex justify-content-between align-items-center mb-2">
//                 <h5 class="card-title mb-0">${product.name}</h5>
//                 <span class="text-primary fw-bold">$${product.price}</span>
//               </div>
//               <p class="card-text text-muted mb-3">
//                 ${
//                   product.description
//                 }<a href="#" class="text-primary" id="moreBtn">More</a>
//               </p>
//               <div id="quantityContainer" class="align-items-center gap-2 mb-3" style="display: none">
//                 <button class="btn btn-outline-secondary btn-sm" onclick="changeQty(-1)">−</button>
//                 <input type="number" id="quantityInput" class="form-control form-control-sm text-center" style="width: 60px" value="1" min="1" />
//                 <button class="btn btn-outline-secondary btn-sm" onclick="changeQty(1)">+</button>
//                 <button class="btn btn-outline-danger btn-sm" onclick="cancelQuantity(this)">Cancel</button>
//               </div>
//               <div class="d-flex justify-content-between">
//                 <button class="btn btn-outline-secondary btn-sm btn-add-to-cart" onclick="addToCart(this)">Add to Cart</button>
//                 <button class="btn btn-secondary btn-buy-now" onclick="buyNow()">Buy Now</button>
//               </div>
//             </div>
//           </div>
//         `;

//         list.appendChild(card);
//       });
//     })
//     .catch((error) => {
//       console.error("Failed to load products:", error);
//     });
// });

let currentPage = 1;
const pageSize = 12;

function loadProducts(page = 1) {
  fetch(`http://localhost:8080/products?page=${page}&size=${pageSize}`)
    .then((res) => res.json())
    .then((data) => {
      const products = data.result.data;
      const totalProducts = data.result.meta.total;

      renderProducts(products);
      renderPagination(totalProducts, page);
    })
    .catch((error) => {
      console.error("Failed to load products:", error);
    });
}

function renderProducts(products) {
  const list = document.querySelector(".product-list");
  list.innerHTML = "";

  products.forEach((product) => {
    const card = document.createElement("div");
    card.className = "col";
    card.innerHTML = `
      <div class="card shadow-sm" data-id="${product.id}">
        <div class="image-wrapper">  <!-- Thêm div này -->
    <img
      src="${
        product.imageUrls && product.imageUrls[0]
          ? product.imageUrls[0]
          : 'http://localhost:8080/assets/images/product/loading.png'
      }"
      class="card-img-top"
      style="height: 250px; object-fit: scale-down"
      alt="Product Image"
    />
    <span class="price-badge">$${product.price}</span>  
  </div>
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <h5 class="card-title mb-0">${product.name}</h5>
          </div>
          <p class="card-text text-muted mb-3" id="descriptionText">
            ${product.description}..
            
            <a href="#" class="text-primary" id="moreBtn">More</a>
          </p>

          <div class="quantity-container align-items-center gap-2 mb-3" style="display: none;">
  <button class="btn btn-outline-secondary btn-sm" onclick="changeQty(this, -1)">−</button>
  <input type="number" class="form-control form-control-sm text-center quantity-input" style="width: 60px" value="1" min="1" />
  <button class="btn btn-outline-secondary btn-sm" onclick="changeQty(this, 1)">+</button>
  <button class="btn btn-outline-danger btn-sm" onclick="cancelQuantity(this)">Cancel</button>
</div>

          <div class="d-flex justify-content-between">
            <button class="btn btn-outline-secondary btn-sm btn-add-to-cart" onclick="addToCart(this)">Add to Cart</button>
            <button class="btn btn-secondary btn-buy-now" onclick="buyNow()">Buy Now</button>
          </div>
        </div>
      </div>
    `;
    list.appendChild(card);
  });
}

function renderPagination(totalItems, currentPage) {
  const totalPages = Math.ceil(totalItems / pageSize);
  const paginationWrapper = document.querySelector(".pagination");
  paginationWrapper.innerHTML = "";

  const createPageLink = (label, page, isActive = false, disabled = false) => {
    const a = document.createElement("a");
    a.href = "#";
    a.textContent = label;
    if (isActive) a.classList.add("active");
    if (disabled) a.classList.add("disabled");

    a.addEventListener("click", function (e) {
      e.preventDefault();
      if (!disabled && page !== currentPage) {
        loadProducts(page);
      }
    });
    return a;
  };

  // Previous button
  paginationWrapper.appendChild(
    createPageLink("«", currentPage - 1, false, currentPage === 1)
  );

  // Page numbers
  for (let i = 1; i <= totalPages; i++) {
    paginationWrapper.appendChild(createPageLink(i, i, i === currentPage));
  }

  // Next button
  paginationWrapper.appendChild(
    createPageLink("»", currentPage + 1, false, currentPage === totalPages)
  );
}

document.addEventListener("DOMContentLoaded", function () {
  loadProducts();
});


