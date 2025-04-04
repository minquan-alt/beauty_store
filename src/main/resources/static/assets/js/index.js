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