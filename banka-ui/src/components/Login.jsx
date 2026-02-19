import React, { useState } from 'react';
import { authService } from '../services/api';

const Login = ({ onLoginSuccess, onGoToRegister }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setFieldErrors({});
        setLoading(true);
        try {
            await authService.login(email, password);
            onLoginSuccess();
        } catch (err) {
            if (err?.errors && Array.isArray(err.errors)) {
                const nextFieldErrors = {};
                err.errors.forEach((e) => {
                    if (e?.field) {
                        nextFieldErrors[e.field] = e.message;
                    }
                });
                setFieldErrors(nextFieldErrors);
                setError(err.message);
            } else {
                setError(err.message);
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="glass-card" style={{ padding: '48px', width: '100%', maxWidth: '480px' }}>
                <h2 style={{ marginBottom: '32px', textAlign: 'center', fontSize: '28px' }}>Bienvenido a <span style={{ color: 'var(--primary)' }}>cory-bank</span></h2>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        <label style={{ fontSize: '14px', color: 'var(--text-dim)' }}>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="ejemplo@banka.com"
                            required
                            style={{ padding: '14px', fontSize: '16px', borderRadius: '12px', border: '1px solid var(--glass-border)', background: 'var(--glass)', color: 'white', outline: 'none' }}
                        />
                        {fieldErrors.email && (
                            <p style={{ color: 'var(--error)', fontSize: '12px' }}>{fieldErrors.email}</p>
                        )}
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        <label style={{ fontSize: '14px', color: 'var(--text-dim)' }}>Contraseña</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            required
                            style={{ padding: '14px', fontSize: '16px', borderRadius: '12px', border: '1px solid var(--glass-border)', background: 'var(--glass)', color: 'white', outline: 'none' }}
                        />
                        {fieldErrors.password && (
                            <p style={{ color: 'var(--error)', fontSize: '12px' }}>{fieldErrors.password}</p>
                        )}
                    </div>

                    {error && <p style={{ color: 'var(--error)', fontSize: '14px', textAlign: 'center' }}>{error}</p>}

                    <button type="submit" className="btn-primary" disabled={loading} style={{ marginTop: '16px', padding: '16px', fontSize: '16px' }}>
                        {loading ? 'Entrando...' : 'Iniciar Sesión'}
                    </button>

                    <p style={{ textAlign: 'center', fontSize: '15px', color: 'var(--text-dim)', marginTop: '16px' }}>
                        ¿No tienes cuenta? <span onClick={onGoToRegister} style={{ color: 'var(--primary)', cursor: 'pointer', fontWeight: '600' }}>Regístrate</span>
                    </p>
                </form>
            </div>
        </div>
    );
};

export default Login;
