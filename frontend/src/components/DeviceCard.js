import React from 'react';

function DeviceCard({ 
  device, 
  showAdminControls = false, 
  onDelete, 
  onUpdate 
}) {

  const handleDelete = () => {
    if (window.confirm(`Are you sure you want to delete ${device.name}?`)) {
      onDelete(device.id);
    }
  };

  const handleUpdate = () => {
    const newConsumption = prompt(
      `Update consumption for ${device.name}:`, 
      device.powerConsumed
    );
    
    // Check if the user pressed "OK" and the value is not null
    if (newConsumption !== null) {
      const parsedValue = parseFloat(newConsumption);
      if (!isNaN(parsedValue)) {
        onUpdate(device.id, parsedValue);
      } else {
        alert("Invalid input. Please enter a number.");
      }
    }
    // If newConsumption is null, the user pressed "Cancel", so we do nothing.
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