import React, { useState } from 'react';
import './AddDeviceModal.css';

function AddUserModal({ show, onClose, onSubmit }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [age, setAge] = useState('');
  const [town, setTown] = useState('');
  const [role, setRole] = useState('USER');
  const [error, setError] = useState('');

  if (!show) {
    return null;
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!username || !password || !email || !age || !town) {
      setError('All fields are required.');
      return;
    }
    
    if (password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }
    if (!email.includes('@')) {
      setError('Invalid email format.');
      return;
    }
    const parsedAge = parseInt(age);
    if (isNaN(parsedAge) || parsedAge < 0) {
      setError('Age must be a non-negative number.');
      return;
    }

    const userData = {
      profile: {
        username: username,
        email: email,
        age: parsedAge,
        town: town,
        role: role, 
      },
      auth: {
        username: username,
        password: password,
        role: role,
      }
    };
    
    onSubmit(userData);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h4>Create New User</h4>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label htmlFor="new-username">Username</label>
            <input
              type="text"
              id="new-username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="new-password">Password (min 6 chars)</label>
            <input
              type="password"
              id="new-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="new-email">Email</label>
            <input
              type="email"
              id="new-email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="new-age">Age</label>
            <input
              type="number"
              id="new-age"
              value={age}
              onChange={(e) => setAge(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="new-town">Town</label>
            <input
              type="text"
              id="new-town"
              value={town}
              onChange={(e) => setTown(e.target.value)}
            />
          </div>
          <div className="input-group">
            <label htmlFor="new-role">Role</label>
            <select id="new-role" value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          {error && <p className="error-message">{error}</p>}
          <div className="modal-buttons">
            <button type="submit">Create User</button>
            <button type="button" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default AddUserModal;