import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Code2, Eye, EyeOff, UserPlus } from 'lucide-react';
import api from '../api/axios';

const ROLES = ['AUTHOR', 'REVIEWER', 'ADMIN'];

export default function RegisterPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'AUTHOR' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPass, setShowPass] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await api.post('/auth/register', form);
      login(res.data);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
      <div className="absolute top-1/4 right-1/3 w-80 h-80 bg-brand-600/10 rounded-full blur-3xl pointer-events-none" />

      <div className="w-full max-w-md animate-slide-up">
        <div className="text-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-brand-600 flex items-center justify-center mx-auto mb-4 glow-brand">
            <Code2 size={28} className="text-white" />
          </div>
          <h1 className="text-3xl font-bold text-gradient">CodeCollab</h1>
          <p className="text-gray-500 mt-1 text-sm">Create your account</p>
        </div>

        <div className="glass-card p-8">
          <h2 className="text-xl font-semibold text-gray-100 mb-6">Register</h2>

          {error && (
            <div className="bg-red-900/30 border border-red-700/50 text-red-300 text-sm rounded-lg px-4 py-3 mb-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm text-gray-400 mb-1.5" htmlFor="name">Full Name</label>
              <input
                id="name"
                type="text"
                required
                value={form.name}
                onChange={e => setForm({ ...form, name: e.target.value })}
                placeholder="John Doe"
                className="input-field"
              />
            </div>

            <div>
              <label className="block text-sm text-gray-400 mb-1.5" htmlFor="reg-email">Email</label>
              <input
                id="reg-email"
                type="email"
                required
                value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })}
                placeholder="you@example.com"
                className="input-field"
              />
            </div>

            <div>
              <label className="block text-sm text-gray-400 mb-1.5" htmlFor="reg-password">Password</label>
              <div className="relative">
                <input
                  id="reg-password"
                  type={showPass ? 'text' : 'password'}
                  required
                  minLength={6}
                  value={form.password}
                  onChange={e => setForm({ ...form, password: e.target.value })}
                  placeholder="Min. 6 characters"
                  className="input-field pr-10"
                />
                <button
                  type="button"
                  onClick={() => setShowPass(!showPass)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300"
                >
                  {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            <div>
              <label className="block text-sm text-gray-400 mb-1.5" htmlFor="role">Role</label>
              <select
                id="role"
                value={form.role}
                onChange={e => setForm({ ...form, role: e.target.value })}
                className="input-field"
              >
                {ROLES.map(r => (
                  <option key={r} value={r}>{r.charAt(0) + r.slice(1).toLowerCase()}</option>
                ))}
              </select>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full justify-center py-2.5 mt-2"
            >
              <UserPlus size={16} />
              {loading ? 'Creating account...' : 'Create Account'}
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-6">
            Already have an account?{' '}
            <Link to="/login" className="text-brand-400 hover:text-brand-300 font-medium">
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
