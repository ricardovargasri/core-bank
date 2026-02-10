import React, { useState } from 'react';
import { bankService } from '../services/api';

const TransactionModal = ({ type, isOpen, onClose, accountNumber, onTransactionSuccess }) => {
    const [amount, setAmount] = useState('');
    const [destinationAccount, setDestinationAccount] = useState('');
    const [description, setDescription] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    if (!isOpen) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            if (type === 'deposit') {
                await bankService.deposit({
                    accountNumber: accountNumber,
                    amount: parseFloat(amount),
                    description: description
                });
            } else {
                await bankService.transfer({
                    sourceAccountNumber: accountNumber,
                    destinationAccountNumber: destinationAccount,
                    amount: parseFloat(amount),
                    description: description
                });
            }
            onTransactionSuccess();
            onClose();
            setAmount('');
            setDestinationAccount('');
            setDescription('');
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={overlayStyle}>
            <div className="glass-card" style={modalStyle}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                    <h2 style={{ fontSize: '24px' }}>{type === 'deposit' ? 'Hacer Depósito' : 'Nueva Transferencia'}</h2>
                    <button onClick={onClose} style={closeButtonStyle}>×</button>
                </div>

                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    <div style={inputGroupStyle}>
                        <label style={labelStyle}>Cuenta de Origen</label>
                        <div style={readOnlyInputStyle}>{accountNumber}</div>
                    </div>

                    {type === 'transfer' && (
                        <div style={inputGroupStyle}>
                            <label style={labelStyle}>Cuenta de Destino</label>
                            <input
                                type="text"
                                value={destinationAccount}
                                onChange={(e) => setDestinationAccount(e.target.value)}
                                placeholder="Número de cuenta"
                                required
                                style={inputStyle}
                            />
                        </div>
                    )}

                    <div style={inputGroupStyle}>
                        <label style={labelStyle}>Monto ($)</label>
                        <input
                            type="number"
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            placeholder="0.00"
                            required
                            min="0.01"
                            step="0.01"
                            style={inputStyle}
                        />
                    </div>

                    <div style={inputGroupStyle}>
                        <label style={labelStyle}>Descripción (Opcional)</label>
                        <textarea
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            placeholder="Ej: Pago de servicios, Regalo, etc."
                            style={{ ...inputStyle, minHeight: '80px', resize: 'none' }}
                        />
                    </div>

                    {error && <p style={{ color: 'var(--error)', fontSize: '14px', textAlign: 'center' }}>{error}</p>}

                    <button type="submit" className="btn-primary" disabled={loading} style={{ marginTop: '10px', padding: '16px' }}>
                        {loading ? 'Procesando...' : 'Confirmar'}
                    </button>
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
    backdropFilter: 'blur(5px)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000
};

const modalStyle = {
    padding: '40px',
    width: '100%',
    maxWidth: '450px',
    position: 'relative'
};

const closeButtonStyle = {
    background: 'none',
    border: 'none',
    color: 'white',
    fontSize: '32px',
    cursor: 'pointer',
    lineHeight: '1'
};

const inputGroupStyle = {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px'
};

const labelStyle = {
    fontSize: '14px',
    color: 'var(--text-dim)'
};

const inputStyle = {
    padding: '14px',
    fontSize: '16px',
    borderRadius: '12px',
    border: '1px solid var(--glass-border)',
    background: 'var(--glass)',
    color: 'white',
    outline: 'none'
};

const readOnlyInputStyle = {
    ...inputStyle,
    opacity: 0.7,
    backgroundColor: 'rgba(255, 255, 255, 0.02)'
};

export default TransactionModal;
