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
const searchInput = document.querySelector(".search-input");
const searchIcon = document.querySelector(".search-icon");

searchInput.addEventListener("keypress", function (e) {
  if (e.key === "Enter") {
    e.preventDefault();
    performSearch();
  }
});

searchForm.addEventListener("submit", function (event) {
  event.preventDefault();
  performSearch();
});

searchIcon.addEventListener("click", function () {
  performSearch();
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

function addToCartAPI(productId, quantity) {
  return fetch("http://localhost:8080/cart/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ productId, quantity }),
  })
    .then((res) => res.json())
    .then((data) => {
      console.log(data.result);
      if (data.code != 1000) {
        Swal.fire({
          title: "You need to login",
          text: data.message,
          icon: "error",
          timer: 2000,
          showConfirmButton: false,
        });
        return false;
      }
      return true;
    })
    .catch((err) => {
      console.error("Add to cart failed:", err);
      return false;
    });
}


async function addToCart(btn, selector, productId, type) {
  if (type == "card" && selector.style.display === "none") {
    selector.style.display = "flex"; // Hiện bộ chọn số lượng
    btn.textContent = "Confirm"; // Đổi tên nút
  } else {
    const qtyInput = selector.querySelector("input");
    const quantity = parseInt(qtyInput.value);

    const success = await addToCartAPI(productId, quantity);
    console.log("success: ", success)
    if(!success) {
      window.location.href = "/signin";
      return;
    }
    

    Swal.fire({
      title: "Added to Cart!",
      text: `Quantity: ${quantity} item(s)`,
      icon: "success",
      timer: 2000,
      showConfirmButton: false,
    });
    // Thêm xử lý gửi dữ liệu về server nếu cần
    if (type == "card") {
      btn.textContent = "Add to Cart"; // Reset nút
      selector.style.display = "none"; // Ẩn lại
    }
    qtyInput.value = 1; // Reset về 1
  }
}

var quantityVisible = false;

function toggleQuantity(button) {
  const qtyContainer = document.getElementById("quantityContainer");
  if (!quantityVisible) {
    qtyContainer.style.display = "flex";
    quantityVisible = true;
    button.innerText = "✔ Added";
  }
}

function changeQty(btn, delta) {
  const cardBody = btn.closest(".quantity-wrapped");
  const input = cardBody.querySelector('input[type="number"]');
  let current = parseInt(input.value);
  current = isNaN(current) ? 1 : current + delta;
  input.value = current < 1 ? 1 : current;
}


// Thêm sự kiện click cho nút "Buy Now"
function buyNow(productId, quantity = 1) {
  addToCartAPI(productId, quantity);
  Swal.fire({
    icon: "success",
    title: "Added to Cart",
    text: "The product has been added to your cart.",
    timer: 2000,
    showConfirmButton: false,
  });
}

function cancelQuantity(btn) {
  const qtyContainer = btn.closest("#quantityContainer");
  const cardBody = qtyContainer.closest(".card-body");
  const addToCartBtn = cardBody.querySelector(".btn-add-to-cart");

  qtyContainer.style.display = "none";
  addToCartBtn.textContent = "Add to Cart";
}

let currentPage = 1;
const pageSize = 12;

let currentSearchTerm = "";
let currentMinPrice = "";
let currentMaxPrice = "";
let currentCategories = [];
let isSearching = false;

function loadProducts(page = 1) {
  isSearching = false;
  fetch(`http://localhost:8080/products?page=${page}&size=${pageSize}`)
    .then((res) => res.json())
    .then((data) => {
      const products = data.result.data;
      const totalProducts = data.result.meta.total;

      const currentPage = data.result.meta.page;

      console.log(products);
      console.log(totalProducts);

      renderProducts(products);
      renderPagination(totalProducts, currentPage);
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
    card.className = "col card-wrapper";
    card.innerHTML = `
      <div class="card shadow-sm view-product" data-id="${product.id}">
        <img
          src="${
            product.imageUrls && product.imageUrls[0]
              ? product.imageUrls[0]
              : "http://localhost:8080/assets/images/product/loading.png"
          }"
          class="card-img-top"
          style="height: 250px; object-fit: scale-down"
          alt="Product Image"
        />
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <h5 class="card-title ellipsis  mb-0">${product.name}</h5>
            <span class="text-primary fw-bold">$${product.price}</span>
          </div>
          <p class="card-text text-muted mb-3 description-2-lines">
            ${product.description}
          </p>
          <div id="quantityContainer" class="quantity-wrapped align-items-center gap-2 mb-3" style="display: none">
            <button class="btn btn-outline-secondary btn-sm" onclick="changeQty(this,-1)">−</button>
            <input type="number" id="quantityInput" class="form-control form-control-sm text-center" style="width: 60px" value="1" min="1" />
            <button class="btn btn-outline-secondary btn-sm" onclick="changeQty(this,1)">+</button>
            <button class="btn btn-outline-danger btn-sm" onclick="cancelQuantity(this)">Cancel</button>
          </div>
          <div class="d-flex justify-content-between">
            <button class="btn btn-outline-secondary btn-sm btn-add-to-cart" 
                  onclick="addToCart(this, this.closest('.card-body').querySelector('#quantityContainer'),${
                    product.id
                  },'card')">Add to Cart</button>
            <button class="btn btn-secondary btn-buy-now" onclick="buyNow(${
              product.id
            })">Buy Now</button>
          </div>
        </div>
      </div>
    `;

    card.addEventListener("click", function (event) {
      if (
        event.target.closest("button") ||
        event.target.closest("input") ||
        event.target.closest(".btn-add-to-cart") ||
        event.target.closest(".btn-buy-now") ||
        event.target.closest("#quantityContainer") ||
        event.target.closest("a#moreBtn")
      ) {
        return;
      }
      const modalBody = document.querySelector(
        "#productDetailModalCenter .modal-body"
      );
      var imageHTML = product.imageUrls
        .map((image) => {
          return `
          <div class="swiper-slide">
            <img src="${image}" />
          </div>
        `;
        })
        .join("");

      modalBody.innerHTML = `
        <div class="container">
                <div class="product-image">
                  <div style="--swiper-navigation-color: #fff; --swiper-pagination-color: #fff" class="swiper mySwiper2">
                    <div class="swiper-wrapper">
                      ${imageHTML}
                    </div>
                    <div class="swiper-button-next"></div>
                    <div class="swiper-button-prev"></div>
                  </div>
                  <div thumbsSlider="" class="swiper mySwiper">
                    <div class="swiper-wrapper">
                      ${imageHTML}
                    </div>
                  </div>
                </div>
                <div class="product-info">
                  <h1 class="product-info-name">${product.name}</h1>
                  <div class="product-info-description">
                    ${product.description}
                  </div>
                  <div class="product-info-category_supplier">
                    ${product.categoryName} <span class="divider"></span> ${product.supplierName}
                  </div>

                  <div class="price">
                    $${product.price}
                  </div>

                  <div class="modal-quantity">
                    <span>
                      Quantity: 
                    </span>
                     <div class="modal-quantity-btn quantity-wrapped align-items-center gap-2">
                      <button class="btn btn-outline-secondary btn-sm" onclick="changeQty(this,-1)">−</button>
                      <input type="number" id="quantityInput" class="form-control form-control-sm text-center" style="width: 60px" value="1" min="1" />
                      <button class="btn btn-outline-secondary btn-sm" onclick="changeQty(this,1)">+</button>
                    </div>
                  </div>

                  <div class="modal-buy-cart">
                    <button class="btn modal-cart" onclick="addToCart(this, document.querySelector('.modal-quantity-btn.quantity-wrapped'),${product.id}, 'modal')"><i class="fa-solid fa-cart-shopping"></i> <span>Add To Cart</span></button>
                    <button class="btn modal-buy" onclick="buyNow(${product.id})">Buy Now</button>
                  </div>
                  
                </div>
              </div>
      `;

      var swiper = new Swiper(".mySwiper", {
        loop: false,
        spaceBetween: 10,
        slidesPerView: 4,
        freeMode: true,
        watchSlidesProgress: true,
      });
      var swiper2 = new Swiper(".mySwiper2", {
        loop: true,
        spaceBetween: 10,
        navigation: {
          nextEl: ".swiper-button-next",
          prevEl: ".swiper-button-prev",
        },
        thumbs: {
          swiper: swiper,
        },
      });

      const modal = new bootstrap.Modal(
        document.getElementById("productDetailModalCenter")
      );
      modal.show();
    });

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
        if (isSearching) {
          performSearch(page);
        } else {
          loadProducts(page);
        }
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

function performSearch(page = 1) {
  isSearching = true;

  let searchTermToUse = currentSearchTerm;
  let minPriceToUse = currentMinPrice;
  let maxPriceToUse = currentMaxPrice;
  let categoriesToUse = currentCategories;

  if (page === 1) {
    //Nếu search lần đầu
    const searchTermInput = document.querySelector(".search-input");
    const minPriceInput = document.getElementById("minPrice");
    const maxPriceInput = document.getElementById("maxPrice");

    searchTermToUse = searchTermInput.value.trim();
    minPriceToUse = minPriceInput.value;
    maxPriceToUse = maxPriceInput.value;
    categoriesToUse = [];

    document
      .querySelectorAll('input[name="category"]:checked')
      .forEach((checkbox) => {
        categoriesToUse.push(checkbox.value);
      });
    currentSearchTerm = searchTermToUse;
    currentMinPrice = minPriceToUse;
    currentMaxPrice = maxPriceToUse;
    currentCategories = categoriesToUse;
  }

  let params = new URLSearchParams();

  if (searchTermToUse) {
    params.append("keyword", searchTermToUse);
  }

  params.append("minPrice", minPriceToUse);
  params.append("maxPrice", maxPriceToUse);
  params.append("page", page);
  params.append("size", pageSize);

  categoriesToUse.forEach((category) => {
    params.append("category", category);
  });

  fetch(`http://localhost:8080/products/searchByQuery?${params.toString()}`)
    .then((res) => {
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }
      return res.json();
    })
    .then((data) => {
      const products = data.result.data;
      const totalProducts = data.result.meta.total;
      const currentPage = data.result.meta.page;

      if (products.length == 0) {
        Swal.fire({
          icon: "error",
          title: "Lỗi",
          text: "Không tìm thấy sản phẩm phù hợp",
          timer: 2000,
        });
      }

      console.log(products);
      console.log(totalProducts);

      renderProducts(products);
      renderPagination(totalProducts, currentPage);
    })
    .catch((error) => {
      console.error("Failed to search products:", error);
      Swal.fire({
        icon: "error",
        title: "Lỗi",
        text: "Không thể tìm kiếm sản phẩm. Vui lòng thử lại sau.",
        timer: 2000,
      });
      console.error(error);
    });
}

window.addEventListener("load", function () {
  document.getElementById("loading").style.display = "none";
  loadProducts(); // Initial load of products
});