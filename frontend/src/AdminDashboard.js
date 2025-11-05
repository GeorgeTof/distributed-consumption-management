import React, { useState } from 'react';
import { getAllDevices, deleteDevice, updateDeviceConsumption } from './api';
import DeviceCard from './components/DeviceCard';

function AdminDashboard({ currentUser }) {
  const [devices, setDevices] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleLoadDevices = async () => {
    setLoading(true);
    setError(null);
    setDevices(null); 
    try {
      const response = await getAllDevices(currentUser.token);
      setDevices(response.data);
    } catch (err) {
      setError(err.message);
      alert(err.message);
    }
    setLoading(false);
  };

  const handleLoadUsers = () => {
    alert('Loading all users... (Not yet implemented)');
  };

  const handleDeleteDevice = async (deviceId) => {
    try {
      await deleteDevice(currentUser.token, deviceId);
      setDevices((prevDevices) =>
        prevDevices.filter((device) => device.id !== deviceId)
      );
    } catch (err) {
      alert(`Error deleting device: ${err.message}`);
    }
  };

  const handleUpdateDevice = async (deviceId, powerConsumed) => {
    try {
      const response = await updateDeviceConsumption(
        currentUser.token,
        deviceId,
        powerConsumed
      );
      const updatedDevice = response.data;

      setDevices((prevDevices) =>
        prevDevices.map((device) =>
          device.id === deviceId ? updatedDevice : device
        )
      );
    } catch (err) {
      alert(`Error updating device: ${err.message}`);
    }
  };

  return (
    <div className="admin-dashboard">
      <h3>Admin Dashboard</h3>
      <p>You can see this because you are an admin.</p>

      <div className="dashboard-buttons">
        <button onClick={handleLoadDevices} disabled={loading}>
          Load All Devices
        </button>
        <button onClick={handleLoadUsers} disabled={loading}>
          Load All Users
        </button>
      </div>

      {loading && <p>Loading...</p>}
      {error && <p className="error-message">Error: {error}</p>}

      <div className="data-display-area">
        {devices && (
          <div className="device-list">
            <h4>All Devices ({devices.length})</h4>
            {devices.length > 0 ? (
              devices.map((device) => (
                <DeviceCard
                  key={device.id}
                  device={device}
                  showAdminControls={true}
                  onDelete={handleDeleteDevice}
                  onUpdate={handleUpdateDevice}
                />
              ))
            ) : (
              <p>No devices found in the system.</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default AdminDashboard;