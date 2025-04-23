$(document).ready(function () {
    // Check if admin is logged in
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'ADMIN') {
        window.location.href = 'login.html';
        return;
    }

    // Load admin name and initial data
    loadAdminProfile();
    loadDashboardData();

    // Handle navigation
    $('.nav-menu a').on('click', function (e) {
        if ($(this).attr('id') === 'logoutBtn') {
            logout();
            return;
        }

        e.preventDefault();

        // Set active menu item
        $('.nav-menu a').removeClass('active');
        $(this).addClass('active');

        // Show selected section
        const section = $(this).data('section');
        $('.admin-section').hide();
        $(`#${section}-section`).show();

        // Load section data
        switch (section) {
            case 'users':
                loadUsers();
                break;
            case 'trips':
                loadTrips();
                break;
            case 'bookings':
                loadBookings();
                break;
            case 'hotels':
                loadHotels();
                break;
            case 'vehicles':
                loadVehicles();
                break;
            case 'buses':
                loadBuses();
                break;
            case 'reports':
                loadReports('user-activity');
                break;
            case 'admins':
                loadAdmins();
                break;
            default:
                loadDashboardData();
        }
    });

    // Inside the $(document).ready(function () {
// Add this after other tab navigation handlers

// Admin section tab navigation
    $('.tab-navigation .tab-button').on('click', function() {
        const tabId = $(this).data('tab');

        // Remove active class from all buttons and content
        $('.tab-button').removeClass('active');
        $('.tab-content').removeClass('active').hide();

        // Add active class to clicked button and show corresponding content
        $(this).addClass('active');
        $(`#${tabId}`).addClass('active').show();
    });

    // ==================== USER MANAGEMENT ====================
    $('#refreshUsersBtn').on('click', function() {
        loadUsers();
        showSuccess('Users list refreshed');
    });

    $('#exportUsersBtn').on('click', function() {
        exportUsersToCSV();
    });

    function loadUsers() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/users?role=USER', // Only get regular users
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const users = response.data || [];
                    displayUsersTable(users);

                    // Initialize search functionality
                    initializeUserSearch(users);
                } else {
                    $('#usersTable').html('<div class="no-items">Failed to load users.</div>');
                }
            },
            error: function() {
                $('#usersTable').html('<div class="no-items">An error occurred while loading users.</div>');
            }
        });
    }

    function displayUsersTable(users) {
        if (!users || users.length === 0) {
            $('#usersTable').html('<div class="no-items">No regular users found.</div>');
            return;
        }

        let tableHTML = `
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Joined Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
        `;

        users.forEach(user => {
            const joinedDate = new Date(user.createdAt).toLocaleDateString();
            tableHTML += `
                <tr>
                    <td>${user.firstName} ${user.lastName}</td>
                    <td>${user.email}</td>
                    <td>${user.phoneNumber || 'N/A'}</td>
                    <td>${joinedDate}</td>
                    <td>Active</td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-outline btn-sm view-user-btn" data-user-id="${user.id}">
                                View
                            </button>
                            <button class="btn btn-danger btn-sm delete-user-btn"
                                    data-user-id="${user.id}"
                                    data-user-email="${user.email}"
                                    data-user-name="${user.firstName} ${user.lastName}">
                                Delete
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        });

        tableHTML += `
                </tbody>
            </table>
        `;

        $('#usersTable').html(tableHTML);

        // Add click handlers for delete buttons
        $('.delete-user-btn').on('click', function() {
            const userId = $(this).data('user-id');
            const userEmail = $(this).data('user-email');
            const userName = $(this).data('user-name');

            $('#deleteUserMessage').html(`Are you sure you want to delete the account of <strong>${userName}</strong> (${userEmail})?`);
            $('#confirmDeleteUserBtn').data('user-id', userId);
            $('#confirmDeleteUserBtn').data('user-email', userEmail);
            $('#deleteUserModal').show();
        });

        // Add click handlers for view buttons
        $('.view-user-btn').on('click', function() {
            const userId = $(this).data('user-id');
            viewUserDetails(userId);
        });
    }

    function initializeUserSearch(users) {
        $('#userSearch').on('keyup', function() {
            const searchText = $(this).val().toLowerCase();

            $('.admin-table tbody tr').each(function() {
                const name = $(this).find('td:eq(0)').text().toLowerCase();
                const email = $(this).find('td:eq(1)').text().toLowerCase();

                $(this).toggle(name.includes(searchText) || email.includes(searchText));
            });
        });
    }

    function viewUserDetails(userId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/admin/users/${userId}`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const user = response.data;
                    const userDetailsHTML = `
                        <div class="user-details">
                            <h3>User Details</h3>
                            <div class="detail-row">
                                <strong>Name:</strong> ${user.firstName} ${user.lastName}
                            </div>
                            <div class="detail-row">
                                <strong>Email:</strong> ${user.email}
                            </div>
                            <div class="detail-row">
                                <strong>Phone:</strong> ${user.phoneNumber || 'N/A'}
                            </div>
                            <div class="detail-row">
                                <strong>Address:</strong> ${user.address || 'N/A'}
                            </div>
                            <div class="detail-row">
                                <strong>Joined:</strong> ${new Date(user.createdAt).toLocaleString()}
                            </div>
                            <div class="detail-row">
                                <strong>Profile Picture:</strong>
                                ${user.profilePicture ?
                        `<img src="/uploads/profile-pictures/${user.profilePicture}" style="max-width: 100px; max-height: 100px;">` :
                        'None'}
                            </div>
                        </div>
                    `;

                    // Show in a modal
                    $('#modalContent').html(userDetailsHTML);
                    $('#userDetailsModal').show();
                } else {
                    showError('Failed to load user details');
                }
            },
            error: function() {
                showError('An error occurred while loading user details');
            }
        });
    }

    $('#confirmDeleteUserBtn').on('click', function() {
        const userId = $(this).data('user-id');
        const userEmail = $(this).data('user-email');
        const reason = $('#deleteReason').val();
        const sendNotification = $('#sendNotification').is(':checked');

        const deleteData = {
            reason: reason,
            notifyUser: sendNotification
        };

        $.ajax({
            url: `http://localhost:8080/api/v1/admin/users/${userId}`,
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(deleteData),
            success: function(response) {
                if (response.code === 200) {
                    showSuccess('User account deleted successfully');
                    loadUsers(); // Refresh the list
                    $('#deleteUserModal').hide();
                    $('#deleteReason').val('');
                    $('#sendNotification').prop('checked', false);
                } else {
                    showError(response.message || 'Failed to delete user account');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while deleting the user account');
            }
        });
    });

    $('#cancelDeleteUserBtn').on('click', function() {
        $('#deleteUserModal').hide();
        $('#deleteReason').val('');
        $('#sendNotification').prop('checked', false);
    });

    function exportUsersToCSV() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/users/export?role=USER',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            xhrFields: {
                responseType: 'blob'
            },
            success: function(data) {
                const blob = new Blob([data], { type: 'text/csv' });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'users_export_' + new Date().toISOString().slice(0, 10) + '.csv';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);

                showSuccess('Users exported to CSV successfully');
            },
            error: function() {
                showError('Failed to export users to CSV');
            }
        });
    }

    // ==================== OTHER DASHBOARD FUNCTIONS ====================
    function loadAdminProfile() {
        const adminEmail = localStorage.getItem('email');
        if (!adminEmail) {
            console.error('No admin email found in local storage');
            $('#adminName').text('Admin');
            return;
        }

        $.ajax({
            url: `http://localhost:8080/api/v1/admin/search?email=${encodeURIComponent(adminEmail)}`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.code === 200) {
                    const admin = response.data;
                    $('#adminName').text(admin.firstName || 'Admin');
                } else {
                    console.error('Failed to load admin profile:', response.message);
                    $('#adminName').text('Admin');
                }
            },
            error: function (xhr, status, error) {
                console.error('Error loading admin profile:', error);
                $('#adminName').text('Admin');
            }
        });
    }

    function loadDashboardData() {
        // Load stats and dashboard data
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/reports/user-activity?role=USER',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.code === 200) {
                    const report = response.data;
                    $('#totalUsers').text(report.totalUsers || 0);
                    displayRecentUsers(report.users);
                }
            }
        });

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/reports/bookings',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.code === 200) {
                    const report = response.data;
                    $('#totalBookings').text(report.totalBookings || 0);
                    displayRecentBookings(report.bookings);
                }
            }
        });

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/reports/payments',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.code === 200) {
                    const report = response.data;
                    const totalRevenue = report.payments ? report.payments.reduce((sum, payment) => sum + (payment.amount || 0), 0) : 0;
                    $('#totalRevenue').text(`$${totalRevenue.toFixed(2)}`);
                }
            }
        });

        // Load popular destinations
        const destinations = [
            {destination: 'Bali, Indonesia', count: 256},
            {destination: 'Paris, France', count: 198},
            {destination: 'Tokyo, Japan', count: 175},
            {destination: 'New York, USA', count: 147},
            {destination: 'Rome, Italy', count: 132}
        ];
        displayPopularDestinations(destinations);
    }

    function displayRecentUsers(users) {
        let usersHTML = '';

        if (!users || users.length === 0) {
            usersHTML = '<div class="no-items">No users found.</div>';
        } else {
            const sortedUsers = [...users].sort((a, b) =>
                new Date(b.createdAt) - new Date(a.createdAt)
            );

            const usersToShow = sortedUsers.slice(0, 5);

            usersToShow.forEach(user => {
                const createdDate = new Date(user.createdAt).toLocaleDateString();
                usersHTML += `
                <div class="list-item">
                    <div>
                        <div class="title">${user.firstName} ${user.lastName}</div>
                        <div class="details">${user.email}</div>
                    </div>
                    <div class="date">${createdDate}</div>
                </div>
            `;
            });
        }

        $('#recentUsers').html(usersHTML);
    }

    function displayRecentBookings(bookings) {
        let bookingsHTML = '';

        if (!bookings || bookings.length === 0) {
            bookingsHTML = '<div class="no-items">No bookings found.</div>';
        } else {
            const sortedBookings = [...bookings].sort((a, b) =>
                new Date(b.createdAt) - new Date(a.createdAt)
            );

            const bookingsToShow = sortedBookings.slice(0, 5);

            bookingsToShow.forEach(booking => {
                const createdDate = new Date(booking.createdAt).toLocaleDateString();
                bookingsHTML += `
                <div class="list-item">
                    <div>
                        <div class="title">${booking.bookingType} Booking</div>
                        <div class="details">Amount: $${booking.amount?.toFixed(2) || '0.00'}</div>
                    </div>
                    <div class="date">${createdDate}</div>
                </div>
            `;
            });
        }

        $('#recentBookings').html(bookingsHTML);
    }

    function displayPopularDestinations(destinations) {
        let destinationsHTML = '';

        if (!destinations || destinations.length === 0) {
            destinationsHTML = '<div class="no-items">No destinations found.</div>';
        } else {
            destinations.forEach(destination => {
                destinationsHTML += `
                <div class="list-item">
                    <div class="title">${destination.destination}</div>
                    <div>${destination.count} bookings</div>
                </div>
            `;
            });
        }

        $('#popularDestinations').html(destinationsHTML);
    }

    function loadTrips() {
        $('#tripsTable').html('<div class="no-items">Trip management feature coming soon.</div>');
    }

    function loadBookings() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/reports/bookings',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.code === 200) {
                    const bookings = response.data.bookings || [];
                    displayBookingsTable(bookings);
                } else {
                    $('#bookingsTable').html('<div class="no-items">Failed to load bookings.</div>');
                }
            },
            error: function () {
                $('#bookingsTable').html('<div class="no-items">An error occurred while loading bookings.</div>');
            }
        });
    }

    function displayBookingsTable(bookings) {
        if (!bookings || bookings.length === 0) {
            $('#bookingsTable').html('<div class="no-items">No bookings found.</div>');
            return;
        }

        let tableHTML = `
        <table class="admin-table">
            <thead>
                <tr>
                    <th>Booking ID</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Amount</th>
                    <th>Booking Date</th>
                </tr>
            </thead>
            <tbody>
        `;

        bookings.forEach(booking => {
            const bookingDate = new Date(booking.createdAt).toLocaleDateString();
            tableHTML += `
                <tr>
                    <td>${booking.id}</td>
                    <td>${booking.bookingType}</td>
                    <td>${booking.status}</td>
                    <td>$${booking.amount?.toFixed(2) || '0.00'}</td>
                    <td>${bookingDate}</td>
                </tr>
            `;
        });

        tableHTML += `
            </tbody>
        </table>
        `;

        $('#bookingsTable').html(tableHTML);
    }

    // ==================== HOTEL MANAGEMENT ====================
    $('#refreshHotelsBtn').on('click', function() {
        loadHotels();
        showSuccess('Hotels list refreshed');
    });

    $('#addHotelForm').on('submit', function(e) {
        e.preventDefault();
        addHotel();
    });

    $('#editHotelForm').on('submit', function(e) {
        e.preventDefault();
        updateHotel();
    });

    function loadHotels() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/hotels',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const hotels = response.data || [];
                    displayHotelsTable(hotels);
                } else {
                    $('#hotelsTable').html('<div class="no-items">Failed to load hotels.</div>');
                }
            },
            error: function() {
                $('#hotelsTable').html('<div class="no-items">An error occurred while loading hotels.</div>');
            }
        });
    }

    function displayHotelsTable(hotels) {
        if (!hotels || hotels.length === 0) {
            $('#hotelsTable').html('<div class="no-items">No hotels found. Add your first hotel!</div>');
            return;
        }

        let tableHTML = `
        <table class="admin-table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Location</th>
                    <th>Rating</th>
                    <th>Contact</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
        `;

        hotels.forEach(hotel => {
            tableHTML += `
            <tr>
                <td>${hotel.name}</td>
                <td>${hotel.location}</td>
                <td>${hotel.rating} ★</td>
                <td>${hotel.contactInfo}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn btn-outline btn-sm edit-hotel-btn"
                                data-hotel-id="${hotel.id}"
                                data-hotel-name="${hotel.name}"
                                data-hotel-location="${hotel.location}"
                                data-hotel-rating="${hotel.rating}"
                                data-hotel-contact="${hotel.contactInfo}">
                            Edit
                        </button>
                        <button class="btn btn-danger btn-sm delete-hotel-btn"
                                data-hotel-id="${hotel.id}">
                            Delete
                        </button>
                    </div>
                </td>
            </tr>
            `;
        });

        tableHTML += `
            </tbody>
        </table>
        `;

        $('#hotelsTable').html(tableHTML);

        // Add click handlers for edit buttons
        $('.edit-hotel-btn').on('click', function() {
            const hotelId = $(this).data('hotel-id');
            const hotelName = $(this).data('hotel-name');
            const hotelLocation = $(this).data('hotel-location');
            const hotelRating = $(this).data('hotel-rating');
            const hotelContact = $(this).data('hotel-contact');

            // Fill the edit form
            $('#editHotelId').val(hotelId);
            $('#editHotelName').val(hotelName);
            $('#editHotelLocation').val(hotelLocation);
            $('#editHotelRating').val(hotelRating);
            $('#editHotelContact').val(hotelContact);

            // Switch to edit tab
            $('.tab-button[data-tab="edit-hotel"]').click();
        });

        // Add click handlers for delete buttons
        $('.delete-hotel-btn').on('click', function() {
            const hotelId = $(this).data('hotel-id');
            if (confirm('Are you sure you want to delete this hotel?')) {
                deleteHotelById(hotelId);
            }
        });
    }

    function addHotel() {
        const hotelData = {
            name: $('#hotelName').val(),
            location: $('#hotelLocation').val(),
            rating: parseFloat($('#hotelRating').val()),
            contactInfo: $('#hotelContact').val()
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/hotels',
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(hotelData),
            success: function(response) {
                if (response.code === 201) {
                    showSuccess('Hotel added successfully');
                    $('#addHotelForm')[0].reset();
                    loadHotels();
                    $('.tab-button[data-tab="hotels-list"]').click();
                } else if (response.code === 409) {
                    showError('Hotel with the same name and location already exists');
                } else {
                    showError(response.message || 'Failed to add hotel');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while adding the hotel');
            }
        });
    }

    function updateHotel() {
        const hotelData = {
            id: $('#editHotelId').val(),
            name: $('#editHotelName').val(),
            location: $('#editHotelLocation').val(),
            rating: parseFloat($('#editHotelRating').val()),
            contactInfo: $('#editHotelContact').val()
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/hotels',
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(hotelData),
            success: function(response) {
                if (response.code === 200) {
                    showSuccess('Hotel updated successfully');
                    loadHotels();
                    $('.tab-button[data-tab="hotels-list"]').click();
                } else {
                    showError(response.message || 'Failed to update hotel');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while updating the hotel');
            }
        });
    }

    function deleteHotelById(hotelId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/admin/hotels/${hotelId}`,
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    showSuccess('Hotel deleted successfully');
                    loadHotels();
                } else {
                    showError(response.message || 'Failed to delete hotel');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while deleting the hotel');
            }
        });
    }

    // ==================== VEHICLE MANAGEMENT ====================
    $('#refreshVehiclesBtn').on('click', function() {
        loadVehicles();
        showSuccess('Vehicles list refreshed');
    });

    $('#addVehicleForm').on('submit', function(e) {
        e.preventDefault();
        addVehicle();
    });

    $('#editVehicleForm').on('submit', function(e) {
        e.preventDefault();
        updateVehicle();
    });

    function loadVehicles() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/vehicles',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.code === 200) {
                    const vehicles = response.data || [];
                    displayVehiclesTable(vehicles);
                } else {
                    $('#vehiclesTable').html('<div class="no-items">Failed to load vehicles.</div>');
                }
            },
            error: function () {
                $('#vehiclesTable').html('<div class="no-items">An error occurred while loading vehicles.</div>');
            }
        });
    }

    function displayVehiclesTable(vehicles) {
        if (!vehicles || vehicles.length === 0) {
            $('#vehiclesTable').html('<div class="no-items">No vehicles found. Add your first vehicle!</div>');
            return;
        }

        let tableHTML = `
        <table class="admin-table">
            <thead>
                <tr>
                    <th>Type</th>
                    <th>Model</th>
                    <th>Registration</th>
                    <th>Seats</th>
                    <th>Available</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
        `;

        vehicles.forEach(vehicle => {
            tableHTML += `
            <tr>
                <td>${vehicle.vehicleType}</td>
                <td>${vehicle.model}</td>
                <td>${vehicle.registrationNumber}</td>
                <td>${vehicle.seatCapacity}</td>
                <td>${vehicle.isAvailable ? 'Yes' : 'No'}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn btn-outline btn-sm edit-vehicle-btn"
                                data-vehicle-id="${vehicle.id}"
                                data-vehicle-type="${vehicle.vehicleType}"
                                data-vehicle-model="${vehicle.model}"
                                data-vehicle-registration="${vehicle.registrationNumber}"
                                data-vehicle-capacity="${vehicle.seatCapacity}"
                                data-vehicle-available="${vehicle.isAvailable}">
                            Edit
                        </button>
                        <button class="btn btn-danger btn-sm delete-vehicle-btn"
                                data-vehicle-id="${vehicle.id}">
                            Delete
                        </button>
                    </div>
                </td>
            </tr>
            `;
        });

        tableHTML += `
            </tbody>
        </table>
        `;

        $('#vehiclesTable').html(tableHTML);

        // Add click handlers for edit buttons
        $('.edit-vehicle-btn').on('click', function() {
            const vehicleId = $(this).data('vehicle-id');
            const vehicleType = $(this).data('vehicle-type');
            const vehicleModel = $(this).data('vehicle-model');
            const vehicleRegistration = $(this).data('vehicle-registration');
            const vehicleCapacity = $(this).data('vehicle-capacity');
            const vehicleAvailable = $(this).data('vehicle-available');

            // Fill the edit form
            $('#editVehicleId').val(vehicleId);
            $('#editVehicleType').val(vehicleType);
            $('#editVehicleModel').val(vehicleModel);
            $('#editVehicleRegistration').val(vehicleRegistration);
            $('#editVehicleCapacity').val(vehicleCapacity);
            $('#editVehicleAvailable').val(vehicleAvailable.toString());

            // Switch to edit tab
            $('.tab-button[data-tab="edit-vehicle"]').click();
        });

        // Add click handlers for delete buttons
        $('.delete-vehicle-btn').on('click', function() {
            const vehicleId = $(this).data('vehicle-id');
            if (confirm('Are you sure you want to delete this vehicle?')) {
                deleteVehicleById(vehicleId);
            }
        });
    }

    function addVehicle() {
        const vehicleData = {
            vehicleType: $('#vehicleType').val(),
            model: $('#vehicleModel').val(),
            registrationNumber: $('#vehicleRegistration').val(),
            seatCapacity: parseInt($('#vehicleCapacity').val()),
            isAvailable: $('#vehicleAvailable').val() === 'true'
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/vehicles',
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(vehicleData),
            success: function(response) {
                if (response.code === 201) {
                    showSuccess('Vehicle added successfully');
                    $('#addVehicleForm')[0].reset();
                    loadVehicles();
                    $('.tab-button[data-tab="vehicles-list"]').click();
                } else {
                    showError(response.message || 'Failed to add vehicle');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while adding the vehicle');
            }
        });
    }

    function updateVehicle() {
        const vehicleData = {
            id: $('#editVehicleId').val(),
            vehicleType: $('#editVehicleType').val(),
            model: $('#editVehicleModel').val(),
            registrationNumber: $('#editVehicleRegistration').val(),
            seatCapacity: parseInt($('#editVehicleCapacity').val()),
            isAvailable: $('#editVehicleAvailable').val() === 'true'
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/vehicles',
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(vehicleData),
            success: function(response) {
                if (response.code === 200) {
                    showSuccess('Vehicle updated successfully');
                    loadVehicles();
                    $('.tab-button[data-tab="vehicles-list"]').click();
                } else {
                    showError(response.message || 'Failed to update vehicle');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while updating the vehicle');
            }
        });
    }

    function deleteVehicleById(vehicleId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/admin/vehicles/${vehicleId}`,
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    showSuccess('Vehicle deleted successfully');
                    loadVehicles();
                } else {
                    showError(response.message || 'Failed to delete vehicle');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while deleting the vehicle');
            }
        });
    }

    // ==================== BUS MANAGEMENT ====================
    $('#refreshBusesBtn').on('click', function() {
        loadBuses();
        showSuccess('Buses list refreshed');
    });

    $('#addBusForm').on('submit', function(e) {
        e.preventDefault();
        addBus();
    });

    $('#editBusForm').on('submit', function(e) {
        e.preventDefault();
        updateBus();
    });

    function loadBuses() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/buses',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const buses = response.data || [];
                    displayBusesTable(buses);
                } else {
                    $('#busesTable').html('<div class="no-items">Failed to load buses.</div>');
                }
            },
            error: function() {
                $('#busesTable').html('<div class="no-items">An error occurred while loading buses.</div>');
            }
        });
    }

    function displayBusesTable(buses) {
        if (!buses || buses.length === 0) {
            $('#busesTable').html('<div class="no-items">No buses found. Add your first bus!</div>');
            return;
        }

        let tableHTML = `
    <table class="admin-table">
        <thead>
            <tr>
                <th>Bus Number</th>
                <th>Route</th>
                <th>Departure</th>
                <th>Arrival</th>
                <th>Available Seats</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
    `;

        buses.forEach(bus => {
            tableHTML += `
        <tr>
            <td>${bus.busNumber}</td>
            <td>${bus.route}</td>
            <td>${bus.departureTime}</td>
            <td>${bus.arrivalTime}</td>
            <td>${bus.availableSeats}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-outline btn-sm edit-bus-btn"
                            data-bus-id="${bus.id}"
                            data-bus-number="${bus.busNumber}"
                            data-bus-route="${bus.route}"
                            data-bus-departure="${bus.departureTime}"
                            data-bus-arrival="${bus.arrivalTime}"
                            data-bus-seats="${bus.availableSeats}">
                        Edit
                    </button>
                    <button class="btn btn-danger btn-sm delete-bus-btn"
                            data-bus-id="${bus.id}">
                        Delete
                    </button>
                </div>
            </td>
        </tr>
        `;
        });

        tableHTML += `
        </tbody>
    </table>
    `;

        $('#busesTable').html(tableHTML);

        // Add click handlers for edit buttons
        $('.edit-bus-btn').on('click', function() {
            const busId = $(this).data('bus-id');
            const busNumber = $(this).data('bus-number');
            const busRoute = $(this).data('bus-route');
            const busDeparture = $(this).data('bus-departure');
            const busArrival = $(this).data('bus-arrival');
            const busSeats = $(this).data('bus-seats');

            // Fill the edit form
            $('#editBusId').val(busId);
            $('#editBusNumber').val(busNumber);
            $('#editBusRoute').val(busRoute);
            $('#editBusDeparture').val(busDeparture);
            $('#editBusArrival').val(busArrival);
            $('#editBusSeats').val(busSeats);

            // Switch to edit tab
            $('.tab-button[data-tab="edit-bus"]').click();
        });

        // Add click handlers for delete buttons
        $('.delete-bus-btn').on('click', function() {
            const busId = $(this).data('bus-id');
            if (confirm('Are you sure you want to delete this bus?')) {
                deleteBusById(busId);
            }
        });
    }

    function addBus() {
        const busData = {
            busNumber: $('#busNumber').val(),
            route: $('#busRoute').val(),
            departureTime: $('#busDeparture').val(),
            arrivalTime: $('#busArrival').val(),
            availableSeats: parseInt($('#busSeats').val())
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/buses',
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(busData),
            success: function(response) {
                if (response.code === 200) {
                    showSuccess('Bus added successfully');
                    $('#addBusForm')[0].reset();
                    loadBuses();
                    $('.tab-button[data-tab="buses-list"]').click();
                } else if (response.code === 406) {
                    showError('A bus with this number already exists');
                } else {
                    showError(response.message || 'Failed to add bus');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while adding the bus');
            }
        });
    }

    function updateBus() {
        const busData = {
            id: $('#editBusId').val(),
            busNumber: $('#editBusNumber').val(),
            route: $('#editBusRoute').val(),
            departureTime: $('#editBusDeparture').val(),
            arrivalTime: $('#editBusArrival').val(),
            availableSeats: parseInt($('#editBusSeats').val())
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/buses',
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(busData),
            success: function(response) {
                if (response.code === 200) {
                    showSuccess('Bus updated successfully');
                    loadBuses();
                    $('.tab-button[data-tab="buses-list"]').click();
                } else if (response.code === 404) {
                    showError('Bus not found');
                } else if (response.code === 406) {
                    showError('A bus with this number already exists');
                } else {
                    showError(response.message || 'Failed to update bus');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while updating the bus');
            }
        });
    }

    function deleteBusById(busId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/admin/buses/${busId}`,
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    showSuccess('Bus deleted successfully');
                    loadBuses();
                } else if (response.code === 404) {
                    showError('Bus not found');
                } else {
                    showError(response.message || 'Failed to delete bus');
                }
            },
            error: function(xhr) {
                showError(xhr.responseJSON?.message || 'An error occurred while deleting the bus');
            }
        });
    }

    // ==================== REPORTS ====================
    function loadReports(reportType) {
        switch (reportType) {
            case 'user-activity':
                $.ajax({
                    url: 'http://localhost:8080/api/v1/admin/reports/user-activity?role=USER',
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    success: function (response) {
                        if (response.code === 200) {
                            displayUserActivityReport(response.data);
                        } else {
                            $('#userActivityReport').html('<div class="no-items">Failed to load report.</div>');
                        }
                    },
                    error: function () {
                        $('#userActivityReport').html('<div class="no-items">An error occurred while loading the report.</div>');
                    }
                });
                break;
            case 'booking-report':
                $.ajax({
                    url: 'http://localhost:8080/api/v1/admin/reports/bookings',
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    success: function (response) {
                        if (response.code === 200) {
                            displayBookingReport(response.data);
                        } else {
                            $('#bookingReport').html('<div class="no-items">Failed to load report.</div>');
                        }
                    },
                    error: function () {
                        $('#bookingReport').html('<div class="no-items">An error occurred while loading the report.</div>');
                    }
                });
                break;
            case 'payment-report':
                $.ajax({
                    url: 'http://localhost:8080/api/v1/admin/reports/payments',
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    success: function (response) {
                        if (response.code === 200) {
                            displayPaymentReport(response.data);
                        } else {
                            $('#paymentReport').html('<div class="no-items">Failed to load report.</div>');
                        }
                    },
                    error: function () {
                        $('#paymentReport').html('<div class="no-items">An error occurred while loading the report.</div>');
                    }
                });
                break;
        }
    }

    function displayUserActivityReport(report) {
        let reportHTML = `
        <div class="dashboard-card">
            <h3>User Summary</h3>
            <p>Total users: ${report.totalUsers || 0}</p>
            <p>New users this month: ${Math.floor(Math.random() * 100)}</p>
        </div>
        `;

        $('#userActivityReport').html(reportHTML);
    }

    function displayBookingReport(report) {
        let reportHTML = `
        <div class="dashboard-card">
            <h3>Booking Summary</h3>
            <p>Total bookings: ${report.totalBookings || 0}</p>
            <p>Active bookings: ${Math.floor(Math.random() * report.totalBookings)}</p>
        </div>
        `;

        $('#bookingReport').html(reportHTML);
    }

    function displayPaymentReport(report) {
        const totalRevenue = report.payments ? report.payments.reduce((sum, payment) => sum + (payment.amount || 0), 0) : 0;

        let reportHTML = `
        <div class="dashboard-card">
            <h3>Payment Summary</h3>
            <p>Total payments: ${report.totalPayments || 0}</p>
            <p>Total revenue: $${totalRevenue.toFixed(2)}</p>
        </div>
        `;

        $('#paymentReport').html(reportHTML);
    }

    // ==================== ADMIN MANAGEMENT ====================
    function loadAdmins() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/getAll',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (response) {
                if (response.code === 200) {
                    const admins = response.data || [];
                    displayAdminsTable(admins);
                } else {
                    $('#adminsTable').html('<div class="no-items">Failed to load admins.</div>');
                }
            },
            error: function () {
                $('#adminsTable').html('<div class="no-items">An error occurred while loading admins.</div>');
            }
        });
    }

    function displayAdminsTable(admins) {
        if (!admins || admins.length === 0) {
            $('#adminsTable').html('<div class="no-items">No admins found.</div>');
            return;
        }

        let tableHTML = `
        <table class="admin-table">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Address</th>
                </tr>
            </thead>
            <tbody>
        `;

        admins.forEach(admin => {
            tableHTML += `
            <tr>
                <td>${admin.firstName} ${admin.lastName}</td>
                <td>${admin.email}</td>
                <td>${admin.phoneNumber || 'N/A'}</td>
                <td>${admin.address || 'N/A'}</td>
            </tr>
        `;
        });

        tableHTML += `
            </tbody>
        </table>
        `;

        $('#adminsTable').html(tableHTML);
    }

    $('#addAdminForm').on('submit', function (e) {
        e.preventDefault();
        addAdmin();
    });

    function addAdmin() {
        const adminData = {
            firstName: $('#adminFirstName').val(),
            lastName: $('#adminLastName').val(),
            email: $('#adminEmail').val(),
            password: $('#adminPassword').val(),
            phoneNumber: $('#adminPhone').val(),
            address: $('#adminAddress').val()
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/admin/add',
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(adminData),
            success: function (response) {
                if (response.code === 201) {
                    showSuccess('Admin added successfully');
                    $('#addAdminForm')[0].reset();

                    // Reload admins list
                    loadAdmins();

                    // Switch back to admins list tab
                    $('.tab-button[data-tab="admins-list"]').click();
                } else {
                    showError(response.message || 'Failed to add admin');
                }
            },
            error: function () {
                showError('An error occurred while adding the admin');
            }
        });
    }

    // ==================== UTILITY FUNCTIONS ====================
    function logout() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/auth/logout',
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function () {
                // Clear local storage
                localStorage.removeItem('token');
                localStorage.removeItem('role');
                localStorage.removeItem('email');

                // Redirect to login page
                window.location.href = 'login.html';
            },
            error: function () {
                console.error('Failed to logout');

                // Still clear local storage and redirect
                localStorage.removeItem('token');
                localStorage.removeItem('role');
                localStorage.removeItem('email');
                window.location.href = 'login.html';
            }
        });
    }

    function showSuccess(message) {
        $('#successAlert').text(message).show();
        $('#errorAlert').hide();

        // Auto-hide after 3 seconds
        setTimeout(function () {
            $('#successAlert').hide();
        }, 3000);
    }

    function showError(message) {
        $('#errorAlert').text(message).show();
        $('#successAlert').hide();
    }

    // Show only dashboard section initially
    $('.admin-section').hide();
    $('#dashboard-section').show();
});