/**
 * Journey Pro API Client
 * A utility class to handle API requests to the Journey Pro backend
 */
class ApiClient {
    constructor() {
        this.baseUrl = 'http://localhost:8080/api/v1';
    }

    /**
     * Get the authentication token from local storage
     * @returns {string|null} The auth token or null if not found
     */
    getToken() {
        return localStorage.getItem('token');
    }

    /**
     * Get the user role from local storage
     * @returns {string|null} The user role or null if not found
     */
    getRole() {
        return localStorage.getItem('role');
    }

    /**
     * Check if the user is logged in
     * @returns {boolean} True if the user is logged in, false otherwise
     */
    isLoggedIn() {
        return !!this.getToken();
    }

    /**
     * Check if the user is an admin
     * @returns {boolean} True if the user is an admin, false otherwise
     */
    isAdmin() {
        return this.getRole() === 'ADMIN';
    }

    /**
     * Make an API request
     * @param {string} endpoint - The API endpoint to call
     * @param {string} method - The HTTP method (GET, POST, PUT, DELETE)
     * @param {Object} data - The data to send (for POST/PUT requests)
     * @returns {Promise} A promise resolving to the API response
     */
    async request(endpoint, method = 'GET', data = null) {
        const url = `${this.baseUrl}${endpoint}`;
        const token = this.getToken();

        const options = {
            method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        // Add auth token if available
        if (token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }

        // Add request body for POST/PUT requests
        if (data && (method === 'POST' || method === 'PUT')) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(url, options);

            // Parse JSON response
            const responseData = await response.json();

            // Check for API error
            if (responseData.code !== 200 && responseData.code !== 201) {
                throw new Error(responseData.message || 'API Error');
            }

            return responseData;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    /* Authentication API */

    /**
     * Login a user
     * @param {string} email - The user's email
     * @param {string} password - The user's password
     * @returns {Promise} A promise resolving to the login response
     */
    async login(email, password) {
        const response = await this.request('/auth/authenticate', 'POST', { email, password });

        if (response.code === 200) {
            // Store auth data in local storage
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('role', response.data.role);
            localStorage.setItem('email', response.data.email);
        }

        return response;
    }

    /**
     * Logout the current user
     * @returns {Promise} A promise resolving when logout is complete
     */
    async logout() {
        try {
            await this.request('/auth/logout', 'POST');
        } finally {
            // Clear local storage regardless of API response
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            localStorage.removeItem('email');
        }
    }

    /**
     * Register a new user
     * @param {Object} userData - The user registration data
     * @returns {Promise} A promise resolving to the registration response
     */
    async register(userData) {
        return await this.request('/user/register', 'POST', userData);
    }

    /* User API */

    /**
     * Get the current user's profile
     * @returns {Promise} A promise resolving to the user profile
     */
    async getUserProfile() {
        return await this.request('/user/profile', 'GET');
    }

    /**
     * Update the current user's profile
     * @param {Object} profileData - The updated profile data
     * @returns {Promise} A promise resolving to the update response
     */
    async updateUserProfile(profileData) {
        return await this.request('/user/profile', 'PUT', profileData);
    }

    /**
     * Change the current user's password
     * @param {string} oldPassword - The current password
     * @param {string} newPassword - The new password
     * @returns {Promise} A promise resolving to the password change response
     */
    async changePassword(oldPassword, newPassword) {
        return await this.request('/user/change-password', 'PUT', {
            oldPassword,
            newPassword
        });
    }

    /* Trip API */

    /**
     * Create a new trip
     * @param {Object} tripData - The trip data
     * @returns {Promise} A promise resolving to the trip creation response
     */
    async createTrip(tripData) {
        return await this.request('/user/trips/create', 'POST', tripData);
    }

    /**
     * Get a trip by ID
     * @param {string} tripId - The trip ID
     * @returns {Promise} A promise resolving to the trip data
     */
    async getTrip(tripId) {
        return await this.request(`/user/trips/${tripId}`, 'GET');
    }

    /**
     * Get all trips for the current user
     * @param {string} userId - The user ID
     * @returns {Promise} A promise resolving to the trips data
     */
    async getUserTrips(userId) {
        return await this.request(`/user/trips?userId=${userId}`, 'GET');
    }

    /**
     * Update a trip
     * @param {string} tripId - The trip ID
     * @param {Object} tripData - The updated trip data
     * @returns {Promise} A promise resolving to the update response
     */
    async updateTrip(tripId, tripData) {
        return await this.request(`/user/trips/${tripId}`, 'PUT', tripData);
    }

    /**
     * Delete a trip
     * @param {string} tripId - The trip ID
     * @returns {Promise} A promise resolving to the deletion response
     */
    async deleteTrip(tripId) {
        return await this.request(`/user/trips/${tripId}`, 'DELETE');
    }

    /* Booking API */

    /**
     * Create a new booking
     * @param {Object} bookingData - The booking data
     * @returns {Promise} A promise resolving to the booking creation response
     */
    async createBooking(bookingData) {
        return await this.request('/user/bookings', 'POST', bookingData);
    }

    /**
     * Get a booking by ID
     * @param {string} bookingId - The booking ID
     * @returns {Promise} A promise resolving to the booking data
     */
    async getBooking(bookingId) {
        return await this.request(`/user/bookings/${bookingId}`, 'GET');
    }

    /**
     * Get all bookings for a user
     * @param {string} userId - The user ID
     * @returns {Promise} A promise resolving to the bookings data
     */
    async getUserBookings(userId) {
        return await this.request(`/user/bookings?userId=${userId}`, 'GET');
    }

    /**
     * Cancel a booking
     * @param {string} bookingId - The booking ID
     * @returns {Promise} A promise resolving to the cancellation response
     */
    async cancelBooking(bookingId) {
        return await this.request(`/user/bookings/${bookingId}/cancel`, 'PUT');
    }

    /* Feedback API */

    /**
     * Submit feedback for a trip
     * @param {Object} feedbackData - The feedback data
     * @returns {Promise} A promise resolving to the feedback submission response
     */
    async submitFeedback(feedbackData) {
        return await this.request('/user/feedbacks', 'POST', feedbackData);
    }

    /**
     * Get all feedback for a user
     * @param {string} userId - The user ID
     * @returns {Promise} A promise resolving to the feedback data
     */
    async getUserFeedback(userId) {
        return await this.request(`/user/feedbacks?userId=${userId}`, 'GET');
    }

    /* Hotels API */

    /**
     * Get all hotels
     * @returns {Promise} A promise resolving to the hotels data
     */
    async getHotels() {
        return await this.request('/admin/hotels', 'GET');
    }

    /**
     * Get a hotel by ID
     * @param {string} hotelId - The hotel ID
     * @returns {Promise} A promise resolving to the hotel data
     */
    async getHotel(hotelId) {
        return await this.request(`/admin/hotels/${hotelId}`, 'GET');
    }

    /**
     * Search hotels by location
     * @param {string} location - The location to search for
     * @returns {Promise} A promise resolving to the hotels data
     */
    async searchHotelsByLocation(location) {
        return await this.request(`/admin/hotels/search?location=${encodeURIComponent(location)}`, 'GET');
    }

    /* Vehicles API */

    /**
     * Get all vehicles
     * @returns {Promise} A promise resolving to the vehicles data
     */
    async getVehicles() {
        return await this.request('/admin/vehicles', 'GET');
    }

    /**
     * Get a vehicle by ID
     * @param {string} vehicleId - The vehicle ID
     * @returns {Promise} A promise resolving to the vehicle data
     */
    async getVehicle(vehicleId) {
        return await this.request(`/admin/vehicles/${vehicleId}`, 'GET');
    }

    /**
     * Search vehicles by type
     * @param {string} type - The vehicle type to search for
     * @returns {Promise} A promise resolving to the vehicles data
     */
    async searchVehiclesByType(type) {
        return await this.request(`/admin/vehicles/search?type=${encodeURIComponent(type)}`, 'GET');
    }

    /* Buses API */

    /**
     * Get all buses
     * @returns {Promise} A promise resolving to the buses data
     */
    async getBuses() {
        return await this.request('/admin/buses', 'GET');
    }

    /**
     * Get a bus by ID
     * @param {string} busId - The bus ID
     * @returns {Promise} A promise resolving to the bus data
     */
    async getBus(busId) {
        return await this.request(`/admin/buses/${busId}`, 'GET');
    }

    /**
     * Search buses by route
     * @param {string} route - The route to search for
     * @returns {Promise} A promise resolving to the buses data
     */
    async searchBusesByRoute(route) {
        return await this.request(`/admin/buses/search?route=${encodeURIComponent(route)}`, 'GET');
    }

    /* Admin API */

    /**
     * Get all admins
     * @returns {Promise} A promise resolving to the admins data
     */
    async getAdmins() {
        return await this.request('/admin/getAll', 'GET');
    }

    /**
     * Add a new admin
     * @param {Object} adminData - The admin data
     * @returns {Promise} A promise resolving to the admin creation response
     */
    async addAdmin(adminData) {
        return await this.request('/admin/add', 'POST', adminData);
    }

    /**
     * Update an admin
     * @param {Object} adminData - The updated admin data
     * @returns {Promise} A promise resolving to the update response
     */
    async updateAdmin(adminData) {
        return await this.request('/admin/update', 'PUT', adminData);
    }

    /**
     * Delete an admin
     * @param {string} email - The admin's email
     * @returns {Promise} A promise resolving to the deletion response
     */
    async deleteAdmin(email) {
        return await this.request(`/admin/delete?email=${encodeURIComponent(email)}`, 'DELETE');
    }

    /* Report API */

    /**
     * Generate user activity report
     * @returns {Promise} A promise resolving to the report data
     */
    async getUserActivityReport() {
        return await this.request('/admin/reports/user-activity', 'GET');
    }

    /**
     * Generate booking report
     * @returns {Promise} A promise resolving to the report data
     */
    async getBookingReport() {
        return await this.request('/admin/reports/bookings', 'GET');
    }

    /**
     * Generate payment report
     * @returns {Promise} A promise resolving to the report data
     */
    async getPaymentReport() {
        return await this.request('/admin/reports/payments', 'GET');
    }
}

// Create a global API client instance
const api = new ApiClient();

// Auth guard function to redirect if not logged in
function authGuard() {
    if (!api.isLoggedIn()) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Admin guard function to redirect if not admin
function adminGuard() {
    if (!api.isLoggedIn() || !api.isAdmin()) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Error handling utility
function handleApiError(error, alertElement) {
    console.error('API Error:', error);
    if (alertElement) {
        alertElement.text(error.message || 'An error occurred. Please try again.').show();
    }
}

// Success message utility
function showSuccessMessage(message, alertElement) {
    if (alertElement) {
        alertElement.text(message).show();

        // Auto-hide after 3 seconds
        setTimeout(function() {
            alertElement.hide();
        }, 3000);
    }
}
