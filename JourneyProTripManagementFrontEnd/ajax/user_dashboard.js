$(document).ready(function() {
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
        userId = "current-user";
    }

    // Load user profile
    loadUserProfile();

    // Load trips, bookings, feedback, and notifications
    loadTrips(userId);
    loadBookings(userId);
    loadFeedback(userId);
    loadNotifications(userId);

    // Handle logout
    $('#logoutBtn').on('click', function() {
        showLogoutConfirmation();
    });

    // Handle mark all notifications as read
    $('#markAllRead').on('click', function(e) {
        e.preventDefault();
        markAllNotificationsAsRead(userId);
    });

    function showLogoutConfirmation() {
        // Create a confirmation dialog
        const confirmDialog = $(`
                <div id="logoutConfirmation" style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000;">
                    <div style="background: white; padding: 25px; border-radius: 10px; width: 90%; max-width: 400px; box-shadow: 0 5px 15px rgba(0,0,0,0.2);">
                        <h3 style="margin-top: 0; margin-bottom: 20px; color: #343a40;">Confirm Logout</h3>
                        <p style="margin-bottom: 25px; color: #6c757d;">Are you sure you want to log out of your account?</p>
                        <div style="display: flex; gap: 10px; justify-content: flex-end;">
                            <button id="cancelLogout" style="padding: 10px 15px; background: #6c757d; color: white; border: none; border-radius: 5px; cursor: pointer;">Cancel</button>
                            <button id="confirmLogout" style="padding: 10px 15px; background: #2ecc71; color: white; border: none; border-radius: 5px; cursor: pointer;">Yes, Logout</button>
                        </div>
                    </div>
                </div>
            `);

        $('body').append(confirmDialog);

        // Handle button clicks
        $('#cancelLogout').on('click', function() {
            confirmDialog.remove();
        });

        $('#confirmLogout').on('click', function() {
            confirmDialog.remove();
            logout();
        });

        // Close when clicking outside
        confirmDialog.on('click', function(e) {
            if (e.target === this) {
                confirmDialog.remove();
            }
        });
    }

    function loadUserProfile() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/user/profile',
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const user = response.data;
                    $('#userFirstName').text(user.firstName);

                    // Store user ID if available
                    if (user.id) {
                        localStorage.setItem('userId', user.id);
                        userId = user.id;

                        // Reload data with actual user ID
                        loadTrips(userId);
                        loadBookings(userId);
                        loadFeedback(userId);
                        loadNotifications(userId);
                    }
                }
            },
            error: function() {
                console.error('Failed to load user profile');
            }
        });
    }

    function loadTrips(userId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/user/trips?userId=${userId}`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const trips = response.data || [];

                    // Update stats with animation
                    animateCounter('totalTrips', trips.length);

                    // Count active trips (UPCOMING and ONGOING)
                    const activeTrips = trips.filter(trip =>
                        trip.status === 'UPCOMING' || trip.status === 'ONGOING'
                    ).length;
                    animateCounter('activeTrips', activeTrips);

                    // Display upcoming trips
                    displayUpcomingTrips(trips);
                } else {
                    $('#upcomingTrips').html(`
                            <div class="no-items">
                                <i class="fas fa-exclamation-circle"></i>
                                <p>Failed to load trips.</p>
                            </div>
                        `);
                }
            },
            error: function() {
                $('#upcomingTrips').html(`
                        <div class="no-items">
                            <i class="fas fa-exclamation-circle"></i>
                            <p>An error occurred while loading trips.</p>
                        </div>
                    `);
            }
        });
    }

    function displayUpcomingTrips(trips) {
        let tripsHTML = '';

        if (!trips || trips.length === 0) {
            tripsHTML = `
                    <div class="no-items">
                        <i class="fas fa-suitcase"></i>
                        <p>You have no upcoming trips. Click "Plan New Trip" to get started!</p>
                    </div>
                `;
        } else {
            // Sort trips by start date (most recent first)
            const sortedTrips = [...trips].sort((a, b) =>
                new Date(a.startDate) - new Date(b.startDate)
            );

            // Filter for upcoming and ongoing trips
            const upcomingTrips = sortedTrips.filter(trip =>
                trip.status === 'UPCOMING' || trip.status === 'ONGOING'
            );

            if (upcomingTrips.length === 0) {
                tripsHTML = `
                        <div class="no-items">
                            <i class="fas fa-suitcase"></i>
                            <p>You have no upcoming trips. Click "Plan New Trip" to get started!</p>
                        </div>
                    `;
            } else {
                // Display up to 5 upcoming trips
                const tripsToShow = upcomingTrips.slice(0, 5);

                tripsToShow.forEach(trip => {
                    // Format dates
                    const startDate = new Date(trip.startDate).toLocaleDateString('en-US', {
                        month: 'short',
                        day: 'numeric',
                        year: 'numeric'
                    });
                    const endDate = new Date(trip.endDate).toLocaleDateString('en-US', {
                        month: 'short',
                        day: 'numeric',
                        year: 'numeric'
                    });

                    // Calculate days until trip
                    const today = new Date();
                    const tripStart = new Date(trip.startDate);
                    const daysUntil = Math.ceil((tripStart - today) / (1000 * 60 * 60 * 24));

                    let daysUntilText = '';
                    if (trip.status === 'UPCOMING') {
                        daysUntilText = daysUntil === 0 ?
                            '<span style="color: #e74c3c;">Starting today!</span>' :
                            `<span style="color: #3498db;">In ${daysUntil} days</span>`;
                    }

                    // Determine status class and icon
                    let statusClass = '';
                    let statusIcon = '';
                    switch(trip.status) {
                        case 'UPCOMING':
                            statusClass = 'status-upcoming';
                            statusIcon = 'calendar-alt';
                            break;
                        case 'ONGOING':
                            statusClass = 'status-ongoing';
                            statusIcon = 'plane';
                            break;
                    }

                    tripsHTML += `
                            <div class="list-item">
                                <div class="content">
                                    <div class="title"><i class="fas fa-map-marker-alt"></i> ${trip.destination}</div>
                                    <div class="details">${startDate} - ${endDate} ${daysUntilText}</div>
                                </div>
                                <div class="actions">
                                    <span class="status-badge ${statusClass}"><i class="fas fa-${statusIcon}"></i> ${trip.status}</span>
                                    <a href="trip-details.html?id=${trip.id}" class="btn btn-outline btn-sm">View</a>
                                </div>
                            </div>
                        `;
                });
            }
        }

        $('#upcomingTrips').html(tripsHTML);
    }

    function loadBookings(userId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/user/bookings?userId=${userId}`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const bookings = response.data || [];

                    // Update stats with animation
                    animateCounter('totalBookings', bookings.length);

                    // Display recent bookings
                    displayRecentBookings(bookings);

                    // Set up view all bookings link
                    if (bookings.length > 0 && bookings[0].tripId) {
                        $('#viewAllBookings').attr('href', `trip-details.html?id=${bookings[0].tripId}`);
                    } else {
                        $('#viewAllBookings').attr('href', 'trip-list.html');
                    }
                } else {
                    $('#recentBookings').html(`
                            <div class="no-items">
                                <i class="fas fa-exclamation-circle"></i>
                                <p>Failed to load bookings.</p>
                            </div>
                        `);
                }
            },
            error: function() {
                $('#recentBookings').html(`
                        <div class="no-items">
                            <i class="fas fa-exclamation-circle"></i>
                            <p>An error occurred while loading bookings.</p>
                        </div>
                    `);
            }
        });
    }

    function displayRecentBookings(bookings) {
        let bookingsHTML = '';

        if (!bookings || bookings.length === 0) {
            bookingsHTML = `
                    <div class="no-items">
                        <i class="fas fa-ticket-alt"></i>
                        <p>You have no bookings yet.</p>
                    </div>
                `;
        } else {
            // Sort bookings by creation date (most recent first)
            const sortedBookings = [...bookings].sort((a, b) =>
                new Date(b.createdAt) - new Date(a.createdAt)
            );

            // Show up to 5 most recent bookings
            const bookingsToShow = sortedBookings.slice(0, 5);

            bookingsToShow.forEach(booking => {
                const createdDate = new Date(booking.createdAt).toLocaleDateString('en-US', {
                    month: 'short',
                    day: 'numeric',
                    year: 'numeric'
                });

                let bookingTypeText = '';
                let bookingIcon = '';
                switch(booking.bookingType) {
                    case 'HOTEL':
                        bookingTypeText = 'Hotel Booking';
                        bookingIcon = 'hotel';
                        break;
                    case 'BUS':
                        bookingTypeText = 'Bus Ticket';
                        bookingIcon = 'bus';
                        break;
                    case 'VEHICLE':
                        bookingTypeText = 'Vehicle Rental';
                        bookingIcon = 'car';
                        break;
                    default:
                        bookingTypeText = booking.bookingType;
                        bookingIcon = 'bookmark';
                }

                // Status badge
                const statusClass = booking.status === 'CONFIRMED' ? 'status-confirmed' : 'status-canceled';
                const statusIcon = booking.status === 'CONFIRMED' ? 'check-circle' : 'ban';

                bookingsHTML += `
                        <div class="list-item">
                            <div class="content">
                                <div class="title"><i class="fas fa-${bookingIcon}"></i> ${bookingTypeText}</div>
                                <div class="details">Amount: $${booking.amount.toFixed(2)} • ${createdDate}</div>
                            </div>
                            <div class="actions">
                                <span class="status-badge ${statusClass}"><i class="fas fa-${statusIcon}"></i> ${booking.status}</span>
                                <a href="booking-details.html?id=${booking.id}" class="btn btn-outline btn-sm">View</a>
                            </div>
                        </div>
                    `;
            });
        }

        $('#recentBookings').html(bookingsHTML);
    }

    function loadFeedback(userId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/user/feedbacks?userId=${userId}`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const feedbacks = response.data || [];
                    displayLatestFeedback(feedbacks);
                } else {
                    $('#latestFeedback').html(`
                            <div class="no-items">
                                <i class="fas fa-exclamation-circle"></i>
                                <p>Failed to load feedback.</p>
                            </div>
                        `);
                }
            },
            error: function() {
                $('#latestFeedback').html(`
                        <div class="no-items">
                            <i class="fas fa-exclamation-circle"></i>
                            <p>An error occurred while loading feedback.</p>
                        </div>
                    `);
            }
        });
    }

    function displayLatestFeedback(feedbacks) {
        let feedbackHTML = '';

        if (!feedbacks || feedbacks.length === 0) {
            feedbackHTML = `
                    <div class="no-items">
                        <i class="fas fa-comment-alt"></i>
                        <p>You have not submitted any feedback yet.</p>
                    </div>
                `;
        } else {
            // Sort feedback by creation date (most recent first)
            const sortedFeedback = [...feedbacks].sort((a, b) =>
                new Date(b.createdAt) - new Date(a.createdAt)
            );

            // Show up to 5 most recent feedback entries
            const feedbackToShow = sortedFeedback.slice(0, 5);

            feedbackToShow.forEach(feedback => {
                const createdDate = new Date(feedback.createdAt).toLocaleDateString('en-US', {
                    month: 'short',
                    day: 'numeric',
                    year: 'numeric'
                });

                const stars = Array(feedback.rating).fill('<i class="fas fa-star star"></i>').join('') +
                    Array(5 - feedback.rating).fill('<i class="far fa-star star"></i>').join('');

                // Get trip information if available
                let tripInfo = 'Trip';
                if (feedback.tripDestination) {
                    tripInfo = feedback.tripDestination;
                }

                feedbackHTML += `
                        <div class="list-item">
                            <div class="content">
                                <div class="title"><i class="fas fa-map-marker-alt"></i> ${tripInfo}</div>
                                <div class="details">
                                    <div>${stars} • ${createdDate}</div>
                                    <div style="margin-top: 5px; font-style: italic;">
                                        "${feedback.comment.length > 60 ? feedback.comment.substring(0, 60) + '...' : feedback.comment}"
                                    </div>
                                </div>
                            </div>
                            <div class="actions">
                                <a href="trip-details.html?id=${feedback.tripId}" class="btn btn-outline btn-sm">View Trip</a>
                            </div>
                        </div>
                    `;
            });
        }

        $('#latestFeedback').html(feedbackHTML);
    }

    function loadNotifications(userId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/user/notifications?userId=${userId}`,
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    const notifications = response.data || [];
                    displayNotifications(notifications);
                } else {
                    $('#notifications').html(`
                            <div class="no-items">
                                <i class="fas fa-exclamation-circle"></i>
                                <p>Failed to load notifications.</p>
                            </div>
                        `);
                }
            },
            error: function() {
                $('#notifications').html(`
                        <div class="no-items">
                            <i class="fas fa-exclamation-circle"></i>
                            <p>An error occurred while loading notifications.</p>
                        </div>
                    `);
            }
        });
    }

    function displayNotifications(notifications) {
        let notificationsHTML = '';

        if (!notifications || notifications.length === 0) {
            notificationsHTML = `
                    <div class="no-items">
                        <i class="fas fa-bell-slash"></i>
                        <p>You have no notifications.</p>
                    </div>
                `;
            $('#markAllRead').hide();
        } else {
            // Sort notifications by creation date (most recent first)
            const sortedNotifications = [...notifications].sort((a, b) =>
                new Date(b.createdAt) - new Date(a.createdAt)
            );

            // Count unread notifications
            const unreadCount = sortedNotifications.filter(notification => !notification.isRead).length;

            // Show up to 5 most recent notifications
            const notificationsToShow = sortedNotifications.slice(0, 5);

            notificationsToShow.forEach(notification => {
                const createdDate = new Date(notification.createdAt).toLocaleDateString('en-US', {
                    month: 'short',
                    day: 'numeric',
                    year: 'numeric'
                });
                const unreadClass = notification.isRead ? '' : 'unread';
                const notificationIcon = notification.isRead ? 'fas fa-envelope-open' : 'fas fa-envelope';

                notificationsHTML += `
                        <div class="list-item ${unreadClass}" data-id="${notification.id}">
                            <div class="content">
                                <div class="title"><i class="${notificationIcon}"></i> ${notification.message}</div>
                                <div class="date">${createdDate}</div>
                            </div>
                            ${!notification.isRead ?
                    `<div class="actions">
                                    <button class="btn btn-outline btn-sm mark-read" data-id="${notification.id}">
                                        <i class="fas fa-check"></i> Mark as read
                                    </button>
                                </div>` : ''
                }
                        </div>
                    `;
            });

            // If there are unread notifications, add a badge to the dashboard
            if (unreadCount > 0) {
                $('#markAllRead').html(`Mark all as read <span class="notification-count">${unreadCount}</span>`);
                $('#markAllRead').show();
            } else {
                $('#markAllRead').hide();
            }
        }

        $('#notifications').html(notificationsHTML);

        // Attach event handler for mark as read buttons
        $('.mark-read').on('click', function() {
            const notificationId = $(this).data('id');
            markNotificationAsRead(notificationId);
        });
    }

    function markNotificationAsRead(notificationId) {
        // Show loading state
        const btn = $(`.mark-read[data-id="${notificationId}"]`);
        const originalContent = btn.html();
        btn.html('<i class="fas fa-circle-notch fa-spin"></i>').prop('disabled', true);

        $.ajax({
            url: `http://localhost:8080/api/v1/user/notifications/${notificationId}/read`,
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                if (response.code === 200) {
                    // Update UI with animation
                    $(`.list-item[data-id="${notificationId}"]`).removeClass('unread').find('.title i').removeClass('fa-envelope').addClass('fa-envelope-open');
                    btn.fadeOut(300, function() {
                        $(this).remove();
                    });

                    // Reload notifications to update counts
                    setTimeout(() => {
                        loadNotifications(userId);
                    }, 500);
                } else {
                    // Restore button
                    btn.html(originalContent).prop('disabled', false);
                }
            },
            error: function() {
                console.error('Failed to mark notification as read');
                // Restore button
                btn.html(originalContent).prop('disabled', false);
            }
        });
    }

    function markAllNotificationsAsRead(userId) {
        // Show loading state
        const allReadBtn = $('#markAllRead');
        const originalContent = allReadBtn.html();
        allReadBtn.html('<i class="fas fa-circle-notch fa-spin"></i> Processing...').prop('disabled', true);

        // In a real implementation, you would have a backend endpoint
        // to mark all notifications as read for a user. Here we're simulating it by making
        // individual calls for each unread notification.
        const markReadPromises = [];

        $('.mark-read').each(function() {
            const notificationId = $(this).data('id');
            const promise = new Promise((resolve) => {
                $.ajax({
                    url: `http://localhost:8080/api/v1/user/notifications/${notificationId}/read`,
                    method: 'PUT',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    success: function() {
                        resolve();
                    },
                    error: function() {
                        resolve();
                    }
                });
            });
            markReadPromises.push(promise);
        });

        Promise.all(markReadPromises).then(() => {
            // Reload notifications after all are marked as read
            loadNotifications(userId);
            // Restore button
            allReadBtn.html(originalContent).prop('disabled', false);
        });
    }

    function animateCounter(elementId, targetValue) {
        const element = document.getElementById(elementId);
        const startValue = parseInt(element.textContent) || 0;
        const duration = 1000; // Animation duration in milliseconds
        const steps = 20; // Number of steps in the animation
        const stepValue = (targetValue - startValue) / steps;
        const stepTime = duration / steps;
        let currentValue = startValue;
        let currentStep = 0;

        const interval = setInterval(() => {
            currentStep++;
            currentValue += stepValue;
            if (currentStep >= steps) {
                clearInterval(interval);
                currentValue = targetValue;
            }
            element.textContent = Math.round(currentValue);
        }, stepTime);
    }

    function logout() {
        // Show loading overlay
        const loadingOverlay = $(`
                <div style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000;">
                    <div style="background: white; padding: 30px; border-radius: 10px; text-align: center;">
                        <i class="fas fa-circle-notch fa-spin" style="font-size: 30px; color: #2ecc71; margin-bottom: 15px;"></i>
                        <div style="font-weight: 500;">Logging out...</div>
                    </div>
                </div>
            `);

        $('body').append(loadingOverlay);

        $.ajax({
            url: 'http://localhost:8080/api/v1/auth/logout',
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function() {
                // Clear local storage
                localStorage.removeItem('token');
                localStorage.removeItem('role');
                localStorage.removeItem('userId');

                // Redirect to login page
                window.location.href = 'login.html';
            },
            error: function() {
                console.error('Failed to logout');

                // Still clear local storage and redirect
                localStorage.removeItem('token');
                localStorage.removeItem('role');
                localStorage.removeItem('userId');
                window.location.href = 'login.html';
            }
        });
    }
});