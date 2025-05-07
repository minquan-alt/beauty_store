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
