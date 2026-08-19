import React, { useState, useEffect, useRef } from 'react';
import { 
  RiMessage3Line, 
  RiSendPlane2Line, 
  RiHeartFill, 
  RiHeartLine, 
  RiUserLine, 
  RiTimeLine 
} from 'react-icons/ri';

const StaffNotes = ({ user, axiosInstance }) => {
  const [notes, setNotes] = useState([]);
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const messageEndRef = useRef(null);

  const fetchNotes = async () => {
    try {
      const res = await axiosInstance.get('/api/notes');
      setNotes(res.data);
    } catch (err) {
      console.error(err);
      setError('Failed to fetch staff notes.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (axiosInstance) {
      fetchNotes();
      // Poll for new messages every 5 seconds to feel live
      const interval = setInterval(fetchNotes, 5000);
      return () => clearInterval(interval);
    }
  }, [axiosInstance]);

  // Scroll to bottom on load/new note
  useEffect(() => {
    messageEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [notes]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!content.trim()) return;

    setSubmitting(true);
    setError('');
    try {
      const res = await axiosInstance.post('/api/notes', { content });
      setNotes([res.data, ...notes]);
      setContent('');
    } catch (err) {
      console.error(err);
      setError('Failed to send note.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleLike = async (id) => {
    try {
      const res = await axiosInstance.post(`/api/notes/${id}/like`);
      // Update local state
      setNotes(notes.map(note => note.id === id ? res.data : note));
    } catch (err) {
      console.error(err);
    }
  };

  const formatTime = (timeStr) => {
    if (!timeStr) return '';
    try {
      const date = new Date(timeStr);
      return date.toLocaleDateString(undefined, { 
        month: 'short', 
        day: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    } catch (e) {
      return timeStr;
    }
  };

  return (
    <div className="glass-panel p-4 fade-in-up d-flex flex-column" style={{ minHeight: '75vh', maxHeight: '80vh' }}>
      {/* Header */}
      <div className="d-flex align-items-center justify-content-between mb-3 border-bottom pb-3">
        <div className="d-flex align-items-center gap-2">
          <div className="bg-primary bg-opacity-10 text-primary p-2 rounded-3">
            <RiMessage3Line size={24} />
          </div>
          <div>
            <h4 className="fw-bold mb-0">Staff Communication Board</h4>
            <small className="text-muted">Exchange notes between Dentists and Receptionists</small>
          </div>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger py-2 px-3 mb-3 rounded-3" style={{ fontSize: '0.85rem' }}>
          {error}
        </div>
      )}

      {/* Messages List Container */}
      <div className="flex-grow-1 overflow-y-auto mb-3 pe-2" style={{ maxHeight: '48vh' }}>
        {loading && notes.length === 0 ? (
          <div className="d-flex justify-content-center align-items-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading messages...</span>
            </div>
          </div>
        ) : notes.length === 0 ? (
          <div className="text-center py-5 text-muted">
            <RiMessage3Line size={48} className="mb-2 opacity-50" />
            <p className="mb-0">No notes posted yet. Start the conversation!</p>
          </div>
        ) : (
          <div className="d-flex flex-column gap-3">
            {/* Render in reverse order (bottom is newest, so reverse the desc order array for display) */}
            {[...notes].reverse().map((note) => {
              const isOwnMessage = note.senderUsername === user?.username;
              const isDentistSender = note.senderRole === 'DENTIST';
              
              return (
                <div 
                  key={note.id} 
                  className={`d-flex flex-column ${isOwnMessage ? 'align-items-end' : 'align-items-start'}`}
                >
                  <div 
                    className={`glass-card p-3 border ${
                      isOwnMessage 
                        ? 'border-primary border-opacity-20 bg-primary bg-opacity-5' 
                        : 'border-secondary-subtle'
                    }`}
                    style={{ maxWidth: '75%', borderRadius: '12px' }}
                  >
                    {/* Sender & Role */}
                    <div className="d-flex align-items-center justify-content-between gap-4 mb-1.5 border-bottom pb-1 border-secondary-subtle">
                      <span className="fw-bold text-primary" style={{ fontSize: '0.82rem' }}>
                        <RiUserLine className="me-1" />
                        {note.senderFullname} ({note.senderRole})
                      </span>
                      <span className="text-muted d-flex align-items-center gap-1" style={{ fontSize: '0.72rem' }}>
                        <RiTimeLine />
                        {formatTime(note.createdAt)}
                      </span>
                    </div>

                    {/* Note Content */}
                    <p className="mb-2 text-start text-dark" style={{ fontSize: '0.9rem', whiteSpace: 'pre-wrap', lineHeight: '1.4' }}>
                      {note.content}
                    </p>

                    {/* Like / Actions */}
                    <div className="d-flex align-items-center justify-content-end">
                      <button 
                        onClick={() => handleToggleLike(note.id)}
                        className={`btn btn-link p-0 text-decoration-none d-flex align-items-center gap-1 ${
                          note.liked ? 'text-danger' : 'text-muted'
                        }`}
                        style={{ fontSize: '0.78rem' }}
                      >
                        {note.liked ? <RiHeartFill size={16} /> : <RiHeartLine size={16} />}
                        <span>{note.liked ? 'Liked' : 'Like'}</span>
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
            <div ref={messageEndRef} />
          </div>
        )}
      </div>

      {/* Input Message Form */}
      <form onSubmit={handleSend} className="mt-auto border-top pt-3">
        <div className="input-group">
          <textarea
            className="form-control form-control-premium"
            placeholder={`Type a note to send to the ${user?.role === 'DENTIST' ? 'receptionists' : 'dentists'}...`}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                handleSend(e);
              }
            }}
            rows="2"
            disabled={submitting}
            style={{ borderTopLeftRadius: '10px', borderBottomLeftRadius: '10px', fontSize: '0.9rem' }}
          />
          <button 
            type="submit" 
            className="btn btn-premium-primary d-flex align-items-center justify-content-center px-4"
            disabled={submitting || !content.trim()}
            style={{ borderTopRightRadius: '10px', borderBottomRightRadius: '10px' }}
          >
            {submitting ? (
              <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            ) : (
              <RiSendPlane2Line size={20} />
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

export default StaffNotes;
