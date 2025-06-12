window.addEventListener("load", function() {
    document.getElementById("loading").style.display = "none";
});

$(document).ready(function() {
    $("#logInForm").submit(async function(event) {
        event.preventDefault();

        var email = $("#email").val();
        var password = $("#password").val();
        console.log(email + " " + password);
        
        fetch("/auth/log-in", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.result.authenticated) {
                    Swal.fire({
                    title: "",
                    text: `You are authenticated!`,
                    icon: "success",
                    timer: 1000,
                    showConfirmButton: false,
                    });
                // alert("Đăng nhập thành công!");
                window.location.href = "/";
            } else {
                alert("Sai email hoặc mật khẩu!");
            }
        })
        .catch(error => {
            console.error("Lỗi:", error);
            alert("Đăng nhập thất bại! Vui lòng thử lại.");
        });
        
    })
})