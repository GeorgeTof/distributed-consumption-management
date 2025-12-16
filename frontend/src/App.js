import React, { useState, useEffect } from 'react'; 
import './App.css';
import { login } from './api';
import AdminDashboard from './AdminDashboard';
import UserDashboard from './UserDashboard';

import { Client } from '@stomp/stompjs'; 
import SockJS from 'sockjs-client';

const USER_DATA_KEY = 'my-app-user-data';

function getSavedUserData() {
  const savedData = localStorage.getItem(USER_DATA_KEY);
  if (savedData) {
    try {
      return JSON.parse(savedData);
    } catch (e) {
      localStorage.removeItem(USER_DATA_KEY);
    }
  }
  return null;
}

function App() {
  const [currentUser, setCurrentUser] = useState(getSavedUserData());
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
      if (!currentUser) return;

      console.log('[DEBUG] Starting WebSocket connection for:', currentUser.user);

      const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8082/ws'),

        onConnect: () => {
        console.log('Connected to WebSocket');
        
        client.subscribe(`/topic/client/${currentUser.user}`, (message) => {
          try {
            const data = JSON.parse(message.body);
            
            const alertText = 
              `⚠️ ENERGY CONSUMPTION OVER LIMIT ALERT ⚠️\n\n` +
              `Device ID: ${data.device_id || 'Unknown'}\n` +
              `Measurement: ${data.measurement_value ?? 'N/A'} W\n` + 
              `Max consumption: ${data.max_consumption ?? 'N/A'} W\n` + 
              `Time: ${data.timestamp ? new Date(data.timestamp).toLocaleString() : 'Just now'}\n\n` +
              `Please contact an administrator!`;

            alert(alertText);

          } catch (e) {
            alert(`Energy Alert:\n\n${message.body}`);
          }
        });
      },
        onDisconnect: () => {
          console.log('Disconnected');
        },
        debug: (str) => {
          console.log(str);
        },
        reconnectDelay: 5000,
      });

      client.activate();

      return () => {
        client.deactivate();
      };
  }, [currentUser]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const userData = await login(username, password);
      localStorage.setItem(USER_DATA_KEY, JSON.stringify(userData));
      setCurrentUser(userData);
      setUsername('');
      setPassword('');
    } catch (err) {
      alert(err.message);
    }
    setLoading(false);
  };

  const handleLogout = () => {
    localStorage.removeItem(USER_DATA_KEY);
    setCurrentUser(null);
  };

  if (!currentUser) {
    return (
      <div className="App">
        <div className="login-container">
          <form onSubmit={handleSubmit}>
            <h2>Login</h2>
            <div className="input-group">
              <label htmlFor="username">Username</label>
              <input
                type="text"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="input-group">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button type="submit" disabled={loading}>
              {loading ? 'Loading...' : 'Login'}
            </button>
          </form>
        </div>
      </div>
    );
  }

  const isAdmin = currentUser.roles.includes('ROLE_ADMIN');

  return (
    <div className="App">
      <div className="dashboard-container">
        <h2>Welcome, {currentUser.user}!</h2>
        <p>Your role: {isAdmin ? 'Admin' : 'User'}</p>
        <button onClick={handleLogout}>
          Logout
        </button>
        <hr />
        
        <UserDashboard currentUser={currentUser} />
        {isAdmin && <AdminDashboard currentUser={currentUser} />}
      </div>
    </div>
  );
}

export default App;