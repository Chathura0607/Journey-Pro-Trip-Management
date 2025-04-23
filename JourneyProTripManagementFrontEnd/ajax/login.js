$(document).ready(function() {
    // Form submission
    $('#loginForm').on('submit', function(e) {
        e.preventDefault();

        const email = $('#email').val();
        const password = $('#password').val();

        // Add loading state to button
        const submitBtn = $(this).find('button[type="submit"]');
        const originalText = submitBtn.html();
        submitBtn.html('<i class="fas fa-circle-notch fa-spin"></i> Signing in...').prop('disabled', true);

        $.ajax({
            url: 'http://localhost:8080/api/v1/auth/authenticate',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({email, password}),
            success: function(data) {
                if (data.code === 200) {
                    // Store auth data including email
                    localStorage.setItem('token', data.data.token);
                    localStorage.setItem('role', data.data.role);
                    localStorage.setItem('email', email);

                    // Add a small delay to show success before redirecting
                    setTimeout(function() {
                        if (data.data.role === 'ADMIN') {
                            window.location.href = 'admin-dashboard.html';
                        } else {
                            window.location.href = 'user-dashboard.html';
                        }
                    }, 300);
                } else {
                    $('#errorMessage').text(data.message || 'Login failed').fadeIn();
                    submitBtn.html(originalText).prop('disabled', false);
                }
            },
            error: function(xhr, status, error) {
                console.error('Login error:', error);
                $('#errorMessage').text('Invalid email or password. Please try again.').fadeIn();
                submitBtn.html(originalText).prop('disabled', false);
            }
        });
    });

    // Hide error message when inputs are changed
    $('#email, #password').on('input', function() {
        $('#errorMessage').fadeOut();
    });
});