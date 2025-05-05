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

            // Validate password match
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