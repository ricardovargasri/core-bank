const API_URL = 'http://localhost:8080/api/v1';

export const authService = {
    async login(email, password) {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Login failed. Please check your credentials.');
        }

        const data = await response.json();
        if (data.token) {
            localStorage.setItem('banka_token', data.token);
            localStorage.setItem('banka_user', JSON.stringify(data));
        }
        return data;
    },

    async register(userData) {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Registration failed.');
        }

        const data = await response.json();
        if (data.token) {
            localStorage.setItem('banka_token', data.token);
            localStorage.setItem('banka_user', JSON.stringify(data));
        }
        return data;
    },

    logout() {
        localStorage.removeItem('banka_token');
        localStorage.removeItem('banka_user');
    },

    getToken() {
        return localStorage.getItem('banka_token');
    },

    isAuthenticated() {
        return !!this.getToken();
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

    async getTransactions(accountNumber) {
        const response = await fetch(`${API_URL}/transactions/account/${accountNumber}`, {
            headers: {
                'Authorization': `Bearer ${authService.getToken()}`,
            },
        });
        if (!response.ok) throw new Error('Could not fetch transactions');
        return response.json();
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
