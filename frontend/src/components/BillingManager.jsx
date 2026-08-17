import React, { useState, useEffect } from 'react';
import { RiFileList3Line, RiPrinterLine, RiMoneyDollarCircleLine, RiCheckboxCircleFill } from 'react-icons/ri';

const BillingManager = ({ axiosInstance, preselectedAppointmentNumber }) => {
  const [appointmentNumber, setAppointmentNumber] = useState('');
  const [bill, setBill] = useState(null);
  const [loading, setLoading] = useState(false);
  const [payLoading, setPayLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    if (preselectedAppointmentNumber) {
      setAppointmentNumber(preselectedAppointmentNumber);
      calculateBill(preselectedAppointmentNumber);
    }
  }, [preselectedAppointmentNumber]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (!appointmentNumber.trim()) {
      setError('Please enter an appointment number.');
      return;
    }
    calculateBill(appointmentNumber.trim().toUpperCase());
  };

  const calculateBill = async (appNum) => {
    setError('');
    setSuccessMsg('');
    setBill(null);
    setLoading(true);

    try {
      const response = await axiosInstance.post(`/api/bills/calculate/${appNum}`);
      setBill(response.data);
    } catch (err) {
      console.error(err);
      if (err.response && err.response.status === 404) {
        setError('No appointment found or error processing billing for registration: ' + appNum);
      } else {
        setError('Connection error generating invoice. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async () => {
    if (!bill) return;
    setError('');
    setSuccessMsg('');
    setPayLoading(true);

    try {
      const response = await axiosInstance.post(`/api/bills/pay/${bill.billId}`);
      setBill(response.data);
      setSuccessMsg('Payment processed successfully! Invoice status updated to PAID.');
    } catch (err) {
      console.error(err);
      setError('Failed to process payment. Try again.');
    } finally {
      setPayLoading(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="glass-panel p-4 fade-in-up">
      {/* Tab/Header */}
      <div className="d-flex align-items-center gap-2 mb-4 border-bottom pb-3 no-print">
        <div className="bg-primary bg-opacity-10 text-primary p-2 rounded-3">
          <RiFileList3Line size={24} />
        </div>
        <h4 className="fw-bold mb-0">Calculate & Print Bill</h4>
      </div>

      {/* Search Input for billing */}
      <form onSubmit={handleSearch} className="mb-4 no-print">
        <label className="form-label fw-semibold" style={{ fontSize: '0.85rem' }}>Search and Calculate Bill</label>
        <div className="input-group">
          <input
            type="text"
            className="form-control form-control-premium"
            placeholder="Enter Appointment Number (e.g. APT1234)"
            value={appointmentNumber}
            onChange={(e) => setAppointmentNumber(e.target.value)}
            disabled={loading}
          />
          <button type="submit" className="btn btn-premium-primary" disabled={loading}>
            {loading ? <span className="spinner-border spinner-border-sm me-2"></span> : 'Generate Bill'}
          </button>
        </div>
      </form>

      {error && <div className="alert alert-danger py-2 px-3 mb-3 rounded-3 no-print">{error}</div>}
      {successMsg && <div className="alert alert-success py-2 px-3 mb-3 rounded-3 no-print">{successMsg}</div>}

      {/* Bill View */}
      {bill && (
        <div className="p-3">
          {/* Printable Invoice Container */}
          <div className="glass-card p-4 border border-secondary-subtle" id="printable-invoice">
            {/* Header info */}
            <div className="row mb-4 border-bottom pb-3 align-items-center">
              <div className="col-md-6 col-12 text-md-start text-center">
                <h4 className="fw-bold text-primary mb-1">Sunrise Dental Clinic</h4>
                <p className="text-muted mb-0" style={{ fontSize: '0.8rem' }}>
                  No. 45 Galle Road, Colombo 03, Sri Lanka<br />
                  Tel: +94 11 234 5678 | Email: info@sunrisedental.lk
                </p>
              </div>
              <div className="col-md-6 col-12 text-md-end text-center mt-md-0 mt-3">
                <h5 className="fw-bold text-secondary mb-1">INVOICE / RECEIPT</h5>
                <span className="text-muted" style={{ fontSize: '0.8rem' }}>Invoice ID: <strong>INV-{bill.billId}</strong></span><br />
                <span className="text-muted" style={{ fontSize: '0.8rem' }}>Date: {new Date(bill.billDate).toLocaleString()}</span>
              </div>
            </div>

            {/* Patient & Appointment Details */}
            <div className="row mb-4 bg-light dark:bg-dark p-3 rounded-3 g-2 mx-0 border">
              <div className="col-sm-6">
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>PATIENT NAME</span>
                <span className="fw-bold text-dark dark:text-light">{bill.patientName}</span>
              </div>
              <div className="col-sm-6 text-sm-end">
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>APPOINTMENT NO.</span>
                <span className="fw-bold text-primary">{bill.appointmentNumber}</span>
              </div>
              <div className="col-12 mt-2 pt-2 border-top border-secondary-subtle">
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>TREATMENT TYPE</span>
                <span className="fw-semibold text-dark dark:text-light">{bill.treatmentType}</span>
              </div>
            </div>

            {/* Calculations Breakdown */}
            <div className="table-responsive mb-4">
              <table className="table table-bordered align-middle">
                <thead className="table-light">
                  <tr>
                    <th>Service Description</th>
                    <th className="text-end" style={{ width: '150px' }}>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>Dental Treatment: <strong>{bill.treatmentType}</strong> (Base Fee)</td>
                    <td className="text-end fw-semibold">${bill.treatmentCost.toFixed(2)}</td>
                  </tr>
                  <tr>
                    <td>Dentist Consultation Fee</td>
                    <td className="text-end fw-semibold">${bill.consultationFee.toFixed(2)}</td>
                  </tr>
                  <tr className="table-light">
                    <td className="text-end fw-bold">Subtotal:</td>
                    <td className="text-end fw-bold">${bill.totalCost.toFixed(2)}</td>
                  </tr>
                  <tr>
                    <td className="text-end text-muted">Government Service Tax (10.0%):</td>
                    <td className="text-end text-muted fw-semibold">${bill.tax.toFixed(2)}</td>
                  </tr>
                  <tr className="table-secondary">
                    <td className="text-end fw-extrabold h5 mb-0">Total Due (USD):</td>
                    <td className="text-end fw-extrabold h5 mb-0 text-primary">${bill.grandTotal.toFixed(2)}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            {/* Payment Status Stamp */}
            <div className="d-flex justify-content-between align-items-center">
              <div>
                <span className="text-muted d-block" style={{ fontSize: '0.75rem' }}>INVOICE STATUS</span>
                <span className={`badge px-3 py-2 fw-bold text-uppercase rounded-3 ${bill.paymentStatus === 'PAID' ? 'bg-success' : 'bg-warning text-dark'}`}>
                  {bill.paymentStatus}
                </span>
              </div>
              <div className="text-end">
                <p className="text-muted mb-0" style={{ fontSize: '0.75rem' }}>Authorized Signature</p>
                <div className="mt-3 border-top border-dark border-opacity-50 pt-1" style={{ width: '150px', fontSize: '0.75rem' }}>
                  Sunrise Dental Staff
                </div>
              </div>
            </div>
          </div>

          {/* Action buttons (unprinted) */}
          <div className="d-flex justify-content-end gap-3 mt-4 no-print">
            {bill.paymentStatus !== 'PAID' && (
              <button
                onClick={handlePayment}
                className="btn btn-premium-secondary d-flex align-items-center gap-2"
                disabled={payLoading}
              >
                {payLoading ? <span className="spinner-border spinner-border-sm"></span> : <RiMoneyDollarCircleLine size={18} />}
                <span>Process Payment</span>
              </button>
            )}

            {bill.paymentStatus === 'PAID' && (
              <div className="d-flex align-items-center gap-1 text-success me-auto">
                <RiCheckboxCircleFill size={20} />
                <span className="fw-bold" style={{ fontSize: '0.85rem' }}>Bill is Fully Settled</span>
              </div>
            )}

            <button
              onClick={handlePrint}
              className="btn btn-outline-primary d-flex align-items-center gap-2"
              style={{ borderRadius: '8px', fontWeight: '600' }}
            >
              <RiPrinterLine size={18} />
              <span>Print Bill / Receipt</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default BillingManager;
