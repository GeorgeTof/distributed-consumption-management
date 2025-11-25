import React, { useState } from 'react';
import { 
  getAllDevices, 
  deleteDevice, 
  updateDeviceConsumption,
  getAllUsers,
  deleteAuthUser,
  createDevice,
  updateUserEmail,
  registerUser,
} from './api';
import DeviceCard from './components/DeviceCard';
import UserCard from './components/UserCard';
import AddDeviceModal from './components/AddDeviceModal';
import AddUserModal from './components/AddUserModal';

function AdminDashboard({ currentUser }) {
  const [devices, setDevices] = useState(null);
  const [users, setUsers] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [userForDevice, setUserForDevice] = useState(null);

  const [isNewUserModalOpen, setIsNewUserModalOpen] = useState(false);

  const handleLoadDevices = async () => {
    setLoading(true);
    setError(null);
    setUsers(null);
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

  const handleLoadUsers = async () => {
    setLoading(true);
    setError(null);
    setDevices(null);
    setUsers(null);
    try {
      const response = await getAllUsers(currentUser.token);
      setUsers(response.data);
    } catch (err) {
      setError(err.message);
      alert(err.message);
    }
    setLoading(false);
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

  const handleUpdateUser = async (userId, newEmail) => {
    setLoading(true);
    try {
      const response = await updateUserEmail(currentUser.token, userId, newEmail);
      const updatedUser = response.data;

      setUsers((prevUsers) =>
        prevUsers.map((user) =>
          user.id === userId ? updatedUser : user
        )
      );
      
      alert(`Successfully updated email for ID ${userId} to ${newEmail}`);

    } catch (err) {
      alert(`Error updating user email: ${err.message}`);
    }
    setLoading(false);
  };

  const handleDeleteUser = async (user) => {
    if (!window.confirm(`Are you sure you want to delete user ${user.username}? This is irreversible.`)) {
      return;
    }
    setLoading(true);
    setError(null);
    try {
      console.log(`Deleting user ${user.username} only via Auth Service...`);
      await deleteAuthUser(currentUser.token, user.username);
      
      setUsers((prevUsers) => prevUsers.filter((u) => u.id !== user.id));
      alert(`Successfully deleted user ${user.username}`);
    } catch (err) {
      alert(`Error deleting user: ${err.message}.`);
      setError(err.message);
    }
    setLoading(false);
  };

  const handleAddDeviceForUser = (user) => {
    setUserForDevice(user);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setUserForDevice(null);
  };

  const handleCreateDevice = async (deviceData) => {
    setLoading(true);
    try {
      await createDevice(currentUser.token, deviceData);
      alert(`Successfully created device ${deviceData.name} for ${deviceData.ownerUsername}`);
      handleCloseModal();
    } catch (err)
 {
      alert(`Error creating device: ${err.message}`);
    }
    setLoading(false);
  };
  
  const handleOpenNewUserModal = () => {
    setIsNewUserModalOpen(true);
  };

  const handleCloseNewUserModal = () => {
    setIsNewUserModalOpen(false);
  };
  
  const handleCreateNewUser = async ({ profile, auth }) => {
    setLoading(true);

    try {
      const registerRequest = {
        username: auth.username,
        password: auth.password,
        email: profile.email,
        age: profile.age,
        town: profile.town,
        role: auth.role
      };

      console.log("Sending registration request:", registerRequest);

      await registerUser(currentUser.token, registerRequest);

      alert(`Successfully registered user ${profile.username}.`);
      handleCloseNewUserModal();
      handleLoadUsers();
      
    } catch (err) {
      alert(`Failed to create user: ${err.message}.`);
    } finally {
      setLoading(false);
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
        <button onClick={handleOpenNewUserModal} disabled={loading}>
          + Create New User
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

        {users && (
          <div className="user-list">
            <h4>All Users ({users.length})</h4>
            {users.length > 0 ? (
              users.map((user) => (
                <UserCard
                  key={user.id}
                  user={user}
                  onDelete={handleDeleteUser}
                  onUpdate={handleUpdateUser}
                  onAddDevice={handleAddDeviceForUser}
                />
              ))
            ) : (
              <p>No users found in the system.</p>
            )}
          </div>
        )}
      </div>

      {userForDevice && (
        <AddDeviceModal
          show={isModalOpen}
          onClose={handleCloseModal}
          onSubmit={handleCreateDevice}
          user={userForDevice}
        />
      )}
      
      <AddUserModal
        show={isNewUserModalOpen}
        onClose={handleCloseNewUserModal}
        onSubmit={handleCreateNewUser}
      />
    </div>
  );
}

export default AdminDashboard;