import React, { useEffect, useState } from 'react';
import { bankService } from '../services/api';
import TransactionTable from './TransactionTable';

const AdminDashboard = ({ onLogout, onGoToPersonal }) => {
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedAccount, setSelectedAccount] = useState(null);
    const [showHistory, setShowHistory] = useState(false);
    const [transactions, setTransactions] = useState([]);
    const [depositAmount, setDepositAmount] = useState('');
    const [depositMode, setDepositMode] = useState(false);

    const fetchData = async () => {
        try {
            const data = await bankService.getAllAccounts();
            setAccounts(data);
        } catch (err) {
            console.error("Error fetching all accounts:", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleViewHistory = async (account) => {
        try {
            setSelectedAccount(account);
            const txs = await bankService.getTransactions(account.accountNumber);
            setTransactions(txs);
            setShowHistory(true);
            setDepositMode(false);
        } catch (error) {
            console.error("Error fetching history:", error);
            alert("No se pudo cargar el historial.");
        }
    };

    const handlePrepareDeposit = (account) => {
        setSelectedAccount(account);
        setDepositMode(true);
        setShowHistory(false);
        setDepositAmount('');
    };

    const executeDeposit = async (e) => {
        e.preventDefault();
        try {
            await bankService.adminDeposit({
                accountNumber: selectedAccount.accountNumber,
                amount: parseFloat(depositAmount)
            });
            alert("Depósito realizado con éxito");
            setDepositMode(false);
            fetchData(); // Refresh balances
        } catch (error) {
            alert(error.message);
        }
    };

    const handleToggleStatus = async (account) => {
        const action = account.active ? 'desactivar' : 'activar';
        if (!window.confirm(`¿Seguro que deseas ${action} esta cuenta?`)) return;

        try {
            if (account.active) {
                await bankService.deactivateAccount(account.id);
            } else {
                await bankService.activateAccount(account.id);
            }
            alert(`Cuenta ${action}da correctamente.`);
            fetchData(); // Refresh list
        } catch (error) {
            console.error(error);
            alert(`Error al ${action} la cuenta.`);
        }
    }

    const filteredAccounts = accounts.map(acc => ({
        ...acc,
        active: acc.active !== undefined ? acc.active : true // Default to true if missing
    })).filter(acc =>
        acc.accountNumber.includes(searchTerm) ||
        acc.ownerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        acc.ownerEmail.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="dashboard-container">
            <aside className="sidebar">
                <div style={{ marginBottom: '40px', padding: '0 10px' }}>
                    <h2 style={{ color: 'var(--primary)', letterSpacing: '2px' }}>ADMIN PANEL</h2>
                </div>

                <div className="nav-item active">
                    <span>Todas las Cuentas</span>
                </div>

                <div className="nav-item" onClick={onGoToPersonal} style={{ marginTop: '20px' }}>
                    <span>Mi Dashboard Personal</span>
                </div>

                <div className="nav-item" onClick={onLogout} style={{ marginTop: 'auto', borderTop: '1px solid var(--glass-border)', paddingTop: '20px' }}>
                    <span>Cerrar Sesión</span>
                </div>
            </aside>

            <main className="main-content">
                <header style={{ marginBottom: '40px' }}>
                    <h1 style={{ fontSize: '32px' }}>Gestión Global de <span style={{ color: 'var(--primary)' }}>Cuentas</span></h1>
                    <p style={{ color: 'var(--text-dim)' }}>Panel de control para administradores y cajeros.</p>
                </header>

                <div className="glass-card" style={{ marginBottom: '30px', display: 'flex', gap: '20px' }}>
                    <input
                        type="text"
                        placeholder="Buscar por número de cuenta, nombre o email..."
                        className="input-field"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={{ background: 'rgba(255,255,255,0.05)', marginBottom: '0', flex: 1 }}
                    />
                </div>

                {/* MODAL / PANEL DEPOSITO */}
                {depositMode && selectedAccount && (
                    <div className="glass-card" style={{ marginBottom: '30px', border: '1px solid var(--accent)' }}>
                        <h3>Depositar a {selectedAccount.ownerName} (#{selectedAccount.accountNumber})</h3>
                        <form onSubmit={executeDeposit} style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
                            <input
                                type="text"
                                inputMode="decimal"
                                pattern="[0-9]*\.?[0-9]*"
                                placeholder="Monto (ej: 50.00)"
                                value={depositAmount}
                                onKeyDown={(e) => {
                                    // Permitir: borrar, tab, escape, enter, decimal point
                                    if (
                                        ['Backspace', 'Tab', 'Escape', 'Enter', '.', 'ArrowLeft', 'ArrowRight'].includes(e.key) ||
                                        // Permitir: Ctrl+A, Ctrl+C, Ctrl+V, Ctrl+X
                                        (e.ctrlKey === true || e.metaKey === true)
                                    ) {
                                        return;
                                    }
                                    // Bloquear si no es número
                                    if (!/^[0-9]$/.test(e.key)) {
                                        e.preventDefault();
                                    }
                                }}
                                onChange={e => {
                                    const val = e.target.value;
                                    // Validar formato decimal estricto para evitar múltiples puntos
                                    if (val === '' || /^\d*\.?\d*$/.test(val)) {
                                        setDepositAmount(val);
                                    }
                                }}
                                className="input-field"
                                style={{ marginBottom: 0 }}
                                required
                            />
                            <button type="submit" className="btn-primary">Confirmar Depósito</button>
                            <button type="button" className="btn-primary" style={{ background: 'var(--text-dim)' }} onClick={() => setDepositMode(false)}>Cancelar</button>
                        </form>
                    </div>
                )}

                {/* HISTORIAL PANEL */}
                {showHistory && selectedAccount && (
                    <div className="glass-card" style={{ marginBottom: '30px', border: '1px solid var(--primary)', position: 'relative' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                            <h3 style={{ margin: 0 }}>Historial: {selectedAccount.ownerName} (#{selectedAccount.accountNumber})</h3>
                            <button
                                onClick={() => setShowHistory(false)}
                                style={{
                                    background: 'rgba(255,255,255,0.1)',
                                    border: 'none',
                                    color: 'white',
                                    cursor: 'pointer',
                                    padding: '5px 10px',
                                    borderRadius: '5px'
                                }}
                            >
                                ✖ Cerrar Historial
                            </button>
                        </div>
                        <TransactionTable transactions={transactions} />
                    </div>
                )}

                {loading ? (
                    <p>Cargando todas las cuentas...</p>
                ) : (
                    <div className="glass-card" style={{ padding: '0', overflow: 'hidden' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                            <thead>
                                <tr style={{ background: 'rgba(255,255,255,0.05)' }}>
                                    <th style={thStyle}>Dueño</th>
                                    <th style={thStyle}>Nro Cuenta</th>
                                    <th style={thStyle}>Tipo</th>
                                    <th style={thStyle}>Saldo</th>
                                    <th style={thStyle}>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredAccounts.map(acc => (
                                    <tr key={acc.id} style={{ borderBottom: '1px solid var(--glass-border)' }}>
                                        <td style={tdStyle}>
                                            <div style={{ fontWeight: '600' }}>{acc.ownerName}</div>
                                            <div style={{ fontSize: '11px', opacity: 0.6 }}>{acc.ownerEmail}</div>
                                        </td>
                                        <td style={tdStyle}>#{acc.accountNumber}</td>
                                        <td style={tdStyle}>{acc.type === 'SAVINGS' ? 'Ahorros' : 'Corriente'}</td>
                                        <td style={tdStyle}>${acc.balance.toLocaleString('en-US')}</td>
                                        <td style={tdStyle}>
                                            <div style={{ display: 'flex', gap: '8px' }}>
                                                <button
                                                    className="btn-primary"
                                                    style={{ padding: '8px 12px', fontSize: '11px', background: 'var(--accent)' }}
                                                    onClick={() => handlePrepareDeposit(acc)}
                                                >
                                                    Depositar
                                                </button>
                                                <button
                                                    className="btn-primary"
                                                    style={{ padding: '8px 12px', fontSize: '11px', background: 'var(--primary)' }}
                                                    onClick={() => handleViewHistory(acc)}
                                                >
                                                    Historial
                                                </button>
                                                <button
                                                    className="btn-primary"
                                                    style={{
                                                        padding: '8px 12px',
                                                        fontSize: '11px',
                                                        background: acc.active ? 'var(--error)' : 'var(--success)'
                                                    }}
                                                    onClick={() => handleToggleStatus(acc)}
                                                >
                                                    {acc.active ? 'Bloquear' : 'Desbloquear'}
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        {filteredAccounts.length === 0 && (
                            <p style={{ textAlign: 'center', padding: '40px', color: 'var(--text-dim)' }}>No se encontraron cuentas.</p>
                        )}
                    </div>
                )}
            </main>
        </div>
    );
};

const thStyle = { padding: '20px', fontSize: '12px', textTransform: 'uppercase', color: 'var(--text-dim)', fontWeight: '600' };
const tdStyle = { padding: '20px', verticalAlign: 'middle' };

export default AdminDashboard;
