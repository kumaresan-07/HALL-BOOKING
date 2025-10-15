// Data Storage
let currentUser = null;
let bookingRequests = [];
let requestCounter = 1000;

// User Database
const users = {
    'advisor': { username: 'advisor', password: 'adv123', role: 'ADMIN', fullName: 'Dr. Kumar Sharma', designation: 'Advisor' },
    'asstadv': { username: 'asstadv', password: 'asst123', role: 'ADMIN', fullName: 'Prof. Priya Singh', designation: 'Assistant Advisor' },
    'hod': { username: 'hod', password: 'hod123', role: 'ADMIN', fullName: 'Dr. Rajesh Verma', designation: 'Head of Department' },
    'student1': { username: 'student1', password: 'stu123', role: 'STUDENT', fullName: 'Amit Kumar', designation: 'Student Coordinator' },
    'student2': { username: 'student2', password: 'stu456', role: 'STUDENT', fullName: 'Neha Patel', designation: 'Event Coordinator' }
};

// Venues Database
const venues = {
    'C V Ramanujam Hall': { name: 'C V Ramanujam Hall', capacity: 500, facilities: 'Projector, AC, Sound System' },
    'Radhakrishnan Hall': { name: 'Radhakrishnan Hall', capacity: 300, facilities: 'Projector, AC' },
    'Kamarajar Hall': { name: 'Kamarajar Hall', capacity: 400, facilities: 'Projector, AC, Stage' },
    'Periyar Hall': { name: 'Periyar Hall', capacity: 250, facilities: 'Projector, Whiteboard' },
    'Annadhurai Hall': { name: 'Annadhurai Hall', capacity: 350, facilities: 'AC, Sound System, Stage' },
    'Natesa Hall': { name: 'Natesa Hall', capacity: 200, facilities: 'Projector, AC' }
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Load saved data from localStorage
    loadData();
    
    // Setup event listeners
    document.getElementById('loginForm').addEventListener('submit', handleLogin);
    document.getElementById('bookingForm').addEventListener('submit', handleBookingSubmit);
    
    // Set minimum date to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('eventDate').setAttribute('min', today);
    document.getElementById('availabilityDate').setAttribute('min', today);
    
    // Populate venue selects
    populateVenueSelects();
});

// Login Handler
function handleLogin(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorDiv = document.getElementById('loginError');
    
    const user = users[username];
    
    if (user && user.password === password) {
        currentUser = user;
        errorDiv.classList.remove('show');
        
        // Show appropriate dashboard
        document.getElementById('loginPage').classList.remove('active');
        
        if (user.role === 'ADMIN') {
            document.getElementById('adminDashboard').classList.add('active');
            document.getElementById('adminName').textContent = `${user.fullName} (${user.designation})`;
            loadAdminDashboard();
        } else {
            document.getElementById('studentDashboard').classList.add('active');
            document.getElementById('studentName').textContent = `${user.fullName}`;
            loadStudentDashboard();
        }
        
        showToast('Login successful! Welcome ' + user.fullName, 'success');
        
        // Reset form
        document.getElementById('loginForm').reset();
    } else {
        errorDiv.textContent = '❌ Invalid username or password!';
        errorDiv.classList.add('show');
    }
}

// Logout Handler
function logout() {
    currentUser = null;
    document.getElementById('adminDashboard').classList.remove('active');
    document.getElementById('studentDashboard').classList.remove('active');
    document.getElementById('loginPage').classList.add('active');
    showToast('Logged out successfully', 'success');
}

// Admin Dashboard Functions
function loadAdminDashboard() {
    updatePendingCount();
    loadPendingRequests();
    loadApprovedRequests();
    loadRejectedRequests();
    loadVenuesList();
    populateScheduleVenues();
}

function showAdminSection(section) {
    // Remove active from all menu items and sections
    document.querySelectorAll('.sidebar .menu-item').forEach(item => item.classList.remove('active'));
    document.querySelectorAll('.content-section').forEach(sec => sec.classList.remove('active'));
    
    // Add active to selected
    event.target.closest('.menu-item').classList.add('active');
    
    switch(section) {
        case 'pending':
            document.getElementById('pendingSection').classList.add('active');
            loadPendingRequests();
            break;
        case 'approved':
            document.getElementById('approvedSection').classList.add('active');
            loadApprovedRequests();
            break;
        case 'rejected':
            document.getElementById('rejectedSection').classList.add('active');
            loadRejectedRequests();
            break;
        case 'venues':
            document.getElementById('venuesSection').classList.add('active');
            loadVenuesList();
            break;
        case 'schedule':
            document.getElementById('scheduleSection').classList.add('active');
            populateScheduleVenues();
            break;
    }
}

function updatePendingCount() {
    const count = bookingRequests.filter(req => req.status === 'PENDING').length;
    document.getElementById('pendingCount').textContent = count;
}

function loadPendingRequests() {
    const container = document.getElementById('pendingRequests');
    const pending = bookingRequests.filter(req => req.status === 'PENDING');
    
    if (pending.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <div class="empty-state-text">No pending requests</div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = pending.map(req => createRequestCard(req, true)).join('');
}

function loadApprovedRequests() {
    const container = document.getElementById('approvedRequests');
    const approved = bookingRequests.filter(req => req.status === 'APPROVED');
    
    if (approved.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <div class="empty-state-text">No approved bookings</div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = approved.map(req => createRequestCard(req, false)).join('');
}

function loadRejectedRequests() {
    const container = document.getElementById('rejectedRequests');
    const rejected = bookingRequests.filter(req => req.status === 'REJECTED');
    
    if (rejected.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <div class="empty-state-text">No rejected bookings</div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = rejected.map(req => createRequestCard(req, false)).join('');
}

function createRequestCard(req, showActions) {
    const statusClass = req.status === 'APPROVED' ? 'status-approved' : 
                       req.status === 'REJECTED' ? 'status-rejected' : 'status-pending';
    const statusIcon = req.status === 'APPROVED' ? '✅' : 
                      req.status === 'REJECTED' ? '❌' : '⏳';
    
    let actionsHTML = '';
    if (showActions) {
        actionsHTML = `
            <div class="request-actions">
                <button class="btn btn-success" onclick="openApprovalModal(${req.requestId}, 'approve')">
                    ✅ Approve
                </button>
                <button class="btn btn-danger" onclick="openApprovalModal(${req.requestId}, 'reject')">
                    ❌ Reject
                </button>
            </div>
        `;
    }
    
    let reviewInfo = '';
    if (req.approvedBy) {
        reviewInfo = `
            <div class="request-detail">
                <strong>Reviewed by:</strong> ${req.approvedBy}
            </div>
        `;
    }
    if (req.remarks) {
        reviewInfo += `
            <div class="request-detail">
                <strong>Remarks:</strong> ${req.remarks}
            </div>
        `;
    }
    
    return `
        <div class="request-card">
            <div class="request-header">
                <span class="request-id">#${req.requestId}</span>
                <span class="status-badge ${statusClass}">${statusIcon} ${req.status}</span>
            </div>
            <div class="request-detail">
                <strong>🏛️ Venue:</strong> ${req.venueName}
            </div>
            <div class="request-detail">
                <strong>📝 Event:</strong> ${req.eventName}
            </div>
            <div class="request-detail">
                <strong>📄 Description:</strong> ${req.eventDescription}
            </div>
            <div class="request-detail">
                <strong>📅 Date:</strong> ${formatDate(req.eventDate)}
            </div>
            <div class="request-detail">
                <strong>🕐 Time:</strong> ${req.startTime} - ${req.endTime}
            </div>
            <div class="request-detail">
                <strong>👤 Requester:</strong> ${req.requesterName}
            </div>
            ${reviewInfo}
            ${actionsHTML}
        </div>
    `;
}

// Modal Functions
let currentRequestId = null;
let currentAction = null;

function openApprovalModal(requestId, action) {
    currentRequestId = requestId;
    currentAction = action;
    
    const request = bookingRequests.find(req => req.requestId === requestId);
    if (!request) return;
    
    const modal = document.getElementById('approvalModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalBody = document.getElementById('modalBody');
    const approveBtn = document.getElementById('approveBtn');
    const rejectBtn = document.getElementById('rejectBtn');
    
    if (action === 'approve') {
        modalTitle.textContent = '✅ Approve Booking Request';
        approveBtn.style.display = 'inline-flex';
        rejectBtn.style.display = 'none';
    } else {
        modalTitle.textContent = '❌ Reject Booking Request';
        approveBtn.style.display = 'none';
        rejectBtn.style.display = 'inline-flex';
    }
    
    modalBody.innerHTML = `
        <div class="request-detail">
            <strong>Request ID:</strong> #${request.requestId}
        </div>
        <div class="request-detail">
            <strong>🏛️ Venue:</strong> ${request.venueName}
        </div>
        <div class="request-detail">
            <strong>📝 Event:</strong> ${request.eventName}
        </div>
        <div class="request-detail">
            <strong>📅 Date:</strong> ${formatDate(request.eventDate)}
        </div>
        <div class="request-detail">
            <strong>🕐 Time:</strong> ${request.startTime} - ${request.endTime}
        </div>
        <div class="request-detail">
            <strong>👤 Requester:</strong> ${request.requesterName}
        </div>
        <div class="input-group" style="margin-top: 20px;">
            <label>${action === 'approve' ? 'Add remarks (optional):' : 'Reason for rejection:'}</label>
            <textarea id="modalRemarks" rows="3" placeholder="Enter your comments..."></textarea>
        </div>
    `;
    
    modal.classList.add('show');
    
    // Setup button handlers
    approveBtn.onclick = () => processApproval('APPROVED');
    rejectBtn.onclick = () => processApproval('REJECTED');
}

function closeModal() {
    document.getElementById('approvalModal').classList.remove('show');
    currentRequestId = null;
    currentAction = null;
}

function processApproval(status) {
    const request = bookingRequests.find(req => req.requestId === currentRequestId);
    if (!request) return;
    
    const remarks = document.getElementById('modalRemarks').value.trim();
    
    if (status === 'REJECTED' && !remarks) {
        showToast('Please provide a reason for rejection', 'error');
        return;
    }
    
    request.status = status;
    request.approvedBy = `${currentUser.fullName} (${currentUser.designation})`;
    request.remarks = remarks;
    
    saveData();
    closeModal();
    
    // Reload dashboard
    loadAdminDashboard();
    
    const message = status === 'APPROVED' ? 
        `✅ Booking request #${currentRequestId} has been approved!` :
        `❌ Booking request #${currentRequestId} has been rejected!`;
    
    showToast(message, status === 'APPROVED' ? 'success' : 'error');
}

// Student Dashboard Functions
function loadStudentDashboard() {
    loadStudentVenues();
    loadMyRequests();
    populateVenueSelects();
}

function showStudentSection(section) {
    // Remove active from all menu items and sections
    document.querySelectorAll('#studentDashboard .sidebar .menu-item').forEach(item => item.classList.remove('active'));
    document.querySelectorAll('#studentDashboard .content-section').forEach(sec => sec.classList.remove('active'));
    
    // Add active to selected
    event.target.closest('.menu-item').classList.add('active');
    
    switch(section) {
        case 'venues':
            document.getElementById('studentVenuesSection').classList.add('active');
            loadStudentVenues();
            break;
        case 'newBooking':
            document.getElementById('newBookingSection').classList.add('active');
            break;
        case 'myRequests':
            document.getElementById('myRequestsSection').classList.add('active');
            loadMyRequests();
            break;
        case 'availability':
            document.getElementById('availabilitySection').classList.add('active');
            break;
    }
}

function loadStudentVenues() {
    const container = document.getElementById('studentVenuesList');
    loadVenuesGrid(container);
}

function loadVenuesList() {
    const container = document.getElementById('venuesList');
    loadVenuesGrid(container);
}

function loadVenuesGrid(container) {
    container.innerHTML = Object.values(venues).map(venue => `
        <div class="venue-card">
            <div class="venue-header">
                <div class="venue-icon">🏛️</div>
                <div class="venue-name">${venue.name}</div>
            </div>
            <div class="venue-body">
                <div class="venue-info">
                    <span class="venue-info-icon">👥</span>
                    <span><strong>Capacity:</strong> ${venue.capacity} persons</span>
                </div>
                <div class="venue-info">
                    <span class="venue-info-icon">🔧</span>
                    <span><strong>Facilities:</strong> ${venue.facilities}</span>
                </div>
                <div class="venue-info">
                    <span class="venue-info-icon">📊</span>
                    <span><strong>Bookings:</strong> ${getVenueBookingCount(venue.name)} approved</span>
                </div>
            </div>
        </div>
    `).join('');
}

function getVenueBookingCount(venueName) {
    return bookingRequests.filter(req => req.venueName === venueName && req.status === 'APPROVED').length;
}

function populateVenueSelects() {
    const venueSelect = document.getElementById('venueSelect');
    const scheduleVenue = document.getElementById('scheduleVenue');
    
    const options = Object.values(venues).map(venue => 
        `<option value="${venue.name}">${venue.name}</option>`
    ).join('');
    
    if (venueSelect) {
        venueSelect.innerHTML = '<option value="">Choose a venue...</option>' + options;
    }
    
    if (scheduleVenue) {
        scheduleVenue.innerHTML = '<option value="">Choose a venue...</option>' + options;
    }
}

function populateScheduleVenues() {
    populateVenueSelects();
}

// Booking Form Handler
function handleBookingSubmit(e) {
    e.preventDefault();
    
    const venueName = document.getElementById('venueSelect').value;
    const eventName = document.getElementById('eventName').value.trim();
    const eventDescription = document.getElementById('eventDescription').value.trim();
    const eventDate = document.getElementById('eventDate').value;
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    
    // Validate time
    if (startTime >= endTime) {
        showToast('End time must be after start time!', 'error');
        return;
    }
    
    // Check availability
    if (!checkVenueAvailable(venueName, eventDate, startTime, endTime)) {
        showToast('Sorry! The venue is not available for the selected date and time.', 'error');
        return;
    }
    
    // Create booking request
    const request = {
        requestId: requestCounter++,
        venueName: venueName,
        requesterName: currentUser.fullName,
        requesterUsername: currentUser.username,
        eventName: eventName,
        eventDescription: eventDescription,
        eventDate: eventDate,
        startTime: startTime,
        endTime: endTime,
        status: 'PENDING',
        approvedBy: '',
        remarks: ''
    };
    
    bookingRequests.push(request);
    saveData();
    
    // Reset form
    document.getElementById('bookingForm').reset();
    
    showToast(`✅ Booking request submitted successfully! Request ID: #${request.requestId}`, 'success');
}

function checkVenueAvailable(venueName, eventDate, startTime, endTime) {
    const approvedBookings = bookingRequests.filter(req => 
        req.venueName === venueName && 
        req.eventDate === eventDate && 
        req.status === 'APPROVED'
    );
    
    for (let booking of approvedBookings) {
        // Check for time overlap
        if (!(endTime <= booking.startTime || startTime >= booking.endTime)) {
            return false;
        }
    }
    
    return true;
}

function loadMyRequests() {
    const container = document.getElementById('myRequestsList');
    const myRequests = bookingRequests.filter(req => req.requesterUsername === currentUser.username);
    
    if (myRequests.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <div class="empty-state-text">No booking requests found</div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = myRequests.map(req => createRequestCard(req, false)).join('');
}

function checkAvailability() {
    const date = document.getElementById('availabilityDate').value;
    if (!date) return;
    
    const container = document.getElementById('availabilityDisplay');
    
    let html = '';
    
    Object.values(venues).forEach(venue => {
        const bookings = bookingRequests.filter(req => 
            req.venueName === venue.name && 
            req.eventDate === date && 
            req.status === 'APPROVED'
        );
        
        html += `
            <div class="availability-venue">
                <h3>🏛️ ${venue.name}</h3>
        `;
        
        if (bookings.length === 0) {
            html += '<div class="availability-free">✅ Available all day</div>';
        } else {
            bookings.forEach(booking => {
                html += `
                    <div class="availability-slot">
                        ⏰ ${booking.startTime} - ${booking.endTime} | ${booking.eventName}
                    </div>
                `;
            });
        }
        
        html += '</div>';
    });
    
    container.innerHTML = html;
}

function showVenueSchedule() {
    const venueName = document.getElementById('scheduleVenue').value;
    if (!venueName) {
        document.getElementById('scheduleDisplay').innerHTML = '';
        return;
    }
    
    const container = document.getElementById('scheduleDisplay');
    const bookings = bookingRequests.filter(req => 
        req.venueName === venueName && 
        req.status === 'APPROVED'
    ).sort((a, b) => new Date(a.eventDate) - new Date(b.eventDate));
    
    if (bookings.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📭</div>
                <div class="empty-state-text">No approved bookings for this venue</div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = bookings.map(booking => `
        <div class="schedule-item">
            <div class="schedule-time">📅 ${formatDate(booking.eventDate)} | ${booking.startTime} - ${booking.endTime}</div>
            <div class="schedule-event">📝 ${booking.eventName}</div>
            <div class="schedule-organizer">👤 Organized by: ${booking.requesterName}</div>
        </div>
    `).join('');
}

// Utility Functions
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
        weekday: 'short', 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
    });
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Data Persistence
function saveData() {
    localStorage.setItem('bookingRequests', JSON.stringify(bookingRequests));
    localStorage.setItem('requestCounter', requestCounter.toString());
}

function loadData() {
    const saved = localStorage.getItem('bookingRequests');
    if (saved) {
        bookingRequests = JSON.parse(saved);
    }
    
    const savedCounter = localStorage.getItem('requestCounter');
    if (savedCounter) {
        requestCounter = parseInt(savedCounter);
    }
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('approvalModal');
    if (event.target === modal) {
        closeModal();
    }
}
