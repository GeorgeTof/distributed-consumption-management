import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import './CommunityChat.css';
import './SupportChat.css';

function CommunityChat({ currentUser, onClose }) {
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const clientRef = useRef(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8082/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('Community Chat Connected');

        client.subscribe('/topic/chat', (message) => {
          const body = JSON.parse(message.body);
          
          if (body.sender === currentUser.user) {
            return;
          }

          addMessage(body.sender, body.content, body.role, 'other');
        });
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [currentUser.user]);

  const addMessage = (sender, content, role, type) => {
    setMessages((prev) => [...prev, { sender, content, role, type }]);
  };

  const handleSend = (e) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    addMessage('Me', inputText, currentUser.roles[0], 'own'); 

    if (clientRef.current && clientRef.current.connected) {
      const payload = {
        sender: currentUser.user,
        content: inputText,
        role: currentUser.roles.includes('ROLE_ADMIN') ? 'ADMIN' : 'USER'
      };
      
      clientRef.current.publish({
        destination: '/app/chat',
        body: JSON.stringify(payload)
      });
    }

    setInputText('');
  };

  return (
    <div className="community-chat-window">
      <div className="community-header">
        <span>Community Chat</span>
        <button className="close-btn" onClick={onClose}>&times;</button>
      </div>
      
      <div className="chat-messages">
        {messages.map((msg, index) => {
          const roleClass = msg.role === 'ADMIN' ? 'admin' : 'user';
          const rowClass = msg.type === 'own' ? 'own' : `other ${roleClass}`;

          return (
            <div key={index} className={`message-row ${rowClass}`}>
              {msg.type !== 'own' && (
                <span className="sender-name">{msg.sender}</span>
              )}
              <div className="message-bubble">
                {msg.content}
              </div>
            </div>
          );
        })}
      </div>

      <form className="chat-input-area" onSubmit={handleSend}>
        <input 
          type="text" 
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder="Say hello..."
        />
        <button type="submit" style={{backgroundColor: '#28a745'}}>Send</button>
      </form>
    </div>
  );
}

export default CommunityChat;