$(document).ready(function () {
    // Check if user is logged in
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (!token || role !== 'USER') {
        window.location.href = 'login.html';
        return;
    }

    // Get user ID (placeholder)
    let userId = localStorage.getItem('userId');
    if (!userId) {
        // In a real application, you'd extract the user ID from the token or get it from an API call
        const userInfo = JSON.parse(atob(token.split('.')[1]));
        userId = userInfo.sub; // Assuming the subject contains the user's email or ID
    }

    // Load user's trips
    loadTrips(userId);

    // Handle filter options
    $('.filter-option').on('click', function() {
        $('.filter-option').removeClass('active');
        $(this).addClass('active');

        const filter = $(this).data('filter');
        filterTrips(filter);
    });

    // Handle search
    $('#searchInput').on('input', function() {
        const searchTerm = $(this).val().toLowerCase();
        searchTrips(searchTerm);
    });

    // Trip deletion
    let tripToDelete = null;

    // Close modal when clicking the X or Cancel button
    $('#closeDeleteModal, #cancelDelete').on('click', function() {
        $('#deleteModal').hide();
        tripToDelete = null;
    });

    // Close modal when clicking outside of it
    $(window).on('click', function(e) {
        if (e.target.className === 'modal') {
            $('.modal').hide();
            tripToDelete = null;
        }
    });

    // Confirm delete button
    $('#confirmDelete').on('click', function() {
        if (tripToDelete) {
            deleteTrip(tripToDelete);
            $('#deleteModal').hide();
        }
    });

    // Function to load and display trips
    function loadTrips(userId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/user/trips?userId=${userId}`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            success: function (response) {
                if (response.code === 200 && response.data) {
                    const trips = response.data;
                    updateTripStats(trips);
                    displayTrips(trips);
                } else {
                    showNoTripsMessage();
                }
            },
            error: function () {
                $('#errorMessage').text('An error occurred while loading trips. Please try again.');
                $('#errorAlert').fadeIn();
                showNoTripsMessage('Error loading trips. Please try again later.');
            }
        });
    }

    function updateTripStats(trips) {
        if (!trips || trips.length === 0) {
            return;
        }

        const total = trips.length;
        const upcoming = trips.filter(trip => trip.status === 'UPCOMING').length;
        const ongoing = trips.filter(trip => trip.status === 'ONGOING').length;
        const completed = trips.filter(trip => trip.status === 'COMPLETED').length;

        // Update stats
        $('#totalTrips').text(total);
        $('#upcomingTrips').text(upcoming);
        $('#ongoingTrips').text(ongoing);
        $('#completedTrips').text(completed);

        // Animate the numbers
        animateNumbers();
    }

    function animateNumbers() {
        $('.stat-value').each(function() {
            const $this = $(this);
            const countTo = parseInt($this.text());

            $({ countNum: 0 }).animate({
                countNum: countTo
            }, {
                duration: 1000,
                easing: 'swing',
                step: function() {
                    $this.text(Math.floor(this.countNum));
                },
                complete: function() {
                    $this.text(this.countNum);
                }
            });
        });
    }

    function displayTrips(trips) {
        // Clear existing trips and loading indicator
        $('#tripsContainer').empty();

        if (!trips || trips.length === 0) {
            showNoTripsMessage();
            return;
        }

        // Create table
        const tableHTML = `
                <table class="trip-table">
                    <thead>
                        <tr>
                            <th>Destination</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="tripsList">
                        <!-- Trip rows will be added here -->
                    </tbody>
                </table>
            `;

        $('#tripsContainer').html(tableHTML);

        // Sort trips by start date (most recent first)
        trips.sort((a, b) => new Date(b.startDate) - new Date(a.startDate));

        // Add each trip to the table
        trips.forEach(function (trip) {
            let statusClass = '';
            let statusIcon = '';

            switch (trip.status) {
                case 'UPCOMING':
                    statusClass = 'status-upcoming';
                    statusIcon = 'calendar-alt';
                    break;
                case 'ONGOING':
                    statusClass = 'status-ongoing';
                    statusIcon = 'plane';
                    break;
                case 'COMPLETED':
                    statusClass = 'status-completed';
                    statusIcon = 'check-circle';
                    break;
            }

            // Format dates
            const startDate = new Date(trip.startDate).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });

            const endDate = new Date(trip.endDate).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });

            // Create the row HTML
            const tripRow = `
                    <tr data-status="${trip.status.toLowerCase()}" data-destination="${trip.destination.toLowerCase()}">
                        <td>${trip.destination}</td>
                        <td>${startDate}</td>
                        <td>${endDate}</td>
                        <td>
                            <span class="status-badge ${statusClass}">
                                <i class="fas fa-${statusIcon}"></i> ${trip.status}
                            </span>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <a href="trip-details.html?id=${trip.id}" class="action-btn view-btn" title="View Trip">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <a href="trip-edit.html?id=${trip.id}" class="action-btn edit-btn" title="Edit Trip">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <a href="#" class="action-btn delete-btn" data-id="${trip.id}" title="Delete Trip">
                                    <i class="fas fa-trash-alt"></i>
                                </a>
                                <a href="booking-create.html?tripId=${trip.id}" class="action-btn book-btn" title="Add Booking">
                                    <i class="fas fa-plus"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                `;

            $('#tripsList').append(tripRow);
        });

        // Add event listener for delete buttons
        $('.delete-btn').on('click', function (e) {
            e.preventDefault();
            tripToDelete = $(this).data('id');
            $('#deleteModal').css('display', 'flex');
        });
    }

    function filterTrips(filter) {
        // Get all trip rows
        const tripRows = $('#tripsList tr');

        if (filter === 'all') {
            tripRows.show();
            return;
        }

        // Hide all trips
        tripRows.hide();

        // Show trips matching the filter
        tripRows.filter(`[data-status="${filter}"]`).show();

        // Check if search is also active
        const searchTerm = $('#searchInput').val().toLowerCase();
        if (searchTerm) {
            // Hide filtered trips that don't match the search term
            tripRows.filter(`[data-status="${filter}"]`).each(function() {
                const destination = $(this).data('destination');
                if (!destination.includes(searchTerm)) {
                    $(this).hide();
                }
            });
        }

        // Show "no trips" message if no trips are visible
        if ($('#tripsList tr:visible').length === 0) {
            // Only show the message if not already shown
            if ($('#noTripsFiltered').length === 0) {
                const message = `
                        <tr id="noTripsFiltered">
                            <td colspan="5" style="text-align: center; padding: 30px;">
                                <i class="fas fa-filter" style="font-size: 24px; color: #dee2e6; margin-bottom: 10px; display: block;"></i>
                                <p>No ${filter} trips found.</p>
                            </td>
                        </tr>
                    `;
                $('#tripsList').append(message);
            }
        } else {
            // Remove the message if it exists and trips are visible
            $('#noTripsFiltered').remove();
        }
    }

    function searchTrips(searchTerm) {
        // Get all trip rows
        const tripRows = $('#tripsList tr');

        if (!searchTerm) {
            // Reset to show all or filtered rows
            const activeFilter = $('.filter-option.active').data('filter');
            if (activeFilter === 'all') {
                tripRows.show();
            } else {
                tripRows.hide();
                tripRows.filter(`[data-status="${activeFilter}"]`).show();
            }
            return;
        }

        // Get active filter
        const activeFilter = $('.filter-option.active').data('filter');

        // Hide all trips first
        tripRows.hide();

        if (activeFilter === 'all') {
            // Show trips matching the search term
            tripRows.filter(function() {
                return $(this).data('destination').includes(searchTerm);
            }).show();
        } else {
            // Show trips matching both the filter and search term
            tripRows.filter(function() {
                return $(this).data('status') === activeFilter &&
                    $(this).data('destination').includes(searchTerm);
            }).show();
        }

        // Show "no trips" message if no trips are visible
        if ($('#tripsList tr:visible').length === 0) {
            // Only show the message if not already shown
            if ($('#noTripsSearched').length === 0) {
                const message = `
                        <tr id="noTripsSearched">
                            <td colspan="5" style="text-align: center; padding: 30px;">
                                <i class="fas fa-search" style="font-size: 24px; color: #dee2e6; margin-bottom: 10px; display: block;"></i>
                                <p>No trips matching "${searchTerm}" found.</p>
                            </td>
                        </tr>
                    `;
                $('#tripsList').append(message);
            }
        } else {
            // Remove the message if it exists and trips are visible
            $('#noTripsSearched').remove();
        }
    }

    function showNoTripsMessage(message = null) {
        const defaultMessage = `
                <div class="no-trips">
                    <i class="fas fa-suitcase-rolling"></i>
                    <h3>No Trips Found</h3>
                    <p>${message || "You haven't created any trips yet. Click the 'Create New Trip' button to get started."}</p>
                    <a href="trip-create.html" class="btn" style="margin-top: 15px;">
                        <i class="fas fa-plus"></i> Create Your First Trip
                    </a>
                </div>
            `;

        $('#tripsContainer').html(defaultMessage);
    }

    function deleteTrip(tripId) {
        // Show loading state on delete button
        const deleteBtn = $('#confirmDelete');
        const originalText = deleteBtn.html();
        deleteBtn.html('<i class="fas fa-circle-notch fa-spin"></i> Deleting...').prop('disabled', true);

        $.ajax({
            url: `http://localhost:8080/api/v1/user/trips/${tripId}`,
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
            success: function (response) {
                if (response.code === 200) {
                    $('#successMessage').text('Trip deleted successfully');
                    $('#successAlert').fadeIn();

                    // Reset the delete button
                    deleteBtn.html(originalText).prop('disabled', false);

                    // Reload the trips list
                    loadTrips(userId);

                    // Auto-hide the success message after 3 seconds
                    setTimeout(function() {
                        $('#successAlert').fadeOut();
                    }, 3000);
                } else {
                    $('#errorMessage').text(response.message || 'Failed to delete trip');
                    $('#errorAlert').fadeIn();
                    deleteBtn.html(originalText).prop('disabled', false);
                }
            },
            error: function () {
                $('#errorMessage').text('An error occurred while deleting trip. Please try again.');
                $('#errorAlert').fadeIn();
                deleteBtn.html(originalText).prop('disabled', false);
            }
        });
    }
});