$(document).ready(function () {
    // Check if user is logged in
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'USER') {
        window.location.href = 'login.html';
        return;
    }

    // Load user profile data
    loadUserProfile();

    // Handle profile form submission
    $('#profileForm').on('submit', function (e) {
        e.preventDefault();
        updateUserProfile();
    });

    function loadUserProfile() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/user/profile',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            success: function (response) {
                // Hide loading indicator
                $('#loadingArea').hide();

                if (response.code === 200) {
                    // Show profile content
                    $('#profileContent').show();

                    // Fill the form with user data
                    const userData = response.data;
                    $('#firstName').val(userData.firstName);
                    $('#lastName').val(userData.lastName);
                    $('#email').val(userData.email);
                    $('#phoneNumber').val(userData.phoneNumber);
                    $('#address').val(userData.address);

                    // Set user initials for avatar
                    if (userData.firstName && userData.lastName) {
                        const initials = userData.firstName.charAt(0) + userData.lastName.charAt(0);
                        $('#userInitials').text(initials.toUpperCase());
                    }

                    // Calculate profile completeness
                    updateProfileCompleteness(userData);
                } else {
                    $('#errorMessage').text(response.message || 'Failed to load profile');
                    $('#errorAlert').fadeIn();
                }
            },
            error: function () {
                $('#loadingArea').hide();
                $('#errorMessage').text('An error occurred. Please try again.');
                $('#errorAlert').fadeIn();
            }
        });
    }

    function updateProfileCompleteness(userData) {
        const fields = ['firstName', 'lastName', 'email', 'phoneNumber', 'address'];
        let completedFields = 0;

        fields.forEach(field => {
            if (userData[field] && userData[field].trim() !== '') {
                completedFields++;
            }
        });

        const percentage = Math.round((completedFields / fields.length) * 100);
        $('#completenessPercentage').text(`${percentage}%`);
        $('#completenessProgress').css('width', `${percentage}%`);
    }

    function updateUserProfile() {
        // Show loading state on button
        const saveBtn = $('#saveBtn');
        const originalBtnText = saveBtn.html();
        saveBtn.html('<i class="fas fa-circle-notch fa-spin"></i> Saving...').prop('disabled', true);

        const userData = {
            firstName: $('#firstName').val(),
            lastName: $('#lastName').val(),
            email: $('#email').val(),
            phoneNumber: $('#phoneNumber').val(),
            address: $('#address').val()
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/user/profile',
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            contentType: 'application/json',
            data: JSON.stringify(userData),
            success: function (response) {
                if (response.code === 200) {
                    $('#successMessage').text('Profile updated successfully');
                    $('#successAlert').fadeIn();
                    $('#errorAlert').hide();

                    // Update user initials
                    const initials = userData.firstName.charAt(0) + userData.lastName.charAt(0);
                    $('#userInitials').text(initials.toUpperCase());

                    // Update profile completeness
                    updateProfileCompleteness(userData);

                    // Auto-hide the success message after 3 seconds
                    setTimeout(function() {
                        $('#successAlert').fadeOut();
                    }, 3000);
                } else {
                    $('#errorMessage').text(response.message || 'Failed to update profile');
                    $('#errorAlert').fadeIn();
                    $('#successAlert').hide();
                }

                // Reset button state
                saveBtn.html(originalBtnText).prop('disabled', false);
            },
            error: function () {
                $('#errorMessage').text('An error occurred. Please try again.');
                $('#errorAlert').fadeIn();
                $('#successAlert').hide();

                // Reset button state
                saveBtn.html(originalBtnText).prop('disabled', false);
            }
        });
    }
});