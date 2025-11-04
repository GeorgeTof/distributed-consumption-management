import React from 'react';


function UserProfile({ data }) {
  const formattedDate = new Date(data.registerDate).toLocaleString();

  return (
    <div className="user-profile-card">
      <h4>My Profile</h4>
      <ul>
        <li><strong>Username:</strong> {data.username}</li>
        <li><strong>Email:</strong> {data.email}</li>
        <li><strong>Role:</strong> {data.role}</li>
        <li><strong>Age:</strong> {data.age || 'N/A'}</li>
        <li><strong>Town:</strong> {data.town || 'N/A'}</li>
        <li><strong>Registered:</strong> {formattedDate}</li>
      </ul>
    </div>
  );
}

export default UserProfile;
