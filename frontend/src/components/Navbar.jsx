import React from 'react';
import { RiSunLine, RiMoonLine, RiLogoutBoxRLine, RiUserHeartLine } from 'react-icons/ri';

const Navbar = ({ user, onLogout, theme, toggleTheme }) => {
  return (
    <nav className="navbar navbar-expand-lg glass-panel py-3 px-4 mb-4 no-print" style={{ borderRadius: '0 0 16px 16px' }}>
      <div className="container-fluid px-0">
        <a className="navbar-brand d-flex align-items-center gap-2 fw-bold text-primary" href="#" style={{ fontSize: '1.25rem' }}>
          <RiUserHeartLine size={28} />
          <span>Sunrise Dental</span>
        </a>

        <div className="d-flex align-items-center gap-3 ms-auto">
          {/* Theme Toggle Button */}
          <button
            onClick={toggleTheme}
            className="btn btn-link p-2 text-decoration-none border-0 text-muted"
            title="Toggle Theme"
            style={{ transition: 'color 0.2s' }}
          >
            {theme === 'dark' ? <RiSunLine size={22} className="text-warning" /> : <RiMoonLine size={22} className="text-dark" />}
          </button>

          {user && (
            <>
              {/* User Metadata */}
              <div className="d-none d-md-flex flex-column text-end">
                <span className="fw-semibold text-primary" style={{ fontSize: '0.9rem' }}>{user.fullname}</span>
                <span className="text-muted" style={{ fontSize: '0.75rem', textTransform: 'capitalize' }}>
                  {user.role.toLowerCase()}
                </span>
              </div>

              {/* Logout / Exit System */}
              <button
                onClick={onLogout}
                className="btn btn-outline-danger d-flex align-items-center gap-2 px-3 py-2"
                style={{ borderRadius: '8px', fontWeight: '600', fontSize: '0.85rem' }}
              >
                <RiLogoutBoxRLine size={16} />
                <span>Exit System</span>
              </button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
