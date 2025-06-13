function logoutUser() {
  fetch("/auth/log-out", {
    method: "GET",
    credentials: "same-origin",
  })
    .then((response) => response.json())
    .then((data) => {
      if (!data.result.authenticated) {
        window.location.href = "/"; 
      }
    })
    .catch((error) => console.error("Logout failed:", error));
}

function renderCollections(categories) {
  const collection = document.querySelector(".category .row");
  collection.innerHTML = "";
  console.log(categories);
  for (var i = 0; i < 3; i++) {
    const cate = document.createElement("div");
    cate.className = "col-md-4";
    cate.innerHTML = `
      <div class="category-item" style="background-image: url('https://fastly.picsum.photos/id/427/200/300.jpg?hmac=3a8xqsGEgfpWKYUhKjkcHRNP0NcEsi0Oyvw5gT6Kpc8');">
        <a th:href="@{#}"><span class="category-text">${categories[i].name}</span></a>
      </div>
    `;
    collection.appendChild(cate);
  }
}
function loadCollections() {
  fetch(`http://localhost:8080/categories`)
    .then((res) => res.json())
    .then((data) => {
      const categories = data.result;
      renderCollections(categories);
    })
    .catch((error) => {
      console.error("Failed to load products:", error);
    });
}

window.addEventListener("load", function () {
  document.getElementById("loading").style.display = "none";
});
window.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(window.location.search);
  const target = params.get("scroll");
  if (target) {
    const element = document.getElementById(target);
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
    }
  }
  loadCollections();
});
