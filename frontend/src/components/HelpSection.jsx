import React, { useState, useEffect } from 'react';
import { 
  RiQuestionLine, 
  RiCompassLine, 
  RiBookOpenLine, 
  RiEdit2Line, 
  RiSave3Line, 
  RiCloseLine 
} from 'react-icons/ri';

const HelpSection = ({ user, axiosInstance }) => {
  const [guide, setGuide] = useState({
    stepInstructions: '',
    designConstraints: ''
  });
  const [isEditing, setIsEditing] = useState(false);
  const [stepInput, setStepInput] = useState('');
  const [designInput, setDesignInput] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const defaultSteps = [
    "User Authentication: Log into the system using your staff credentials. Access is restricted to authorized clinic employees (Administrators, Receptionists, and Dentists).",
    "Register New Appointment: Navigate to the Book Appointment tab. Input the patient's full name, address, phone number, select their assigned dentist, treatment type, and enter the date/time. The default consultation fee is configured automatically. Upon saving, a unique Appointment Registration Number (e.g., APT0482) will be generated. Copy or record this number.",
    "Search Appointment Details: Go to the Search tab, type the APTXXXX number, and click Search. This will display all registered patient information, scheduling status, and details.",
    "Calculate and Process Bills: Under the Billing tab, search by the appointment number. The system applies the Factory Design Pattern to fetch treatment costs based on the treatment type: Cleaning ($50.00), Filling ($80.00), Extraction ($120.00), Root Canal ($300.00). The total cost equals the treatment cost plus the consultation fee, plus 10% government service tax.",
    "Settle Payments & Printing: Click Process Payment to mark the invoice as PAID. This triggers a database operation that automatically changes the appointment status to COMPLETED. Click Print Bill / Receipt to open a custom, print-ready document format.",
    "Exit System: To terminate your secure session, click Exit System in the navigation bar. This safely invalidates the session and prevents unauthorized access."
  ].join('|');

  const defaultConstraints = [
    "Database Triggers: The after_bill_payment trigger watches updates to the bills table. When payment_status shifts to PAID, the trigger automatically fires an update statement on the appointments table, updating its status to COMPLETED.",
    "Factory Pattern: Treatment prices are strictly managed by a polymorphic strategy engine in the business logic layer, decoupling price policies from the database structure.",
    "Secure Sessions: Standard session cookies are used. The server checks the authentication token on every API call to prevent unauthorized API spoofing."
  ].join('|');

  const fetchGuide = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await axiosInstance.get('/api/help-guide');
      setGuide(res.data);
      setStepInput(res.data.stepInstructions ? res.data.stepInstructions.split('|').join('\n') : '');
      setDesignInput(res.data.designConstraints ? res.data.designConstraints.split('|').join('\n') : '');
    } catch (err) {
      console.error("Failed to fetch help guide, using defaults", err);
      setGuide({ stepInstructions: defaultSteps, designConstraints: defaultConstraints });
      setStepInput(defaultSteps.split('|').join('\n'));
      setDesignInput(defaultConstraints.split('|').join('\n'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (axiosInstance) {
      fetchGuide();
    }
  }, [axiosInstance]);

  const handleSave = async () => {
    const formattedSteps = stepInput.split('\n').filter(line => line.trim() !== '').join('|');
    const formattedConstraints = designInput.split('\n').filter(line => line.trim() !== '').join('|');
    
    setLoading(true);
    try {
      setError('');
      const res = await axiosInstance.put('/api/help-guide', {
        stepInstructions: formattedSteps,
        designConstraints: formattedConstraints
      });
      setGuide(res.data);
      setIsEditing(false);
    } catch (err) {
      console.error(err);
      setError(err.response?.data || 'Failed to update help guide. Please ensure you are authenticated as DENTIST.');
    } finally {
      setLoading(false);
    }
  };

  if (loading && !guide.stepInstructions) {
    return (
      <div className="d-flex justify-content-center align-items-center p-5">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading Help Guide...</span>
        </div>
      </div>
    );
  }

  const isDentist = user?.role === 'DENTIST';
  const steps = guide.stepInstructions ? guide.stepInstructions.split('|') : [];
  const constraints = guide.designConstraints ? guide.designConstraints.split('|') : [];

  if (isEditing) {
    return (
      <div className="glass-panel p-4 fade-in-up">
        <div className="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
          <div className="d-flex align-items-center gap-2">
            <div className="bg-primary bg-opacity-10 text-primary p-2 rounded-3">
              <RiQuestionLine size={24} />
            </div>
            <h4 className="fw-bold mb-0">Edit Help & Usage Guide</h4>
          </div>
          <div className="d-flex gap-2">
            <button className="btn btn-premium-primary d-flex align-items-center gap-1 py-2 px-3" onClick={handleSave} disabled={loading}>
              <RiSave3Line size={18} />
              <span>Save</span>
            </button>
            <button className="btn btn-outline-secondary d-flex align-items-center gap-1 py-2 px-3" onClick={() => setIsEditing(false)} disabled={loading}>
              <RiCloseLine size={18} />
              <span>Cancel</span>
            </button>
          </div>
        </div>

        {error && (
          <div className="alert alert-danger py-2 px-3 mb-4 rounded-3 text-start" role="alert" style={{ fontSize: '0.9rem' }}>
            {error}
          </div>
        )}

        <div className="row g-4">
          <div className="col-12">
            <div className="glass-card p-4 border border-secondary-subtle">
              <h5 className="fw-bold text-primary mb-3">Step-by-Step Instructions (One instruction per line)</h5>
              <textarea 
                className="form-control form-control-premium text-start" 
                rows="8" 
                style={{ fontSize: '0.9rem', lineHeight: '1.6' }}
                value={stepInput}
                onChange={(e) => setStepInput(e.target.value)}
                disabled={loading}
              />
            </div>
          </div>

          <div className="col-12">
            <div className="glass-card p-4 border border-secondary-subtle">
              <h5 className="fw-bold text-secondary mb-3">System Design & Integrity Constraints (One constraint per line)</h5>
              <textarea 
                className="form-control form-control-premium text-start" 
                rows="5" 
                style={{ fontSize: '0.9rem', lineHeight: '1.6' }}
                value={designInput}
                onChange={(e) => setDesignInput(e.target.value)}
                disabled={loading}
              />
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="glass-panel p-4 fade-in-up">
      <div className="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
        <div className="d-flex align-items-center gap-2">
          <div className="bg-primary bg-opacity-10 text-primary p-2 rounded-3">
            <RiQuestionLine size={24} />
          </div>
          <h4 className="fw-bold mb-0">Staff Help & Usage Guide</h4>
        </div>
        {isDentist && (
          <button className="btn btn-outline-primary d-flex align-items-center gap-1 py-1.5 px-3" onClick={() => setIsEditing(true)}>
            <RiEdit2Line size={16} />
            <span>Edit Guide</span>
          </button>
        )}
      </div>

      <div className="row g-4">
        {/* Quick Start Guide */}
        <div className="col-12">
          <div className="glass-card p-4 border border-secondary-subtle">
            <h5 className="fw-bold text-primary mb-3 d-flex align-items-center gap-2">
              <RiCompassLine size={20} />
              <span>Step-by-Step Instructions</span>
            </h5>
            
            <ol className="lh-lg mb-0" style={{ fontSize: '0.92rem' }}>
              {steps.map((step, idx) => {
                const parts = step.split(':');
                if (parts.length > 1) {
                  return (
                    <li key={idx} className="mb-3">
                      <strong>{parts[0]}:</strong>{parts.slice(1).join(':')}
                    </li>
                  );
                }
                return (
                  <li key={idx} className="mb-3">{step}</li>
                );
              })}
            </ol>
          </div>
        </div>

        {/* Database Rules & Advanced Logic */}
        <div className="col-12">
          <div className="glass-card p-4 border border-secondary-subtle">
            <h5 className="fw-bold text-secondary mb-3 d-flex align-items-center gap-2">
              <RiBookOpenLine size={20} />
              <span>System Design & Integrity Constraints</span>
            </h5>
            
            <ul className="lh-lg mb-0 text-muted" style={{ fontSize: '0.85rem' }}>
              {constraints.map((constraint, idx) => {
                const parts = constraint.split(':');
                if (parts.length > 1) {
                  return (
                    <li key={idx} className="mb-2">
                      <strong>{parts[0]}:</strong>{parts.slice(1).join(':')}
                    </li>
                  );
                }
                return (
                  <li key={idx} className="mb-2">{constraint}</li>
                );
              })}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HelpSection;
