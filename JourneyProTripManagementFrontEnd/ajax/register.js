$(document).ready(function() {
    // Profile picture preview
    $('#profilePicture').change(function(e) {
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $('#profilePreview').attr('src', e.target.result);
            }
            reader.readAsDataURL(this.files[0]);
        }
    });

    // Form submission
    $('#registerForm').submit(function(e){
        e.preventDefault();

        const formData = new FormData();
        formData.append('userData', JSON.stringify({
            firstName: $('#firstName').val(),
            lastName: $('#lastName').val(),
            email: $('#email').val(),
            password: $('#password').val(),
            phoneNumber: $('#phoneNumber').val(),
            address: $('#address').val()
        }));

        const profilePic = $('#profilePicture')[0].files[0];
        if (profilePic) {
            formData.append('profilePicture', profilePic);
        }

        $.ajax({
            url: 'http://localhost:8080/api/v1/user/register',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                showAlert('Registration successful! Redirecting to login...', 'success');
                setTimeout(() => window.location.href = 'login.html', 2000);
            },
            error: function(xhr) {
                const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : 'Registration failed';
                showAlert(errorMsg, 'danger');
            }
        });
    });

    function showAlert(message, type) {
        const alert = $('#alertMessage');
        alert.removeClass('d-none alert-success alert-danger')
            .addClass(`alert-${type}`)
            .html(`<i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i> ${message}`);
    }
});

function togglePassword() {
    const passwordField = $('#password');
    const icon = $('.password-toggle i');

    if (passwordField.attr('type') === 'password') {
        passwordField.attr('type', 'text');
        icon.removeClass('fa-eye-slash').addClass('fa-eye');
    } else {
        passwordField.attr('type', 'password');
        icon.removeClass('fa-eye').addClass('fa-eye-slash');
    }
}