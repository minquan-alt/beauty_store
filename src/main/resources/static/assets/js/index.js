function logoutUser() {
    fetch('/auth/log-out', {
        method: 'GET',
        credentials: 'same-origin'
    })
    .then(response => response.json())
    .then(data => {
        if (!data.result.authenticated) {
            window.location.href = "/"; // Redirect về trang chủ
        }
    })
    .catch(error => console.error('Logout failed:', error));
}

window.addEventListener("load", function() {
    document.getElementById("loading").style.display = "none";
});
window.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const target = params.get("scroll");
    if (target) {
      const element = document.getElementById(target);
      if (element) {
        element.scrollIntoView({ behavior: "smooth" });
      }
    }
  });





