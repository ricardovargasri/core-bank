import React from 'react';
import keycloak from '../keycloak';

const Landing = () => {
    const handleLogin = () => {
        keycloak.login();
    };

    const handleRegister = () => {
        keycloak.register();
    };

    return (
        <div className="landing-container" style={{
            height: '100vh',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            background: 'radial-gradient(circle at top right, #2a2a4a, #0f0f1a)',
            color: 'white',
            textAlign: 'center',
            padding: '20px'
        }}>
            <header style={{ marginBottom: '50px' }}>
                <h1 style={{ fontSize: '4rem', marginBottom: '10px', fontWeight: '800', letterSpacing: '-1px' }}>
                    Core<span style={{ color: 'var(--primary)' }}>Bank</span>
                </h1>
                <p style={{ fontSize: '1.2rem', color: 'var(--text-dim)', maxWidth: '500px' }}>
                    La banca digital del futuro, hoy. Segura, rápida y con un diseño que te encantará.
                </p>
            </header>

            <div className="glass-card" style={{
                padding: '40px',
                display: 'flex',
                gap: '20px',
                flexDirection: 'column',
                width: '100%',
                maxWidth: '400px'
            }}>
                <button 
                    className="btn-primary" 
                    onClick={handleLogin}
                    style={{ padding: '18px', fontSize: '18px' }}
                >
                    Iniciar Sesión
                </button>
                
                <button 
                    className="btn-primary" 
                    onClick={handleRegister}
                    style={{ 
                        padding: '18px', 
                        fontSize: '18px',
                        background: 'transparent',
                        border: '2px solid var(--primary)',
                        color: 'var(--primary)'
                    }}
                >
                    Registrarme Gratis
                </button>
            </div>

            <footer style={{ marginTop: '50px', color: 'var(--text-dim)', fontSize: '0.9rem' }}>
                &copy; 2026 Core Bank Digital. Todos los derechos reservados.
            </footer>
        </div>
    );
};

export default Landing;
