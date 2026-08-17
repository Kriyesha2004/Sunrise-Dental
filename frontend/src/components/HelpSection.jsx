import React from 'react';
import { RiQuestionLine, RiCompassLine, RiBookOpenLine } from 'react-icons/ri';

const HelpSection = () => {
  return (
    <div className="glass-panel p-4 fade-in-up">
      <div className="d-flex align-items-center gap-2 mb-4 border-bottom pb-3">
        <div className="bg-primary bg-opacity-10 text-primary p-2 rounded-3">
          <RiQuestionLine size={24} />
        </div>
        <h4 className="fw-bold mb-0">Staff Help & Usage Guide</h4>
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
              <li className="mb-3">
                <strong>User Authentication:</strong> Log into the system using your staff credentials. Access is restricted to authorized clinic employees (Administrators, Receptionists, and Dentists).
              </li>
              <li className="mb-3">
                <strong>Register New Appointment:</strong> Navigate to the <em>Book Appointment</em> tab. Input the patient's full name, address, phone number, select their assigned dentist, treatment type, and enter the date/time. The default consultation fee is configured automatically. Upon saving, a unique <strong>Appointment Registration Number</strong> (e.g., <code>APT0482</code>) will be generated. Copy or record this number.
              </li>
              <li className="mb-3">
                <strong>Search Appointment Details:</strong> Go to the <em>Search</em> tab, type the <code>APTXXXX</code> number, and click Search. This will display all registered patient information, scheduling status, and details.
              </li>
              <li className="mb-3">
                <strong>Calculate and Process Bills:</strong> Under the <em>Billing</em> tab, search by the appointment number. The system applies the **Factory Design Pattern** to fetch treatment costs based on the treatment type:
                <ul>
                  <li>Cleaning: <strong>$50.00</strong></li>
                  <li>Filling: <strong>$80.00</strong></li>
                  <li>Extraction: <strong>$120.00</strong></li>
                  <li>Root Canal: <strong>$300.00</strong></li>
                </ul>
                The total cost equals the treatment cost plus the consultation fee, plus 10% government service tax.
              </li>
              <li className="mb-3">
                <strong>Settle Payments & Printing:</strong> Click <em>Process Payment</em> to mark the invoice as PAID. This triggers a database operation that automatically changes the appointment status to <strong>COMPLETED</strong>. Click <em>Print Bill / Receipt</em> to open a custom, print-ready document format.
              </li>
              <li>
                <strong>Exit System:</strong> To terminate your secure session, click <em>Exit System</em> in the navigation bar. This safely invalidates the session and prevents unauthorized access.
              </li>
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
              <li>
                <strong>Database Triggers:</strong> The <code>after_bill_payment</code> trigger watches updates to the <code>bills</code> table. When `payment_status` shifts to <code>PAID</code>, the trigger automatically fires an update statement on the <code>appointments</code> table, updating its status to <code>COMPLETED</code>.
              </li>
              <li>
                <strong>Factory Pattern:</strong> Treatment prices are strictly managed by a polymorphic strategy engine in the business logic layer, decoupling price policies from the database structure.
              </li>
              <li>
                <strong>Secure Sessions:</strong> Standard session cookies are used. The server checks the authentication token on every API call to prevent unauthorized API spoofing.
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HelpSection;
