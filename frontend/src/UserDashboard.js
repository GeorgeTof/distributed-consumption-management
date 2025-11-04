import React, { useState } from 'react'; 
import { getMyProfile, getOwnDevices } from './api'; 
import UserProfile from './components/UserProfile'; 
import DeviceCard from './components/DeviceCard'; 

function UserDashboard({ currentUser }) {
  const [profile, setProfile] = useState(null);
  const [devices, setDevices] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleLoadProfile = async () => {
    setLoading(true);
    setError(null);
    setDevices(null); 
    try {
      const response = await getMyProfile(currentUser.token);
      setProfile(response.data); 
    } catch (err) {
      setError(err.message);
      alert(err.message); 
    }
    setLoading(false);
  };

  const handleLoadDevices = async () => {
    setLoading(true);
    setError(null);
    setProfile(null); 
    try {
      const response = await getOwnDevices(currentUser.token);
      setDevices(response.data); 
    } catch (err) {
      setError(err.message);
      alert(err.message);
    }
    setLoading(false);
  };

  return (
    <div className="user-dashboard">
      <h3>User Dashboard (My Data)</h3>
      <p>All logged-in users can see this.</p>

      <div className="dashboard-buttons">
        <button onClick={handleLoadProfile} disabled={loading}>
          Load My Profile
        </button>
        <button onClick={handleLoadDevices} disabled={loading}>
          Load My Devices
        </button>
      </div>

      {loading && <p>Loading...</p>}

      <div className="data-display-area">
        {profile && <UserProfile data={profile} />}

        {devices && (
          <div className="device-list">
            <h4>My Devices</h4>
            {devices.length > 0 ? (
              devices.map(device => (
                <DeviceCard 
                  key={device.id} 
                  device={device} 
                  showAdminControls={false}
                />
              ))
            ) : (
              <p>You have no devices assigned.</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default UserDashboard;

