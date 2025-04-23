$(document).ready(function() {
    // Check if user is logged in
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'USER') {
        window.location.href = 'login.html';
        return;
    }

    // Get trip ID from URL
    const urlParams = new URLSearchParams(window.location.search);
    const tripId = urlParams.get('id');

    if (!tripId) {
        showError('Trip ID is missing');
        setTimeout(() => {
            window.location.href = 'trip-list.html';
        }, 2000);
        return;
    }

    // Set up back and cancel links
    $('#backToTrip, #cancelBtn').attr('href', `trip-details.html?id=${tripId}`);

    // Get user ID (placeholder)
    let userId = localStorage.getItem('userId');
    if (!userId) {
        userId = "current-user";
    }

    // Load trip details
    loadTripDetails(tripId);

    // Handle form submission
    $('#editTripForm').on('submit', function(e) {
        e.preventDefault();
        updateTrip(tripId);
    });

    function loadTripDetails(tripId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/user/trips/${tripId}`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                // Hide loading indicator
                $('#loadingArea').hide();

                if (response.code === 200) {
                    // Show form content
                    $('#editFormContent').show();

                    const trip = response.data;

                    // Fill form with trip details
                    $('#destination').val(trip.destination);

                    // Format dates for input fields (YYYY-MM-DD)
                    const startDate = new Date(trip.startDate);
                    const endDate = new Date(trip.endDate);

                    const formattedStartDate = startDate.toISOString().split('T')[0];
                    const formattedEndDate = endDate.toISOString().split('T')[0];

                    $('#startDate').val(formattedStartDate);
                    $('#endDate').val(formattedEndDate);

                    // Set minimum date for end date based on start date
                    $('#startDate').on('change', function() {
                        $('#endDate').attr('min', $(this).val());

                        // If end date is before start date, reset it
                        if ($('#endDate').val() < $(this).val()) {
                            $('#endDate').val($(this).val());
                        }
                    });

                    // Update page title with destination
                    document.title = `Edit ${trip.destination} Trip - Journey Pro`;
                } else {
                    showError(response.message || 'Failed to load trip details');
                }
            },
            error: function() {
                $('#loadingArea').hide();
                showError('An error occurred while loading trip details');
            }
        });
    }

    function updateTrip(tripId) {
        // Show loading state on button
        const saveBtn = $('#saveBtn');
        const originalBtnText = saveBtn.html();
        saveBtn.html('<i class="fas fa-circle-notch fa-spin"></i> Saving...').prop('disabled', true);

        const tripData = {
            userId: userId,
            destination: $('#destination').val(),
            startDate: $('#startDate').val(),
            endDate: $('#endDate').val()
        };

        $.ajax({
            url: `http://localhost:8080/api/v1/user/trips/${tripId}`,
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            contentType: 'application/json',
            data: JSON.stringify(tripData),
            success: function(response) {
                if (response.code === 200) {
                    $('#successMessage').text('Trip updated successfully');
                    $('#successAlert').fadeIn();
                    $('#errorAlert').hide();

                    // Redirect to trip details after a delay
                    setTimeout(function() {
                        window.location.href = `trip-details.html?id=${tripId}`;
                    }, 2000);
                } else {
                    showError(response.message || 'Failed to update trip');
                    saveBtn.html(originalBtnText).prop('disabled', false);
                }
            },
            error: function(xhr) {
                let errorMessage = 'An error occurred while updating the trip';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMessage = xhr.responseJSON.message;
                }
                showError(errorMessage);
                saveBtn.html(originalBtnText).prop('disabled', false);
            }
        });
    }

    function showSuccess(message) {
        $('#successMessage').text(message);
        $('#successAlert').fadeIn();
        $('#errorAlert').hide();
    }

    function showError(message) {
        $('#errorMessage').text(message);
        $('#errorAlert').fadeIn();
        $('#successAlert').hide();
    }
});