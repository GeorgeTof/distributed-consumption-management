import React, { useState } from 'react';
import { 
  getAllDevices, 
  deleteDevice, 
  updateDeviceConsumption,
  getAllUsers,
  deleteAuthUser,
  deleteDevicesByUsername,
  deleteUser,
  createDevice,
  updateUserEmail,
  createUserProfile,
  signUpAuth
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
      console.log(`Step 1: Deleting from auth-service for ${user.username}`);
      await deleteAuthUser(currentUser.token, user.username);
      console.log(`Step 2: Deleting devices for ${user.username}`);
      await deleteDevicesByUsername(currentUser.token, user.username);
      console.log(`Step 3: Deleting from user-service for ID ${user.id}`);
      await deleteUser(currentUser.token, user.id);
      setUsers((prevUsers) => prevUsers.filter((u) => u.id !== user.id));
      alert(`Successfully deleted user ${user.username}`);
    } catch (err) {
      alert(`Error deleting user: ${err.message}. The data may be inconsistent.`);
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
      alert(`Step 1: Creating profile for ${profile.username}...`);
      const profileResponse = await createUserProfile(currentUser.token, profile);
      
      if (profileResponse.status !== 201) {
          throw new Error("User profile creation failed with an unexpected status.");
      }
      
      alert(`Step 2: Creating credentials for ${auth.username}...`);
      await signUpAuth(currentUser.token, auth.username, auth.password, auth.role);

      alert(`Successfully created user ${profile.username} with role ${profile.role}.`);
      handleCloseNewUserModal();
      handleLoadUsers();
      
    } catch (err) {
      alert(`Failed to create user: ${err.message}. Data may be inconsistent.`);
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