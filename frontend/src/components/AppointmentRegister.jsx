import React, { useState } from 'react';
import { RiCalendarCheckLine, RiCheckboxCircleLine } from 'react-icons/ri';

const AppointmentRegister = ({ axiosInstance }) => {
  const [formData, setFormData] = useState({
    patientName: '',
    patientAddress: '',
    patientContact: '',
    dentistName: '',
    treatmentType: 'Cleaning',
    appointmentDate: '',
    appointmentTime: '',
    consultationFee: '30.00'
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [createdAppointment, setCreatedAppointment] = useState(null);

  const treatmentTypes = ['Cleaning', 'Filling', 'Extraction', 'Root Canal'];
  const dentists = ['Dr. Smith', 'Dr. Perera', 'Dr. Alwis'];

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const validate = () => {
    if (!formData.patientName.trim()) return 'Patient Name is required.';
    if (formData.patientName.trim().length < 2) return 'Patient Name must be at least 2 characters.';
    if (!formData.patientContact.trim()) return 'Contact Number is required.';
    if (!/^[0-9+\-\s()]{7,20}$/.test(formData.patientContact.trim())) {
      return 'Please enter a valid contact number (numbers/spaces/hyphens, minimum 7 digits).';
    }
    if (!formData.dentistName) return 'Please select a dentist.';
    if (!formData.treatmentType) return 'Please select a treatment type.';
    if (!formData.appointmentDate) return 'Please select an appointment date.';
    if (!formData.appointmentTime) return 'Please select an appointment time.';
    if (!formData.consultationFee || isNaN(formData.consultationFee) || parseFloat(formData.consultationFee) < 0) {
      return 'Consultation fee must be a positive number.';
    }
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMsg('');
    setCreatedAppointment(null);

    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    try {
      const response = await axiosInstance.post('/api/appointments', formData);
      setSuccessMsg('Appointment registered successfully!');
      setCreatedAppointment(response.data);
      
      // Clear form
      setFormData({
        patientName: '',
        patientAddress: '',
        patientContact: '',
        dentistName: '',
        treatmentType: 'Cleaning',
        appointmentDate: '',
        appointmentTime: '',
        consultationFee: '30.00'
      });
    } catch (err) {
      console.error(err);
      if (err.response && err.response.data) {
        setError(typeof err.response.data === 'string' ? err.response.data : 'Failed to register appointment.');
      } else {
        setError('Connection to backend failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="glass-panel p-4 fade-in-up">
      <div className="d-flex align-items-center gap-2 mb-4 border-bottom pb-3">
        <div className="bg-primary bg-opacity-10 text-primary p-2 rounded-3">
          <RiCalendarCheckLine size={24} />
        </div>
        <h4 className="fw-bold mb-0">Register New Appointment</h4>
      </div>

      {error && <div className="alert alert-danger py-2 px-3 mb-3 rounded-3">{error}</div>}
      {successMsg && <div className="alert alert-success py-2 px-3 mb-3 rounded-3">{successMsg}</div>}

      {createdAppointment && (
        <div className="alert alert-info border border-info border-opacity-50 p-3 mb-4 rounded-3 d-flex align-items-start gap-3">
          <RiCheckboxCircleLine size={24} className="text-info mt-1" />
          <div>
            <h6 className="alert-heading fw-bold mb-1">Appointment Successfully Created!</h6>
            <p className="mb-0 mb-1" style={{ fontSize: '0.9rem' }}>
              Write down the unique registration number:
            </p>
            <div className="d-inline-block bg-white dark:bg-dark text-primary fw-bold px-3 py-1 rounded border border-primary border-opacity-20 shadow-sm" style={{ fontSize: '1.2rem', letterSpacing: '1px' }}>
              {createdAppointment.appointmentNumber}
            </div>
            <p className="text-muted mt-2 mb-0" style={{ fontSize: '0.8rem' }}>
              Patient Name: <strong>{createdAppointment.patient.name}</strong> | Dentist: <strong>{createdAppointment.dentistName}</strong>
            </p>
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="row g-3">
          {/* Patient Name */}
          <div className="col-md-6">
            <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Patient Name</label>
            <input
              type="text"
              name="patientName"
              className="form-control form-control-premium"
              placeholder="e.g. John Doe"
              value={formData.patientName}
              onChange={handleChange}
              disabled={loading}
            />
          </div>

          {/* Contact Number */}
          <div className="col-md-6">
            <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Contact Number</label>
            <input
              type="text"
              name="patientContact"
              className="form-control form-control-premium"
              placeholder="e.g. 0771234567"
              value={formData.patientContact}
              onChange={handleChange}
              disabled={loading}
            />
          </div>

          {/* Address */}
          <div className="col-12">
            <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Patient Address</label>
            <input
              type="text"
              name="patientAddress"
              className="form-control form-control-premium"
              placeholder="e.g. 123 Temple Rd, Colombo"
              value={formData.patientAddress}
              onChange={handleChange}
              disabled={loading}
            />
          </div>

          {/* Dentist Selection */}
          <div className="col-md-6">
            <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Dentist Name</label>
            <select
              name="dentistName"
              className="form-select form-control-premium"
              value={formData.dentistName}
              onChange={handleChange}
              disabled={loading}
            >
              <option value="">-- Select Dentist --</option>
              {dentists.map((d) => <option key={d} value={d}>{d}</option>)}
            </select>
          </div>

          {/* Treatment Type */}
          <div className="col-md-6">
            <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Treatment Type</label>
            <select
              name="treatmentType"
              className="form-select form-control-premium"
              value={formData.treatmentType}
              onChange={handleChange}
              disabled={loading}
            >
              {treatmentTypes.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>

          {/* Date */}
          <div className="col-md-4">
            <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Appointment Date</label>
            <input
              type="date"
              name="appointmentDate"
              className="form-control form-control-premium"
              value={formData.appointmentDate}
              onChange={handleChange}
              disabled={loading}
            />
          </div>

          {/* Time */}
          <div className="col-md-4">
            <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Appointment Time</label>
            <input
              type="time"
              name="appointmentTime"
              className="form-control form-control-premium"
              value={formData.appointmentTime}
              onChange={handleChange}
              disabled={loading}
            />
          </div>

          {/* Consultation Fee */}
          <div className="col-md-4">
            <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Consultation Fee ($)</label>
            <input
              type="number"
              step="0.01"
              name="consultationFee"
              className="form-control form-control-premium"
              value={formData.consultationFee}
              onChange={handleChange}
              disabled={loading}
            />
          </div>
        </div>

        <div className="mt-4 pt-2">
          <button type="submit" className="btn btn-premium-primary w-100 py-3" disabled={loading}>
            {loading ? (
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            ) : null}
            Book Appointment
          </button>
        </div>
      </form>
    </div>
  );
};

export default AppointmentRegister;
