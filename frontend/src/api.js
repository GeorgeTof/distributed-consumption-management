export async function login(username, password) {
  // backend expects x-www-form-urlencoded data
  const body = new URLSearchParams();
  body.append('username', username);
  body.append('password', password);

  const response = await fetch('/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: body.toString(),
  });

  if (response.ok) {
    return response.json(); 
  }

  try {
    const errorData = await response.json();
    throw new Error(errorData.error || 'Invalid username or password'); 
  } catch (e) {
    throw new Error('Invalid username or password');
  }
}

async function authFetch(url, token, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (response.ok) {
    if (response.status === 204) {
      return { data: null, status: response.status };
    }
    const data = await response.json();
    return { data: data, status: response.status };
  }

  try {
    const errorData = await response.json();
    throw new Error(errorData.error || 'API request failed');
  } catch (e) {
    throw new Error('API request failed');
  }
}

export async function getMyProfile(token) {
  return authFetch('/users/profile', token);
}

export async function getOwnDevices(token) {
  return authFetch('/devices', token);
}

export async function getAllDevices(token) {
  return authFetch('/devices/all', token);
}

export async function deleteDevice(token, deviceId) {
  return authFetch(`/devices/${deviceId}`, token, {
    method: 'DELETE',
  });
}

export async function updateDeviceConsumption(token, deviceId, powerConsumed) {
  const url = `/devices/${deviceId}/consumption?powerConsumed=${powerConsumed}`;
  
  return authFetch(url, token, {
    method: 'PATCH',
  });
}