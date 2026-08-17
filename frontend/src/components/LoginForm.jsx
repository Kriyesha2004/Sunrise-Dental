import React, { useState } from 'react';
import { RiShieldKeyholeLine, RiUserLine } from 'react-icons/ri';

const LoginForm = ({ onLoginSuccess, axiosInstance }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Client-side validations
    if (!username.trim()) {
      setError('Username is required.');
      return;
    }
    if (!password.trim()) {
      setError('Password is required.');
      return;
    }

    setLoading(true);
    try {
      const response = await axiosInstance.post('/api/auth/login', { username, password });
      onLoginSuccess(response.data);
    } catch (err) {
      console.error(err);
      if (err.response && err.response.data) {
        setError(typeof err.response.data === 'string' ? err.response.data : 'Invalid credentials.');
      } else {
        setError('Connection to backend failed. Please ensure backend is running.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <div className="glass-panel p-5 fade-in-up text-center" style={{ maxWidth: '450px', width: '100%' }}>
        <div className="mb-4">
          <div className="d-inline-flex align-items-center justify-content-center bg-primary bg-opacity-10 text-primary rounded-circle p-3 mb-3">
            <RiShieldKeyholeLine size={40} />
          </div>
          <h2 className="fw-bold mb-1">Sunrise Dental</h2>
          <p className="text-muted">Authorized staff portal login</p>
        </div>

        {error && (
          <div className="alert alert-danger py-2 px-3 mb-4 rounded-3 text-start" role="alert" style={{ fontSize: '0.9rem' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="text-start mb-3">
            <label className="form-label fw-semibold mb-1" style={{ fontSize: '0.85rem' }}>Username</label>
            <div className="input-group">
              <span className="input-group-text bg-transparent border-end-0 border-secondary-subtle text-muted" style={{ borderTopLeftRadius: '8px', borderBottomLeftRadius: '8px' }}>
                <RiUserLine />
              </span>
              <input
                type="text"
                className="form-control form-control-premium border-start-0 ps-0"
                style={{ borderTopLeftRadius: 0, borderBottomLeftRadius: 0 }}
                placeholder="Enter username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={loading}
              />
            </div>
          </div>

          <div className="text-start mb-4">
            <label className="form-label fw-semibold mb-1" style={{ fontSize: '0.85rem' }}>Password</label>
            <div className="input-group">
              <span className="input-group-text bg-transparent border-end-0 border-secondary-subtle text-muted" style={{ borderTopLeftRadius: '8px', borderBottomLeftRadius: '8px' }}>
                <RiShieldKeyholeLine />
              </span>
              <input
                type="password"
                className="form-control form-control-premium border-start-0 ps-0"
                style={{ borderTopLeftRadius: 0, borderBottomLeftRadius: 0 }}
                placeholder="Enter password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
              />
            </div>
          </div>

          <button type="submit" className="btn btn-premium-primary w-100 py-3" disabled={loading}>
            {loading ? (
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            ) : null}
            Sign In
          </button>
        </form>

        <div className="mt-4 pt-3 border-top border-secondary-subtle" style={{ fontSize: '0.8rem' }}>
          <p className="text-muted mb-0">Use demo user credentials:</p>
          <code className="text-primary">receptionist</code> / <code className="text-primary">password</code>
        </div>
      </div>
    </div>
  );
};

export default LoginForm;
