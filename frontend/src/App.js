import React, { useState } from 'react';
import './App.css';
import { login } from './api';

function App() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);

    try {
      const userData = await login(username, password);

      setLoading(false);
      console.log('Login Successful!', userData);
      
      alert(`Welcome, ${userData.user}! Roles: ${userData.roles}`);

    } catch (err) {
      setLoading(false);
      alert(err.message); 
    }
  };

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

export default App;