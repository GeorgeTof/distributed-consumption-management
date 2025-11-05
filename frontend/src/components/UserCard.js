import React from 'react';

function UserCard({ user, onUpdate, onAddDevice, onDelete }) {
  const formattedDate = new Date(user.registerDate).toLocaleString();

  const handleUpdate = () => {
    const newEmail = prompt(
      `Update email for ${user.username}:`, 
      user.email
    );
    
    if (newEmail !== null && newEmail.trim() !== '') {
      if (newEmail.includes('@') && newEmail.includes('.')) {
        onUpdate(user.id, newEmail);
      } else {
        alert("Invalid email format. Please try again.");
      }
    }
  };

  const handleAddDevice = () => {
    onAddDevice(user);
  };

  const handleDelete = () => {
    onDelete(user);
  };

  return (
    <div className="user-profile-card">
      <h5>{user.username}</h5>
      <ul>
        <li><strong>ID:</strong> {user.id}</li>
        <li><strong>Email:</strong> {user.email}</li>
        <li><strong>Role:</strong> {user.role}</li>
        <li><strong>Age:</strong> {user.age || 'N/A'}</li>
        <li><strong>Town:</strong> {user.town || 'N/A'}</li>
        <li><strong>Registered:</strong> {formattedDate}</li>
      </ul>
      <div className="admin-controls">
        <button className="btn-update" onClick={handleUpdate}>Update</button>
        <button className="btn-add" onClick={handleAddDevice}>Add Device</button>
        <button className="btn-delete" onClick={handleDelete}>Delete</button>
      </div>
    </div>
  );
}

export default UserCard;