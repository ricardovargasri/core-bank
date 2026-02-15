import { useState, useEffect } from 'react'
import './App.css'
import Login from './components/Login'
import Register from './components/Register'
import Dashboard from './components/Dashboard'
import AdminDashboard from './components/AdminDashboard'
import { authService } from './services/api'

function App() {
  const [view, setView] = useState('login') // 'login', 'register', 'dashboard', 'admin-dashboard'
  const [user, setUser] = useState(null)

  useEffect(() => {
    if (authService.isAuthenticated()) {
      const savedUser = JSON.parse(localStorage.getItem('banka_user'))
      setUser(savedUser)
      setView(savedUser?.role === 'ADMIN' ? 'admin-dashboard' : 'dashboard')
    }
  }, [])

  const handleAuthSuccess = () => {
    const savedUser = JSON.parse(localStorage.getItem('banka_user'))
    setUser(savedUser)
    setView(savedUser?.role === 'ADMIN' ? 'admin-dashboard' : 'dashboard')
  }

  const handleLogout = () => {
    authService.logout()
    setUser(null)
    setView('login')
  }

  return (
    <div className="App">
      {view === 'dashboard' && <Dashboard onLogout={handleLogout} />}
      {view === 'admin-dashboard' && (
        <AdminDashboard
          onLogout={handleLogout}
          onGoToPersonal={() => setView('dashboard')}
        />
      )}
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
