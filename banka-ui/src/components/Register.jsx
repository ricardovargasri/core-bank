import React, { useState } from 'react';
import { authService } from '../services/api';

const Register = ({ onRegisterSuccess, onBackToLogin }) => {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        firstName: '',
        lastName: '',
        documentId: ''
    });
    const [error, setError] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setFieldErrors({});
        setLoading(true);
        try {
            await authService.register(formData);
            onRegisterSuccess();
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
            <div className="glass-card" style={{ padding: '48px', width: '100%', maxWidth: '520px' }}>
                <h2 style={{ marginBottom: '32px', textAlign: 'center', fontSize: '28px' }}>Crea tu cuenta en <span style={{ color: 'var(--primary)' }}>cory-bank</span></h2>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                            <label style={{ fontSize: '14px', color: 'var(--text-dim)' }}>Nombre</label>
                            <input name="firstName" onChange={handleChange} required style={inputStyle} />
                            {fieldErrors.firstName && (
                                <p style={{ color: 'var(--error)', fontSize: '12px' }}>{fieldErrors.firstName}</p>
                            )}
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                            <label style={{ fontSize: '14px', color: 'var(--text-dim)' }}>Apellido</label>
                            <input name="lastName" onChange={handleChange} required style={inputStyle} />
                            {fieldErrors.lastName && (
                                <p style={{ color: 'var(--error)', fontSize: '12px' }}>{fieldErrors.lastName}</p>
                            )}
                        </div>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                        <label style={{ fontSize: '14px', color: 'var(--text-dim)' }}>Email</label>
                        <input type="email" name="email" onChange={handleChange} required style={inputStyle} />
                        {fieldErrors.email && (
                            <p style={{ color: 'var(--error)', fontSize: '12px' }}>{fieldErrors.email}</p>
                        )}
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                        <label style={{ fontSize: '14px', color: 'var(--text-dim)' }}>Documento ID</label>
                        <input name="documentId" onChange={handleChange} required style={inputStyle} />
                        {fieldErrors.documentId && (
                            <p style={{ color: 'var(--error)', fontSize: '12px' }}>{fieldErrors.documentId}</p>
                        )}
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                        <label style={{ fontSize: '14px', color: 'var(--text-dim)' }}>Contraseña</label>
                        <input type="password" name="password" onChange={handleChange} required style={inputStyle} />
                        {fieldErrors.password && (
                            <p style={{ color: 'var(--error)', fontSize: '12px' }}>{fieldErrors.password}</p>
                        )}
                    </div>

                    {error && <p style={{ color: 'var(--error)', fontSize: '14px', textAlign: 'center' }}>{error}</p>}

                    <button type="submit" className="btn-primary" disabled={loading} style={{ marginTop: '16px', padding: '16px', fontSize: '16px' }}>
                        {loading ? 'Registrando...' : 'Crear Cuenta'}
                    </button>

                    <p style={{ textAlign: 'center', fontSize: '15px', color: 'var(--text-dim)', marginTop: '16px' }}>
                        ¿Ya tienes cuenta? <span onClick={onBackToLogin} style={{ color: 'var(--primary)', cursor: 'pointer', fontWeight: '600' }}>Inicia sesión</span>
                    </p>
                </form>
            </div>
        </div>
    );
};

const inputStyle = {
    padding: '10px',
    borderRadius: '8px',
    border: '1px solid var(--glass-border)',
    background: 'var(--glass)',
    color: 'white',
    outline: 'none'
};

export default Register;
