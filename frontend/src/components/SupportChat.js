import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import './SupportChat.css';

function SupportChat({ currentUser, onClose }) {
  const [messages, setMessages] = useState([
    { sender: 'Support Bot', content: 'Hello! Ask me about consumption or devices or anything.', type: 'other' }
  ]);
  const [inputText, setInputText] = useState('');
  const clientRef = useRef(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8082/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('Support Chat Connected');

        client.subscribe(`/topic/support/${currentUser.user}`, (message) => {
          const body = JSON.parse(message.body);
          addMessage(body.sender, body.content, 'other');
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      console.log('Disconnecting Support Chat...');
      client.deactivate();
    };
  }, [currentUser.user]);

  const addMessage = (sender, content, type) => {
    setMessages((prev) => [...prev, { sender, content, type }]);
  };

  const handleSend = (e) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    addMessage('Me', inputText, 'own');

    if (clientRef.current && clientRef.current.connected) {
      const payload = {
        sender: currentUser.user,
        content: inputText,
        role: currentUser.roles.includes('ROLE_ADMIN') ? 'ADMIN' : 'USER',
        timestamp: new Date().toISOString()
      };
      
      clientRef.current.publish({
        destination: '/app/support',
        body: JSON.stringify(payload)
      });
    } else {
      alert("Chat is not connected!");
    }

    setInputText('');
  };

  return (
    <div className="chat-window">
      <div className="chat-header">
        <span>Support Chat</span>
        <button className="close-btn" onClick={onClose}>&times;</button>
      </div>
      
      <div className="chat-messages">
        {messages.map((msg, index) => (
          <div key={index} className={`message ${msg.type}`}>
            <strong>{msg.sender !== 'Me' && msg.sender !== currentUser.user ? '' : ''}</strong>
            {msg.content}
          </div>
        ))}
      </div>

      <form className="chat-input-area" onSubmit={handleSend}>
        <input 
          type="text" 
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="Type a message..."
        />
        <button type="submit">Send</button>
      </form>
    </div>
  );
}

export default SupportChat;