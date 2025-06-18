window.addEventListener("load", function () {
    document.getElementById("loading").style.display = "none";
});

$(document).ready(function () {
    $("#signupForm").submit(async function (event) {
        event.preventDefault();
        const userData = {
            name: $('input[name="name"]').val(),
            email: $('input[name="email"]').val(),
            password: $('input[name="password"]').val(),
            confirmPassword: $('input[name="confirmPassword"]').val()
        };

        if (!userData.name || !userData.email || !userData.password || !userData.confirmPassword) {
            alert("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(userData.email)) {
            alert("Email không đúng định dạng!");
            return;
        }

        if (userData.password.length < 6) {
            alert("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }

        if (userData.password !== userData.confirmPassword) {
            alert("Mật khẩu không khớp!");
            return;
        }

        fetch("/users", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    name: userData.name,
                    email: userData.email,
                    password: userData.password
                })
            })
            .then(async response => {
                const data = await response.json();
                if (!response.ok) {
                    throw new Error(data.message || "Đăng ký thất bại");
                }
                return data;
            })
            .then(data => {
                alert("Đăng ký thành công!");
                window.location.href = "/signin";
            })
            .catch(error => {
                console.error("Error:", error);
                alert(error.message);
            });
    });
});