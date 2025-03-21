$(document).ready(function() {
    $("#logInForm").submit(function(event) {
        event.preventDefault();

        var email = $("#email").val();
        var password = $("#password").val();
        console.log(email + " " + password);
        $.ajax({
            url: "/auth/log-in",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                email: email,
                password: password
            }),
            success: function(response) {
                if (response.result.authenticated) {
                    alert("Đăng nhập thành công!");
                    window.location.href = "/"; 
                } else {
                    alert("Sai email hoặc mật khẩu!");
                }
            },
            error: function(xhr, status, error) {
                console.error("Lỗi:", error);
                alert("Đăng nhập thất bại! Vui lòng thử lại.");
            }
        })
    })
})