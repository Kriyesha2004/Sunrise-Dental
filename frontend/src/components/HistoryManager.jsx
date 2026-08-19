import React, { useState, useEffect } from 'react';
import { 
  RiHistoryLine, 
  RiDeleteBinLine, 
  RiCalendarEventLine, 
  RiFileList3Line, 
  RiUserLine, 
  RiSearchLine,
  RiTimeLine
} from 'react-icons/ri';

const HistoryManager = ({ user, axiosInstance }) => {
  const [activeSubTab, setActiveSubTab] = useState('appointments');
  const [appointments, setAppointments] = useState([]);
  const [bills, setBills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      const [appRes, billRes] = await Promise.all([
        axiosInstance.get('/api/appointments'),
        axiosInstance.get('/api/bills')
      ]);
      setAppointments(appRes.data);
      setBills(billRes.data);
    } catch (err) {
      console.error(err);
      setError('Failed to fetch historical logs. Ensure you are logged in as Admin or Dentist.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (axiosInstance) {
      fetchData();
    }
  }, [axiosInstance]);

  const handleDeleteAppointment = async (id, appNum) => {
    const confirm = window.confirm(`Are you sure you want to delete appointment ${appNum}?\nThis will also permanently delete any associated bill records.`);
    if (!confirm) return;

    try {
      setError('');
      await axiosInstance.delete(`/api/appointments/${id}`);
      fetchData(); // Refresh both lists since deleting appointment cascades to deleting its bill
    } catch (err) {
      console.error(err);
      setError(err.response?.data || 'Failed to delete appointment.');
    }
  };

  const handleDeleteBill = async (id, billId) => {
    const confirm = window.confirm(`Are you sure you want to delete invoice INV-${billId}?`);
    if (!confirm) return;

    try {
      setError('');
      await axiosInstance.delete(`/api/bills/${id}`);
      fetchData();
    } catch (err) {
      console.error(err);
      setError(err.response?.data || 'Failed to delete bill.');
    }
  };

  const filteredAppointments = appointments.filter(app => 
    app.appointmentNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
    app.patient.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    app.dentistName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    app.treatmentType.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredBills = bills.filter(bill => 
    bill.appointmentNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
    bill.patientName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    bill.treatmentType.toLowerCase().includes(searchQuery.toLowerCase()) ||
    bill.paymentStatus.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="glass-panel p-4 fade-in-up">
      {/* Header */}
      <div className="d-flex flex-md-row flex-column align-items-md-center justify-content-between mb-4 border-bottom pb-3 gap-3">
        <div className="d-flex align-items-center gap-2">
          <div className="bg-primary bg-opacity-10 text-primary p-2 rounded-3">
            <RiHistoryLine size={24} />
          </div>
          <div>
            <h4 className="fw-bold mb-0">System History Logs</h4>
            <small className="text-muted">Manage system bookings and invoice records (Admin & Dentist only)</small>
          </div>
        </div>
        
        {/* Search */}
        <div className="input-group" style={{ maxWidth: '300px' }}>
          <span className="input-group-text bg-transparent border-end-0 border-secondary-subtle text-muted">
            <RiSearchLine />
          </span>
          <input
            type="text"
            className="form-control form-control-premium border-start-0 ps-0"
            placeholder="Search records..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {error && (
        <div className="alert alert-danger py-2 px-3 mb-4 rounded-3 text-start" role="alert" style={{ fontSize: '0.9rem' }}>
          {error}
        </div>
      )}

      {/* Sub-Tabs */}
      <div className="d-flex gap-2 mb-4">
        <button
          onClick={() => { setActiveSubTab('appointments'); setSearchQuery(''); }}
          className={`btn d-flex align-items-center gap-1.5 py-2 px-3 border-0 ${
            activeSubTab === 'appointments' ? 'btn-premium-primary text-white' : 'btn-light text-muted'
          }`}
          style={{ borderRadius: '8px', fontWeight: '500', fontSize: '0.85rem' }}
        >
          <RiCalendarEventLine size={16} />
          <span>All Bookings ({appointments.length})</span>
        </button>
        <button
          onClick={() => { setActiveSubTab('bills'); setSearchQuery(''); }}
          className={`btn d-flex align-items-center gap-1.5 py-2 px-3 border-0 ${
            activeSubTab === 'bills' ? 'btn-premium-primary text-white' : 'btn-light text-muted'
          }`}
          style={{ borderRadius: '8px', fontWeight: '500', fontSize: '0.85rem' }}
        >
          <RiFileList3Line size={16} />
          <span>All Bills/Invoices ({bills.length})</span>
        </button>
      </div>

      {/* Table Container */}
      {loading ? (
        <div className="d-flex justify-content-center align-items-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading logs...</span>
          </div>
        </div>
      ) : activeSubTab === 'appointments' ? (
        /* Appointments Log Table */
        <div className="table-responsive">
          {filteredAppointments.length === 0 ? (
            <div className="text-center py-5 text-muted">No appointments found.</div>
          ) : (
            <table className="table table-premium align-middle">
              <thead>
                <tr>
                  <th>App #</th>
                  <th>Patient info</th>
                  <th>Dentist</th>
                  <th>Treatment</th>
                  <th>Date & Time</th>
                  <th>Status</th>
                  <th className="text-center">Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredAppointments.map((app) => (
                  <tr key={app.id}>
                    <td>
                      <span className="fw-bold text-primary">{app.appointmentNumber}</span>
                    </td>
                    <td>
                      <div className="d-flex flex-column">
                        <span className="fw-semibold text-dark">{app.patient.name}</span>
                        <span className="text-muted" style={{ fontSize: '0.78rem' }}>{app.patient.contactNumber} | {app.patient.email}</span>
                      </div>
                    </td>
                    <td>{app.dentistName}</td>
                    <td>{app.treatmentType}</td>
                    <td>
                      <div className="d-flex flex-column text-muted" style={{ fontSize: '0.85rem' }}>
                        <span>{app.appointmentDate}</span>
                        <span>{app.appointmentTime}</span>
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${
                        app.status === 'COMPLETED' ? 'bg-success bg-opacity-10 text-success' : 'bg-warning bg-opacity-10 text-warning'
                      }`} style={{ fontSize: '0.75rem' }}>
                        {app.status}
                      </span>
                    </td>
                    <td className="text-center">
                      <button 
                        onClick={() => handleDeleteAppointment(app.id, app.appointmentNumber)}
                        className="btn btn-outline-danger btn-sm p-1.5 rounded-circle border-0"
                        title="Delete Appointment"
                      >
                        <RiDeleteBinLine size={16} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      ) : (
        /* Bills Log Table */
        <div className="table-responsive">
          {filteredBills.length === 0 ? (
            <div className="text-center py-5 text-muted">No invoices found.</div>
          ) : (
            <table className="table table-premium align-middle">
              <thead>
                <tr>
                  <th>Invoice ID</th>
                  <th>App #</th>
                  <th>Patient</th>
                  <th>Treatment Type</th>
                  <th>Grand Total</th>
                  <th>Date</th>
                  <th>Status</th>
                  <th className="text-center">Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredBills.map((bill) => (
                  <tr key={bill.id}>
                    <td>
                      <span className="fw-bold text-dark">INV-{bill.id}</span>
                    </td>
                    <td>
                      <span className="fw-semibold text-primary">{bill.appointmentNumber}</span>
                    </td>
                    <td>{bill.patientName}</td>
                    <td>{bill.treatmentType}</td>
                    <td>
                      <span className="fw-bold text-success">${bill.grandTotal.toFixed(2)}</span>
                    </td>
                    <td>
                      <span className="text-muted" style={{ fontSize: '0.82rem' }}>
                        {new Date(bill.billDate).toLocaleDateString()}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${
                        bill.paymentStatus === 'PAID' ? 'bg-success bg-opacity-10 text-success' : 'bg-danger bg-opacity-10 text-danger'
                      }`} style={{ fontSize: '0.75rem' }}>
                        {bill.paymentStatus}
                      </span>
                    </td>
                    <td className="text-center">
                      <button 
                        onClick={() => handleDeleteBill(bill.id, bill.id)}
                        className="btn btn-outline-danger btn-sm p-1.5 rounded-circle border-0"
                        title="Delete Invoice"
                      >
                        <RiDeleteBinLine size={16} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
};

export default HistoryManager;
