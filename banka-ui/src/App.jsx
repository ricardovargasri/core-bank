import { useState, useEffect } from 'react'
import './App.css'
import keycloak from './keycloak'
import Dashboard from './components/Dashboard'
import AdminDashboard from './components/AdminDashboard'
import Landing from './components/Landing'

function App() {
  const [authenticated, setAuthenticated] = useState(false)
  const [init, setInit] = useState(false)
  const [role, setRole] = useState(null)
  const [view, setView] = useState('dashboard') // Default view

  useEffect(() => {
    keycloak.init({
      onLoad: 'check-sso',
      checkLoginIframe: false,
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
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
    return <Landing />
  }

  return (
    <div className="App">
      {view === 'dashboard' && (
        <Dashboard
          onLogout={handleLogout}
          role={role}
          userInfo={keycloak.tokenParsed}
          onGoToAdmin={() => setView('admin-dashboard')}
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
