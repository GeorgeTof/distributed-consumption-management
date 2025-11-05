import React, { useState } from 'react';
import './AddDeviceModal.css';

function AddDeviceModal({ show, onClose, onSubmit, user }) {
  const [name, setName] = useState('');
  const [brand, setBrand] = useState('');
  const [maxConsumption, setMaxConsumption] = useState('');
  const [error, setError] = useState('');

  if (!show) {
    return null;
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!name || !brand || !maxConsumption) {
      setError('All fields are required.');
      return;
    }
    
    const parsedConsumption = parseFloat(maxConsumption);
    if (isNaN(parsedConsumption) || parsedConsumption <= 0) {
      setError('Maximum Consumption must be a positive number.');
      return;
    }

    const deviceData = {
      name,
      brand,
      maximumConsumption: parsedConsumption,
      ownerUsername: user.username,
    };
    
    onSubmit(deviceData);
    
    setName('');
    setBrand('');
    setMaxConsumption('');
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h4>Add Device for {user.username}</h4>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="dev-name">Device Name</label>
            <input
              type="text"
              id="dev-name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="dev-brand">Brand</label>
            <input
              type="text"
              id="dev-brand"
              value={brand}
              onChange={(e) => setBrand(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="dev-consump">Max Consumption (W)</label>
            <input
              type="number"
              id="dev-consump"
              value={maxConsumption}
              onChange={(e) => setMaxConsumption(e.target.value)}
            />
          </div>
          {error && <p className="error-message">{error}</p>}
          <div className="modal-buttons">
            <button type="submit">Create Device</button>
            <button type="button" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default AddDeviceModal;