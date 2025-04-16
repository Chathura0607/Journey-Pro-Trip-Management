$(document).ready(function () {
    $('#loginForm').on('submit', function (e) {
        e.preventDefault();

        const email = $('#email').val();
        const password = $('#password').val();

        $.ajax({
            url: 'http://localhost:8080/api/v1/auth/authenticate',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({email, password}),
            success: function (data) {
                if (data.code === 200) {
                    localStorage.setItem('token', data.data.token);
                    localStorage.setItem('role', data.data.role);
                    localStorage.setItem('email', email); // Ensure this is set

                    if (data.data.role === 'ADMIN') {
                        window.location.href = 'admin-dashboard.html';
                    } else {
                        window.location.href = 'user-dashboard.html';
                    }
                }
            },
            error: function (xhr, status, error) {
                console.error('Login error:', error);
                $('#errorMessage').text('An error occurred. Please try again.');
            }
        });
    });
});