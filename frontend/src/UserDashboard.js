import React, { useState } from 'react'; 
import { getMyProfile, getOwnDevices, getDeviceHistory } from './api'; 
import UserProfile from './components/UserProfile'; 
import DeviceCard from './components/DeviceCard'; 
import EnergyConsumptionModal from './components/EnergyConsumptionModal';
// Recharts components
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer 
} from 'recharts';

function UserDashboard({ currentUser }) {
  const [profile, setProfile] = useState(null);
  const [devices, setDevices] = useState(null);
  const [chartData, setChartData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [isEnergyModalOpen, setIsEnergyModalOpen] = useState(false);
  const [selectedDateDisplay, setSelectedDateDisplay] = useState('');

  const handleLoadProfile = async () => {
    setLoading(true);
    setError(null);
    setDevices(null);
    setChartData(null);
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
    setChartData(null);
    try {
      const response = await getOwnDevices(currentUser.token);
      setDevices(response.data); 
    } catch (err) {
      setError(err.message);
      alert(err.message);
    }
    setLoading(false);
  };

  const handleOpenEnergyModal = () => {
    setIsEnergyModalOpen(true);
  };

  const handleCloseEnergyModal = () => {
    setIsEnergyModalOpen(false);
  };

  const handleEnergyDateSubmit = async (dateStr) => {
    // input type="date" as "YYYY-MM-DD"
    setIsEnergyModalOpen(false);
    
    const [yearStr, monthStr, dayStr] = dateStr.split('-');
    const displayDate = `${dayStr}/${monthStr}/${yearStr}`;
    setSelectedDateDisplay(displayDate);

    setLoading(true);
    setProfile(null); 
    setDevices(null);
    setChartData(null);

    try {
      const year = parseInt(yearStr, 10);
      const month = parseInt(monthStr, 10);
      const day = parseInt(dayStr, 10);

      const devicesResponse = await getOwnDevices(currentUser.token);
      const myDevices = devicesResponse.data;

      if (!myDevices || myDevices.length === 0) {
        alert("You have no devices to monitor.");
        setLoading(false);
        return;
      }

      const historyPromises = myDevices.map(device => 
        getDeviceHistory(currentUser.token, device.id, year, month, day)
          .then(res => res.data)
          .catch(err => {
             console.error(`Failed to fetch history for device ${device.id}`, err);
             return []; // Return empty array on error so Promise.all doesn't fail
          })
      );

      const allHistories = await Promise.all(historyPromises);

      const hourlyTotals = Array.from({ length: 24 }, (_, i) => ({
        hour: i,
        totalConsumption: 0
      }));

      allHistories.forEach(deviceHistory => {
        deviceHistory.forEach(record => {
          if (record.hour >= 0 && record.hour < 24) {
            hourlyTotals[record.hour].totalConsumption += record.measurement;
          }
        });
      });

      setChartData(hourlyTotals);

    } catch (err) {
      console.error(err);
      alert("Failed to load energy data: " + err.message);
    } finally {
      setLoading(false);
    }
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
        <button onClick={handleOpenEnergyModal} disabled={loading} style={{ marginLeft: '10px' }}>
          Show My Energy Consumption
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

        {chartData && (
          <div className="chart-container" style={{ width: '100%', height: 400, marginTop: '20px' }}>
            <h4>Total Energy Consumption for {selectedDateDisplay}</h4>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                data={chartData}
                margin={{
                  top: 5,
                  right: 30,
                  left: 20,
                  bottom: 5,
                }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="hour" label={{ value: 'Hour (0-23)', position: 'insideBottomRight', offset: -5 }} />
                <YAxis label={{ value: 'Energy (kWh)', angle: -90, position: 'insideLeft' }} />
                <Tooltip />
                <Legend />
                <Bar dataKey="totalConsumption" name="Total Consumption (kWh)" fill="#8884d8" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}
      </div>

      <EnergyConsumptionModal 
        show={isEnergyModalOpen} 
        onClose={handleCloseEnergyModal} 
        onSubmit={handleEnergyDateSubmit} 
      />
    </div>
  );
}

export default UserDashboard;