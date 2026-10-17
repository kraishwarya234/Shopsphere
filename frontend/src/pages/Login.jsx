import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api, { errMsg } from '../api.js';
import { useAuth } from '../auth.jsx';

export default function Login() {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const { data } = await api.post(`/auth/${mode}`, form);
      login(data);
      navigate('/');
    } catch (err) {
      setError(errMsg(err));
    }
  };

  return (
    <form className="panel narrow" onSubmit={submit}>
      <h2>{mode === 'login' ? 'Log in' : 'Create your account'}</h2>
      {mode === 'register' && <input placeholder="Full name" value={form.name} onChange={set('name')} required />}
      <input type="email" placeholder="Email" value={form.email} onChange={set('email')} required />
      <input type="password" placeholder="Password (min 6 characters)" value={form.password} onChange={set('password')} required />
      {error && <p className="error">{error}</p>}
      <button className="btn">{mode === 'login' ? 'Log in' : 'Sign up'}</button>
      <button type="button" className="link" onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
        {mode === 'login' ? 'New here? Create an account' : 'Already registered? Log in'}
      </button>
    </form>
  );
}
