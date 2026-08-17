import React, { useState } from 'react';
import { RiSearchLine, RiFileTextLine, RiCalendarLine, RiUserLine, RiPhoneLine, RiMapPinLine, RiHeartPulseLine } from 'react-icons/ri';

const AppointmentSearch = ({ axiosInstance, onSelectForBilling }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [appointment, setAppointment] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSearch = async (e) => {
    e.preventDefault();
    setError('');
    setAppointment(null);

    if (!searchQuery.trim()) {
      setError('Please enter an appointment number to search.');
      return;
    }

    setLoading(true);
    try {
      const response = await axiosInstance.get(`/api/appointments/${searchQuery.trim().toUpperCase()}`);
      setAppointment(response.data);
    } catch (err) {
      console.error(err);
      if (err.response && err.response.status === 404) {
        setError('No appointment found with that registration number.');
      } else {
        setError('Error fetching appointment details. Try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'SCHEDULED': return 'bg-primary bg-opacity-10 text-primary';
      case 'COMPLETED': return 'bg-success bg-opacity-10 text-success';
      case 'CANCELLED': return 'bg-danger bg-opacity-10 text-danger';
      default: return 'bg-secondary bg-opacity-10 text-secondary';
    }
  };

  return (
    <div className="glass-panel p-4 fade-in-up">
      <div className="d-flex align-items-center gap-2 mb-4 border-bottom pb-3">
        <div className="bg-primary bg-opacity-10 text-primary p-2 rounded-3">
          <RiSearchLine size={24} />
        </div>
        <h4 className="fw-bold mb-0">Search Appointment Details</h4>
      </div>

      <form onSubmit={handleSearch} className="mb-4">
        <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Appointment Registration Number</label>
        <div className="input-group">
          <input
            type="text"
            className="form-control form-control-premium"
            placeholder="e.g. APT1234"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            disabled={loading}
          />
          <button type="submit" className="btn btn-premium-primary" disabled={loading}>
            {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : <RiSearchLine className="me-1" />}
            Search
          </button>
        </div>
      </form>

      {error && <div className="alert alert-danger py-2 px-3 rounded-3">{error}</div>}

      {appointment && (
        <div className="glass-card p-4 border border-secondary-subtle">
          <div className="d-flex justify-content-between align-items-start mb-3 border-bottom pb-3 flex-wrap gap-2">
            <div>
              <span className="text-muted" style={{ fontSize: '0.8rem' }}>Registration ID</span>
              <h5 className="fw-bold text-primary mb-0">{appointment.appointmentNumber}</h5>
            </div>
            <span className={`badge px-3 py-2 rounded-pill fw-semibold ${getStatusBadgeClass(appointment.status)}`} style={{ fontSize: '0.8rem' }}>
              {appointment.status}
            </span>
          </div>

          <div className="row g-3">
            <div className="col-md-6 d-flex align-items-start gap-2">
              <RiUserLine className="text-muted mt-1" size={18} />
              <div>
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>Patient Name</span>
                <span className="fw-bold" style={{ fontSize: '0.95rem' }}>{appointment.patient.name}</span>
              </div>
            </div>

            <div className="col-md-6 d-flex align-items-start gap-2">
              <RiPhoneLine className="text-muted mt-1" size={18} />
              <div>
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>Contact Number</span>
                <span className="fw-bold" style={{ fontSize: '0.95rem' }}>{appointment.patient.contactNumber}</span>
              </div>
            </div>

            <div className="col-12 d-flex align-items-start gap-2">
              <RiMapPinLine className="text-muted mt-1" size={18} />
              <div>
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>Address</span>
                <span className="fw-semibold" style={{ fontSize: '0.9rem' }}>{appointment.patient.address || 'Not Provided'}</span>
              </div>
            </div>

            <div className="col-md-6 d-flex align-items-start gap-2">
              <RiCalendarLine className="text-muted mt-1" size={18} />
              <div>
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>Scheduled Date & Time</span>
                <span className="fw-bold text-success-emphasis" style={{ fontSize: '0.9rem' }}>
                  {appointment.appointmentDate} at {appointment.appointmentTime}
                </span>
              </div>
            </div>

            <div className="col-md-6 d-flex align-items-start gap-2">
              <RiHeartPulseLine className="text-muted mt-1" size={18} />
              <div>
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>Treatment & Dentist</span>
                <span className="fw-bold" style={{ fontSize: '0.9rem' }}>
                  {appointment.treatmentType} under {appointment.dentistName}
                </span>
              </div>
            </div>
          </div>

          <div className="mt-4 pt-3 border-top d-flex gap-2">
            <button
              onClick={() => onSelectForBilling(appointment.appointmentNumber)}
              className="btn btn-premium-secondary d-flex align-items-center gap-2"
              style={{ fontSize: '0.85rem' }}
            >
              <RiFileTextLine size={16} />
              <span>Proceed to Calculate Bill</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AppointmentSearch;
