import React from 'react';


function DeviceCard({ device, showAdminControls = false }) {

  const handleDelete = () => {
    alert(`(Admin) Deleting device ${device.id}...`);
  };

  const handleUpdate = () => {
    const newConsumption = prompt(`Update consumption for ${device.name}:`, device.powerConsumed);
    if (newConsumption) {
      alert(`(Admin) Updating device ${device.id} to ${newConsumption}...`);
    }
  };

  return (
    <div className="device-card">
      <h5>{device.name}</h5>
      <ul>
        <li><strong>Brand:</strong> {device.brand}</li>
        <li><strong>Owner:</strong> {device.ownerUsername}</li>
        <li><strong>Max Consumption:</strong> {device.maximumConsumption} W</li>
        <li><strong>Power Consumed:</strong> {device.powerConsumed} W</li>
      </ul>
      
      {showAdminControls && (
        <div className="device-controls">
          <button className="btn-update" onClick={handleUpdate}>Update</button>
          <button className="btn-delete" onClick={handleDelete}>Delete</button>
        </div>
      )}
    </div>
  );
}

export default DeviceCard;
