import keycloak from '../keycloak';

const API_URL = 'http://localhost:8080/api/v1';

async function buildApiError(response) {
    let errorBody = null;
    try {
        errorBody = await response.json();
    } catch (e) {
        errorBody = null;
    }

    const message = errorBody?.message || `Request failed with status ${response.status}`;
    const err = new Error(message);
    err.status = errorBody?.status ?? response.status;
    err.errors = errorBody?.errors;
    err.raw = errorBody;
    return err;
}

export const authService = {
    logout() {
        keycloak.logout();
    },

    getToken() {
        return keycloak.token;
    },

    isAuthenticated() {
        return !!keycloak.token;
    }
};

export const bankService = {
    async getMyAccounts() {
        const response = await fetch(`${API_URL}/accounts/me`, {
            headers: {
                'Authorization': `Bearer ${authService.getToken()}`,
            },
        });
        if (!response.ok) throw new Error('Could not fetch accounts');
        return response.json();
    },

    async getAllAccounts() {
        const response = await fetch(`${API_URL}/admin/accounts`, {
            headers: {
                'Authorization': `Bearer ${authService.getToken()}`,
            },
        });
        if (!response.ok) throw new Error('Could not fetch accounts');
        return response.json();
    },

    async getAdminTransactions(accountNumber) {
        const response = await fetch(`${API_URL}/admin/accounts/${accountNumber}/transactions`, {
            headers: {
                'Authorization': `Bearer ${authService.getToken()}`,
            },
        });
        if (!response.ok) {
            if (response.status === 404) return [];
            throw new Error('Could not fetch admin transactions');
        }
        const data = await response.json();
        return Array.isArray(data) ? data : (data.content || []);
    },

    async adminDeposit(data) {
        const response = await fetch(`${API_URL}/admin/deposit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authService.getToken()}`,
            },
            body: JSON.stringify(data),
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Deposit failed');
        }
        return response.json();
    },

    async deposit(depositData) {
        const response = await fetch(`${API_URL}/transactions/deposit`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authService.getToken()}`,
            },
            body: JSON.stringify(depositData),
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Deposit failed');
        }
        return response.json();
    },

    async transfer(transferData) {
        const response = await fetch(`${API_URL}/transactions/transfer`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authService.getToken()}`,
            },
            body: JSON.stringify(transferData),
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Transfer failed');
        }
        return response.json();
    },

    async createAccount(type) {
        const response = await fetch(`${API_URL}/accounts/me`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authService.getToken()}`,
            },
            body: JSON.stringify({ type }),
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Error creating account');
        }
        return response.json();
    },

    async getTransactions(accountNumber, page = 0, size = 50) {
        const response = await fetch(`${API_URL}/transactions/history/${accountNumber}?page=${page}&size=${size}`, {
            headers: {
                'Authorization': `Bearer ${authService.getToken()}`,
            },
        });
        if (!response.ok) {
            // Return empty array instead of throwing — no transactions is not an error
            if (response.status === 404) return [];
            throw new Error('Could not fetch transactions');
        }
        const data = await response.json();
        // Backend returns a Page object with .content array
        return data.content || data || [];
    },

    async deactivateAccount(accountId) {
        const response = await fetch(`${API_URL}/admin/accounts/${accountId}/deactivate`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${authService.getToken()}`,
            },
        });
        if (!response.ok) throw new Error('Could not deactivate account');
        return true;
    },

    async activateAccount(accountId) {
        const response = await fetch(`${API_URL}/admin/accounts/${accountId}/activate`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${authService.getToken()}`,
            },
        });
        if (!response.ok) throw new Error('Could not activate account');
        return true;
    }
};
