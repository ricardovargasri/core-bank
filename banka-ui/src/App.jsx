import { useState, useEffect } from 'react'
import './App.css'
import Login from './components/Login'
import Register from './components/Register'
import Dashboard from './components/Dashboard'
import { authService } from './services/api'

function App() {
  const [view, setView] = useState('login') // 'login', 'register', 'dashboard'

  useEffect(() => {
    if (authService.isAuthenticated()) {
      setView('dashboard')
    }
  }, [])

  const handleAuthSuccess = () => {
    setView('dashboard')
  }

  const handleLogout = () => {
    authService.logout()
    setView('login')
  }

  return (
    <div className="App">
      {view === 'dashboard' && <Dashboard onLogout={handleLogout} />}
      {view === 'login' && (
        <Login
          onLoginSuccess={handleAuthSuccess}
          onGoToRegister={() => setView('register')}
        />
      )}
      {view === 'register' && (
        <Register
          onRegisterSuccess={handleAuthSuccess}
          onBackToLogin={() => setView('login')}
        />
      )}
    </div>
  )
}

export default App
