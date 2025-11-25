import React, { useState } from 'react';
import './AddDeviceModal.css'; // Re-using your existing styles

function EnergyConsumptionModal({ show, onClose, onSubmit }) {
  const [selectedDate, setSelectedDate] = useState('');
  const [error, setError] = useState('');

  if (!show) {
    return null;
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!selectedDate) {
      setError('Please select a date.');
      return;
    }

    onSubmit(selectedDate);
    // Reset date after submit if desired, or keep it.
    setSelectedDate('');
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h4>Select Date for Energy Consumption</h4>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="energy-date">Date</label>
            <input
              type="date"
              id="energy-date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              // Optional: restrict to not selecting future dates
              max={new Date().toISOString().split('T')[0]} 
            />
          </div>

          {error && <p className="error-message">{error}</p>}

          <div className="modal-buttons">
            <button type="submit">View Consumption</button>
            <button type="button" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EnergyConsumptionModal;