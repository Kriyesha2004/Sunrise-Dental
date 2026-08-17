import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Navbar from './components/Navbar';
import LoginForm from './components/LoginForm';
import AppointmentRegister from './components/AppointmentRegister';
import AppointmentSearch from './components/AppointmentSearch';
import BillingManager from './components/BillingManager';
import HelpSection from './components/HelpSection';
import { RiCalendarCheckLine, RiSearchLine, RiFileList3Line, RiQuestionLine } from 'react-icons/ri';

// Set up Axios with CORS support and smart base URL selection
const baseURL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
  ? 'http://localhost:8080'
  : '';

const axiosInstance = axios.create({
  baseURL,
  withCredentials: true
});

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('register');
  const [preselectedAppNumber, setPreselectedAppNumber] = useState('');
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'light');

  // Sync theme
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme(theme === 'light' ? 'dark' : 'light');
  };

  // Check login status on mount
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await axiosInstance.get('/api/auth/me');
        setUser(response.data);
      } catch (err) {
        setUser(null);
      } finally {
        setLoading(false);
      }
    };
    checkAuth();
  }, []);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
  };

  const handleLogout = async () => {
    try {
      await axiosInstance.post('/api/auth/logout');
    } catch (err) {
      console.error('Logout error', err);
    } finally {
      setUser(null);
    }
  };

  const handleSelectForBilling = (appointmentNumber) => {
    setPreselectedAppNumber(appointmentNumber);
    setActiveTab('billing');
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-primary" role="status" style={{ width: '3rem', height: '3rem' }}>
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-vh-100 pb-5">
      <Navbar user={user} onLogout={handleLogout} theme={theme} toggleTheme={toggleTheme} />

      <div className="container px-3">
        {!user ? (
          <LoginForm onLoginSuccess={handleLoginSuccess} axiosInstance={axiosInstance} />
        ) : (
          <div className="row g-4">
            {/* Sidebar / Tabs (hidden in print) */}
            <div className="col-lg-3 col-md-4 col-12 no-print">
              <div className="glass-panel p-3 d-flex flex-column gap-2">
                <h5 className="fw-bold mb-3 px-2 text-primary" style={{ fontSize: '0.95rem' }}>Management Menu</h5>
                
                <button
                  onClick={() => { setActiveTab('register'); setPreselectedAppNumber(''); }}
                  className={`btn d-flex align-items-center gap-2 px-3 py-2.5 text-start w-100 border-0 ${
                    activeTab === 'register' ? 'btn-premium-primary text-white' : 'btn-link text-decoration-none text-muted'
                  }`}
                  style={{ borderRadius: '8px', fontWeight: '500', fontSize: '0.9rem' }}
                >
                  <RiCalendarCheckLine size={18} />
                  <span>Book Appointment</span>
                </button>

                <button
                  onClick={() => setActiveTab('search')}
                  className={`btn d-flex align-items-center gap-2 px-3 py-2.5 text-start w-100 border-0 ${
                    activeTab === 'search' ? 'btn-premium-primary text-white' : 'btn-link text-decoration-none text-muted'
                  }`}
                  style={{ borderRadius: '8px', fontWeight: '500', fontSize: '0.9rem' }}
                >
                  <RiSearchLine size={18} />
                  <span>Search Appointment</span>
                </button>

                <button
                  onClick={() => setActiveTab('billing')}
                  className={`btn d-flex align-items-center gap-2 px-3 py-2.5 text-start w-100 border-0 ${
                    activeTab === 'billing' ? 'btn-premium-primary text-white' : 'btn-link text-decoration-none text-muted'
                  }`}
                  style={{ borderRadius: '8px', fontWeight: '500', fontSize: '0.9rem' }}
                >
                  <RiFileList3Line size={18} />
                  <span>Calculate & Print Bill</span>
                </button>

                <button
                  onClick={() => setActiveTab('help')}
                  className={`btn d-flex align-items-center gap-2 px-3 py-2.5 text-start w-100 border-0 ${
                    activeTab === 'help' ? 'btn-premium-primary text-white' : 'btn-link text-decoration-none text-muted'
                  }`}
                  style={{ borderRadius: '8px', fontWeight: '500', fontSize: '0.9rem' }}
                >
                  <RiQuestionLine size={18} />
                  <span>Help Guide</span>
                </button>
              </div>
            </div>

            {/* Active Content Panel */}
            <div className="col-lg-9 col-md-8 col-12">
              {activeTab === 'register' && <AppointmentRegister axiosInstance={axiosInstance} />}
              {activeTab === 'search' && <AppointmentSearch axiosInstance={axiosInstance} onSelectForBilling={handleSelectForBilling} />}
              {activeTab === 'billing' && <BillingManager axiosInstance={axiosInstance} preselectedAppointmentNumber={preselectedAppNumber} />}
              {activeTab === 'help' && <HelpSection />}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
