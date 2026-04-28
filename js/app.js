/**
 * Lost and Found Management System - Main JavaScript logic
 */

const API_BASE_URL = 'api';

// --- Dashboard Functions ---

function loadDashboard() {
    fetch(`${API_BASE_URL}/dashboard`)
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                console.error('Error:', data.error);
                return;
            }

            // Update Stats
            document.getElementById('totalLost').textContent = data.stats.totalLost;
            document.getElementById('totalFound').textContent = data.stats.totalFound;
            document.getElementById('totalClaimed').textContent = data.stats.totalClaimed;
            document.getElementById('totalReturned').textContent = data.stats.totalReturned;

            // Update Recent Lost Items
            const lostList = document.getElementById('recentLostList');
            lostList.innerHTML = '';
            if (data.recentLost.length === 0) {
                lostList.innerHTML = '<li class="list-group-item text-muted text-center">No recent lost items.</li>';
            } else {
                data.recentLost.forEach(item => {
                    lostList.innerHTML += createMiniItemCard(item, 'lost');
                });
            }

            // Update Recent Found Items
            const foundList = document.getElementById('recentFoundList');
            foundList.innerHTML = '';
            if (data.recentFound.length === 0) {
                foundList.innerHTML = '<li class="list-group-item text-muted text-center">No recent found items.</li>';
            } else {
                data.recentFound.forEach(item => {
                    foundList.innerHTML += createMiniItemCard(item, 'found');
                });
            }
        })
        .catch(error => console.error('Error loading dashboard:', error));
}

function createMiniItemCard(item, type) {
    const badgeClass = type === 'lost' ? 'status-Lost' : 'status-Found';
    const icon = type === 'lost' ? 'fa-search-minus' : 'fa-search-plus';
    const color = type === 'lost' ? 'danger' : 'success';
    
    return `
        <li class="list-group-item d-flex justify-content-between align-items-center">
            <div>
                <h6 class="mb-0 font-weight-bold text-${color}">
                    <i class="fas ${icon} mr-2"></i>${item.itemName}
                </h6>
                <small class="text-muted"><i class="fas fa-map-marker-alt"></i> ${item.location} | ${type === 'lost' ? item.dateLost : item.dateFound}</small>
            </div>
            <div class="text-right">
                <span class="badge badge-status ${badgeClass} mb-1">${item.status}</span><br>
            </div>
        </li>
    `;
}

// --- Report Forms Functions ---

function submitReport(event, type) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);

    const endpoint = type === 'lost' ? `${API_BASE_URL}/lost` : `${API_BASE_URL}/found`;

    fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showAlert('success', data.message);
            form.reset();
        } else {
            showAlert('danger', data.message || 'An error occurred.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showAlert('danger', 'Failed to connect to the server.');
    });
}

function showAlert(type, message) {
    const alertDiv = document.getElementById('alertArea');
    alertDiv.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
    `;
}

// --- Search Functions ---

function searchItems(event) {
    if (event) event.preventDefault();

    const keyword = document.getElementById('searchInput').value;
    const category = document.getElementById('filterCategory').value;
    const type = document.getElementById('filterType').value;

    const params = new URLSearchParams({ keyword, category, type });

    fetch(`${API_BASE_URL}/search?${params.toString()}`)
        .then(response => response.json())
        .then(data => {
            const resultsContainer = document.getElementById('searchResults');
            resultsContainer.innerHTML = '';

            let hasResults = false;

            if (data.lostItems && data.lostItems.length > 0) {
                hasResults = true;
                data.lostItems.forEach(item => {
                    resultsContainer.innerHTML += createItemCard(item, 'lost');
                });
            }

            if (data.foundItems && data.foundItems.length > 0) {
                hasResults = true;
                data.foundItems.forEach(item => {
                    resultsContainer.innerHTML += createItemCard(item, 'found');
                });
            }

            if (!hasResults) {
                resultsContainer.innerHTML = '<div class="col-12 text-center text-muted mt-5"><h4>No items found matching criteria.</h4></div>';
            }
        })
        .catch(error => console.error('Error searching:', error));
}

function createItemCard(item, type) {
    const badgeClass = type === 'lost' ? 'status-Lost' : 'status-Found';
    const borderClass = type === 'lost' ? 'border-left-danger' : 'border-left-success';
    const dateLabel = type === 'lost' ? 'Lost on' : 'Found on';
    const dateValue = type === 'lost' ? item.dateLost : item.dateFound;
    const personLabel = type === 'lost' ? 'Reporter' : 'Finder';
    const personValue = type === 'lost' ? item.reporterName : item.finderName;

    return `
        <div class="col-md-6 col-lg-4 mb-4 fade-in">
            <div class="card h-100 ${borderClass}">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h5 class="card-title font-weight-bold mb-0">${item.itemName}</h5>
                        <span class="badge badge-status ${badgeClass}">${item.status}</span>
                    </div>
                    <span class="badge badge-secondary mb-2">${item.category}</span>
                    <p class="card-text text-muted small mb-1"><i class="fas fa-calendar-alt"></i> ${dateLabel}: ${dateValue}</p>
                    <p class="card-text text-muted small mb-3"><i class="fas fa-map-marker-alt"></i> ${item.location}</p>
                    <p class="card-text">${item.description}</p>
                </div>
                <div class="card-footer bg-white border-top-0">
                    <div class="d-flex justify-content-between align-items-center">
                        <small class="text-muted">
                            <i class="fas fa-user-circle"></i> ${personValue} <br>
                            <i class="fas fa-phone"></i> ${item.contact}
                        </small>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// --- Matches Functions ---

function loadMatches() {
    fetch(`${API_BASE_URL}/match`)
        .then(response => response.json())
        .then(data => {
            const container = document.getElementById('matchesContainer');
            container.innerHTML = '';

            if (data.length === 0) {
                container.innerHTML = '<div class="col-12 text-center text-muted mt-5"><h4>No potential matches found at the moment.</h4></div>';
                return;
            }

            data.forEach(match => {
                const confidenceClass = match.confidence === 'High' ? 'success' : (match.confidence === 'Medium' ? 'warning' : 'secondary');
                
                container.innerHTML += `
                    <div class="col-12 mb-4 fade-in">
                        <div class="card border-left-${confidenceClass} shadow-sm">
                            <div class="card-header bg-white">
                                <span class="font-weight-bold text-${confidenceClass}"><i class="fas fa-star"></i> ${match.confidence} Confidence Match</span>
                                <span class="badge badge-pill badge-light">Score: ${match.matchScore}</span>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-5">
                                        <h6 class="text-danger font-weight-bold">Lost Item</h6>
                                        <p class="mb-1"><strong>${match.lostItem.itemName}</strong> (${match.lostItem.category})</p>
                                        <p class="small text-muted mb-1"><i class="fas fa-map-marker-alt"></i> ${match.lostItem.location}</p>
                                        <p class="small text-muted mb-0"><i class="fas fa-calendar"></i> ${match.lostItem.dateLost}</p>
                                    </div>
                                    <div class="col-md-2 text-center align-self-center my-3 my-md-0">
                                        <i class="fas fa-exchange-alt fa-2x text-muted"></i>
                                    </div>
                                    <div class="col-md-5">
                                        <h6 class="text-success font-weight-bold">Found Item</h6>
                                        <p class="mb-1"><strong>${match.foundItem.itemName}</strong> (${match.foundItem.category})</p>
                                        <p class="small text-muted mb-1"><i class="fas fa-map-marker-alt"></i> ${match.foundItem.location}</p>
                                        <p class="small text-muted mb-0"><i class="fas fa-calendar"></i> ${match.foundItem.dateFound}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer bg-light d-flex justify-content-center">
                                <button class="btn btn-primary btn-sm mx-2" onclick="alert('Contact Reporter: ${match.lostItem.contact}')">Contact Reporter</button>
                                <button class="btn btn-success btn-sm mx-2" onclick="alert('Contact Finder: ${match.foundItem.contact}')">Contact Finder</button>
                            </div>
                        </div>
                    </div>
                `;
            });
        })
        .catch(error => console.error('Error loading matches:', error));
}

function updateItemStatus(id, type, newStatus) {
    if(!confirm(`Are you sure you want to mark this item as ${newStatus}?`)) return;

    const params = new URLSearchParams({ id: id, type: type, status: newStatus });

    fetch(`${API_BASE_URL}/status`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            window.location.reload();
        } else {
            alert("Error: " + data.message);
        }
    })
    .catch(error => console.error('Error updating status:', error));
}


// --- My Items Functions ---

function loadMyItems() {
    fetch(`${API_BASE_URL}/myitems`)
        .then(response => {
            if (response.status === 401) {
                window.location.href = 'login.html';
                throw new Error('Unauthorized');
            }
            return response.json();
        })
        .then(data => {
            if (data.error) {
                console.error('Error:', data.error);
                return;
            }

            const lostContainer = document.getElementById('myLostItemsContainer');
            lostContainer.innerHTML = '';
            if (data.lost && data.lost.length > 0) {
                data.lost.forEach(item => {
                    lostContainer.innerHTML += createMyItemCard(item, 'lost');
                });
            } else {
                lostContainer.innerHTML = '<div class="col-12"><p class="text-muted">You have no reported lost items.</p></div>';
            }

            const foundContainer = document.getElementById('myFoundItemsContainer');
            foundContainer.innerHTML = '';
            if (data.found && data.found.length > 0) {
                data.found.forEach(item => {
                    foundContainer.innerHTML += createMyItemCard(item, 'found');
                });
            } else {
                foundContainer.innerHTML = '<div class="col-12"><p class="text-muted">You have no reported found items.</p></div>';
            }
        })
        .catch(error => console.error('Error loading my items:', error));
}

function createMyItemCard(item, type) {
    const badgeClass = type === 'lost' ? 'status-Lost' : 'status-Found';
    const borderClass = type === 'lost' ? 'border-left-danger' : 'border-left-success';
    const dateLabel = type === 'lost' ? 'Lost on' : 'Found on';
    const dateValue = type === 'lost' ? item.dateLost : item.dateFound;

    const isRequested = type === 'lost' ? item.claimRequested : item.returnRequested;
    const isCompleted = item.status === 'Claimed' || item.status === 'Returned';
    
    let actionHtml = '';
    if (isCompleted) {
        actionHtml = `<span class="badge badge-success px-3 py-2"><i class="fas fa-check-circle"></i> Already ${item.status}</span>`;
    } else if (isRequested) {
        actionHtml = '<span class="badge badge-warning px-3 py-2"><i class="fas fa-hourglass-half"></i> Request Pending Admin Approval</span>';
    } else {
        const actionStatus = type === 'lost' ? 'Claimed' : 'Returned';
        actionHtml = `<button class="btn btn-sm btn-primary" onclick="updateItemStatus(${item.id}, '${type}', '${actionStatus}')">Request item as ${actionStatus}</button>`;
    }

    return `
        <div class="col-md-6 col-lg-4 mb-4 fade-in">
            <div class="card h-100 ${borderClass}">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <h5 class="card-title font-weight-bold mb-0">${item.itemName}</h5>
                        <span class="badge badge-status ${badgeClass}">${item.status}</span>
                    </div>
                    <span class="badge badge-secondary mb-2">${item.category}</span>
                    <p class="card-text text-muted small mb-1"><i class="fas fa-calendar-alt"></i> ${dateLabel}: ${dateValue}</p>
                    <p class="card-text text-muted small mb-3"><i class="fas fa-map-marker-alt"></i> ${item.location}</p>
                </div>
                <div class="card-footer bg-light border-top-0 text-center">
                    ${actionHtml}
                </div>
            </div>
        </div>
    `;
}

// --- Authentication Functions ---
document.addEventListener('DOMContentLoaded', () => {
    checkAuthStatus();
});

function checkAuthStatus() {
    fetch('api/auth?action=session')
        .then(response => response.json())
        .then(data => {
            const authNav = document.getElementById('authNav');
            if (!authNav) return; // if page doesn't have authNav

            authNav.innerHTML = ''; // Clear existing
            
            const currentPath = window.location.pathname;
            const isProtected = ['report-lost.html', 'report-found.html', 'matches.html', 'my-items.html'].some(page => currentPath.endsWith(page));
            const isAdminPage = currentPath.endsWith('admin-dashboard.html');

            if (data.loggedIn) {
                // User is authenticated
                if (isAdminPage && data.role !== 'ADMIN') {
                    window.location.href = 'index.html'; // Kick out non-admins
                    return;
                }
                
                authNav.innerHTML = `
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle font-weight-bold text-white py-1 px-3 bg-dark rounded-pill" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <i class="fas fa-user-circle mr-1"></i>${data.fullName}
                        </a>
                        <div class="dropdown-menu dropdown-menu-right shadow border-0" aria-labelledby="navbarDropdown">
                            <a class="dropdown-item" href="my-items.html"><i class="fas fa-list mr-2 text-primary"></i>My Items</a>
                            ${data.role === 'ADMIN' && !isAdminPage ? '<a class="dropdown-item" href="admin-dashboard.html"><i class="fas fa-shield-alt mr-2 text-secondary"></i>Admin Dashboard</a>' : ''}
                            <div class="dropdown-divider"></div>
                            <a class="dropdown-item text-danger" href="api/auth?action=logout"><i class="fas fa-sign-out-alt mr-2"></i>Logout</a>
                        </div>
                    </li>
                `;
                
                // Auto-fill contact details if on a report page
                const reporterInput = document.querySelector('input[name="reporterName"]');
                const finderInput = document.querySelector('input[name="finderName"]');
                const contactInput = document.querySelector('input[name="contact"]');
                
                if (reporterInput) {
                    reporterInput.value = data.fullName;
                    reporterInput.readOnly = true;
                }
                if (finderInput) {
                    finderInput.value = data.fullName;
                    finderInput.readOnly = true;
                }
                if (contactInput) {
                    contactInput.value = data.email;
                    contactInput.readOnly = true;
                }

            } else {
                // Not authenticated
                if (isProtected || isAdminPage) {
                    window.location.href = 'login.html'; // Enforce login
                    return;
                }

                authNav.innerHTML = `
                    <li class="nav-item ml-2">
                        <a class="btn btn-outline-light btn-sm font-weight-bold rounded-pill px-3 py-2" href="login.html">
                            Login / Register
                        </a>
                    </li>
                `;
            }
        })
        .catch(error => console.error('Error checking session:', error));
}
