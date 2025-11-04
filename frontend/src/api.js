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
