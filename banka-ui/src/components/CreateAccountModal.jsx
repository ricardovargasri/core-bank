import React, { useState } from 'react';
import { bankService } from '../services/api';

const CreateAccountModal = ({ isOpen, onClose, onAccountCreated }) => {
    const [type, setType] = useState('SAVINGS');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    if (!isOpen) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            await bankService.createAccount(type);
            onAccountCreated();
            onClose();
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={overlayStyle}>
            <div className="glass-card" style={modalStyle}>
                <h2 style={{ marginBottom: '24px', fontSize: '24px' }}>Abrir Nueva Cuenta</h2>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        <label style={{ color: 'var(--text-dim)', fontSize: '14px' }}>Tipo de Cuenta</label>
                        <select
                            value={type}
                            onChange={(e) => setType(e.target.value)}
                            style={selectStyle}
                        >
                            <option value="SAVINGS">Cuenta de Ahorros</option>
                            <option value="CHECKING">Cuenta Corriente</option>
                        </select>
                    </div>

                    <div style={infoBoxStyle}>
                        <p style={{ fontSize: '12px', color: 'rgba(255,255,255,0.5)' }}>
                            {type === 'SAVINGS'
                                ? 'ℹ️ Nota: Debes esperar 5 días entre creaciones de cuentas de ahorro.'
                                : 'ℹ️ Nota: Debes esperar 24 horas entre creaciones de cuentas corrientes.'
                            }
                        </p>
                    </div>

                    {error && <p style={{ color: 'var(--error)', fontSize: '14px', textAlign: 'center' }}>{error}</p>}

                    <div style={{ display: 'flex', gap: '15px', marginTop: '10px' }}>
                        <button
                            type="button"
                            onClick={onClose}
                            style={{ flex: 1, padding: '14px', borderRadius: '12px', border: '1px solid var(--glass-border)', background: 'transparent', color: 'white', cursor: 'pointer' }}
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            className="btn-primary"
                            disabled={loading}
                            style={{ flex: 1 }}
                        >
                            {loading ? 'Creando...' : 'Confirmar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

const overlayStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
    backdropFilter: 'blur(8px)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000
};

const modalStyle = {
    padding: '40px',
    width: '100%',
    maxWidth: '450px'
};

const selectStyle = {
    padding: '14px',
    fontSize: '16px',
    borderRadius: '12px',
    border: '1px solid var(--glass-border)',
    background: 'var(--bg-sidebar)',
    color: 'white',
    outline: 'none',
    cursor: 'pointer'
};

const infoBoxStyle = {
    background: 'rgba(99, 102, 241, 0.1)',
    padding: '12px',
    borderRadius: '8px',
    borderLeft: '4px solid var(--primary)'
};

export default CreateAccountModal;
