import React, { useEffect, useState } from 'react';
import { bankService } from '../services/api';
import TransactionModal from './TransactionModal';
import CreateAccountModal from './CreateAccountModal';

const Dashboard = ({ onLogout }) => {
    const [accounts, setAccounts] = useState([]);
    const [activeAccountIdx, setActiveAccountIdx] = useState(0);
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [modalConfig, setModalConfig] = useState({ isOpen: false, type: 'deposit' });
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [showBalance, setShowBalance] = useState(true);

    const fetchData = async () => {
        try {
            const userData = JSON.parse(localStorage.getItem('banka_user'));
            setUser(userData);
            const data = await bankService.getMyAccounts();
            setAccounts(data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleOpenModal = (type) => {
        setModalConfig({ isOpen: true, type });
    };

    const handleTransactionSuccess = () => {
        fetchData(); // Refresh balances
    };

    const activeAccount = accounts[activeAccountIdx];

    // SVG Icons
    const EyeIcon = () => (
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{ opacity: 0.8 }}><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" /><circle cx="12" cy="12" r="3" /></svg>
    );
    const EyeOffIcon = () => (
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{ opacity: 0.8 }}><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" /><line x1="1" y1="1" x2="23" y2="23" /></svg>
    );

    return (
        <div className="dashboard-container">
            {/* Sidebar */}
            <aside className="sidebar">
                <div style={{ marginBottom: '40px', padding: '0 10px' }}>
                    <h2 style={{ color: 'var(--primary)', letterSpacing: '2px' }}>CORY-BANK</h2>
                </div>

                <div style={{ flex: 1, overflowY: 'auto' }}>
                    <p style={{ fontSize: '12px', color: 'var(--text-dim)', textTransform: 'uppercase', marginBottom: '20px', padding: '0 10px' }}>Tus Cuentas</p>

                    {accounts.map((acc, idx) => (
                        <div
                            key={acc.id}
                            className={`nav-item ${idx === activeAccountIdx ? 'active' : ''}`}
                            onClick={() => setActiveAccountIdx(idx)}
                        >
                            <div style={{ display: 'flex', flexDirection: 'column' }}>
                                <span style={{ fontWeight: '600' }}>#{acc.accountNumber}</span>
                                <span style={{ fontSize: '11px', opacity: 0.7 }}>{acc.type === 'SAVINGS' ? 'Ahorros' : 'Corriente'}</span>
                            </div>
                            <div style={{ marginLeft: 'auto', fontWeight: '600' }}>
                                {showBalance ? `$${acc.balance.toLocaleString('en-US', { minimumFractionDigits: 0 })}` : '****'}
                            </div>
                        </div>
                    ))}

                    <button
                        onClick={() => setIsCreateModalOpen(true)}
                        style={{ width: '100%', marginTop: '20px', background: 'var(--glass)', border: '1px dashed var(--glass-border)', color: 'white', padding: '12px', borderRadius: '12px', cursor: 'pointer' }}
                    >
                        + Abrir Nueva Cuenta
                    </button>
                </div>

                <div className="nav-item" onClick={onLogout} style={{ marginTop: 'auto', borderTop: '1px solid var(--glass-border)', paddingTop: '20px' }}>
                    <span>Cerrar Sesión</span>
                </div>
            </aside>

            {/* Main Content */}
            <main className="main-content">
                <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '40px', width: '100%', maxWidth: '1000px' }}>
                    <div>
                        <h1 style={{ fontSize: '32px' }}>Hola, <span style={{ color: 'var(--primary)' }}>{user?.firstName || user?.email?.split('@')[0] || 'Usuario'}</span>! 👋</h1>
                        <p style={{ color: 'var(--text-dim)' }}>Gestiona tu dinero con seguridad y estilo.</p>
                    </div>
                </header>

                {loading ? (
                    <p>Cargando información...</p>
                ) : activeAccount ? (
                    <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1fr', gap: '30px', width: '100%', maxWidth: '1000px' }}>
                        {/* Account Highlight Card */}
                        <div className="glass-card" style={{
                            background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.3), rgba(168, 85, 247, 0.3))',
                            position: 'relative',
                            overflow: 'hidden'
                        }}>
                            <div style={{ position: 'relative', zIndex: 1 }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                                    <p style={{ color: 'var(--text-dim)', fontSize: '14px' }}>
                                        Saldo Disponible - {activeAccount.type === 'SAVINGS' ? 'Cuenta de Ahorros' : 'Cuenta Corriente'}
                                    </p>
                                    <button
                                        onClick={() => setShowBalance(!showBalance)}
                                        style={{ background: 'rgba(255,255,255,0.1)', border: 'none', color: 'white', cursor: 'pointer', padding: '8px', borderRadius: '10px', display: 'flex', alignItems: 'center' }}
                                    >
                                        {showBalance ? <EyeIcon /> : <EyeOffIcon />}
                                    </button>
                                </div>
                                <h2 style={{ fontSize: '56px', fontWeight: '800', marginBottom: '30px' }}>
                                    {showBalance ? `$${activeAccount.balance.toLocaleString('en-US', { minimumFractionDigits: 2 })}` : '••••••••'}
                                </h2>
                                <div style={{ fontSize: '18px', letterSpacing: '4px', opacity: 0.8 }}>
                                    {activeAccount.accountNumber} <span style={{ fontSize: '12px', verticalSpacing: 'middle' }}>● ● ● ●</span>
                                </div>
                            </div>
                            <div style={cardBlurDecorationStyle}></div>
                        </div>

                        {/* Quick Actions Panel */}
                        <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', gap: '20px', justifyContent: 'center' }}>
                            <h3 style={{ fontSize: '18px', marginBottom: '10px' }}>Acciones Rápidas</h3>
                            <button className="btn-primary" onClick={() => handleOpenModal('transfer')} style={{ padding: '20px', fontSize: '16px' }}>
                                Nueva Transferencia
                            </button>
                            <button className="btn-primary" onClick={() => handleOpenModal('deposit')} style={{ padding: '20px', fontSize: '16px', background: 'var(--accent)', boxShadow: '0 4px 15px rgba(59, 130, 246, 0.4)' }}>
                                Hacer Depósito
                            </button>
                        </div>

                        {/* Transactions Section */}
                        <div className="glass-card" style={{ gridColumn: 'span 2' }}>
                            <h3 style={{ marginBottom: '24px' }}>Movimientos Recientes</h3>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                                <p style={{ color: 'var(--text-dim)', textAlign: 'center', padding: '40px' }}>
                                    Aún no tienes movimientos registrados en esta cuenta.
                                </p>
                            </div>
                        </div>
                    </div>
                ) : (
                    <div className="glass-card" style={{ textAlign: 'center', padding: '60px' }}>
                        <p>No tienes cuentas activas. Crea una para empezar.</p>
                    </div>
                )}
            </main>

            <TransactionModal
                type={modalConfig.type}
                isOpen={modalConfig.isOpen}
                onClose={() => setModalConfig({ ...modalConfig, isOpen: false })}
                accountNumber={activeAccount?.accountNumber}
                onTransactionSuccess={handleTransactionSuccess}
            />

            <CreateAccountModal
                isOpen={isCreateModalOpen}
                onClose={() => setIsCreateModalOpen(false)}
                onAccountCreated={fetchData}
            />
        </div>
    );
};

const cardBlurDecorationStyle = {
    position: 'absolute',
    top: '-50px',
    right: '-50px',
    width: '200px',
    height: '200px',
    background: 'white',
    filter: 'blur(100px)',
    opacity: 0.2,
    zIndex: 0
};

export default Dashboard;
