import { useState, useEffect } from 'react'
import './App.css'
import keycloak from './keycloak'
import Dashboard from './components/Dashboard'
import AdminDashboard from './components/AdminDashboard'

function App() {
  const [authenticated, setAuthenticated] = useState(false)
  const [init, setInit] = useState(false)
  const [role, setRole] = useState(null)
  const [view, setView] = useState('dashboard') // Default view

  useEffect(() => {
    keycloak.init({
      onLoad: 'login-required',
      checkLoginIframe: false
    }).then((auth) => {
      setAuthenticated(auth)
      setInit(true)
      if (auth) {
        // Extract role from Keycloak Token (BankaRealm roles)
        const isAdmin = keycloak.hasRealmRole('ADMIN')
        setRole(isAdmin ? 'ADMIN' : 'USER')
        setView(isAdmin ? 'admin-dashboard' : 'dashboard')

        // Optional: Save minimal info for legacy components if needed
        localStorage.setItem('banka_token', keycloak.token)
      }
    }).catch(err => {
      console.error("Keycloak Init Error", err)
      setInit(true)
    })
  }, [])

  const handleLogout = () => {
    keycloak.logout()
  }

  if (!init) {
    return <div className="loading-screen">Cargando Banco...</div>
  }

  if (!authenticated) {
    return <div className="error-screen">No se pudo autenticar con el servidor de identidad.</div>
  }

  return (
    <div className="App">
      {view === 'dashboard' && (
        <Dashboard
          onLogout={handleLogout}
          role={role}
          userInfo={keycloak.tokenParsed}
        />
      )}
      {view === 'admin-dashboard' && (
        <AdminDashboard
          onLogout={handleLogout}
          onGoToPersonal={() => setView('dashboard')}
        />
      )}
    </div>
  )
}

export default App
