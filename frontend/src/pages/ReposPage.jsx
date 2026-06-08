import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { BookOpen, Plus, Code, GitBranch, Calendar } from 'lucide-react';

const LANGUAGE_COLORS = {
  JavaScript: '#f7df1e', TypeScript: '#3178c6', Python: '#3572a5',
  Java: '#b07219', Go: '#00add8', Rust: '#dea584', 'C++': '#f34b7d',
  Ruby: '#701516', PHP: '#4f5d95', default: '#6e40c9'
};

function RepoCard({ repo }) {
  const langColor = LANGUAGE_COLORS[repo.language] || LANGUAGE_COLORS.default;
  return (
    <Link
      to={`/repos/${repo.id}`}
      className="glass-card p-5 block hover:border-brand-600/50 transition-all duration-200 hover:-translate-y-1 group"
    >
      <div className="flex items-start gap-3">
        <div className="w-9 h-9 rounded-lg bg-dark-600 flex items-center justify-center shrink-0">
          <BookOpen size={18} className="text-brand-400" />
        </div>
        <div className="flex-1 min-w-0">
          <h3 className="font-semibold text-gray-200 group-hover:text-brand-300 transition-colors truncate">
            {repo.name}
          </h3>
          <p className="text-xs text-gray-500 truncate mt-0.5">{repo.description || 'No description'}</p>
          <div className="flex items-center gap-3 mt-3">
            <span className="flex items-center gap-1.5 text-xs text-gray-500">
              <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: langColor }} />
              {repo.language}
            </span>
            <span className="flex items-center gap-1 text-xs text-gray-600">
              <Calendar size={11} />
              {new Date(repo.createdAt).toLocaleDateString('en-IN')}
            </span>
          </div>
        </div>
      </div>
    </Link>
  );
}

export default function ReposPage() {
  const { user } = useAuth();
  const [repos, setRepos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: '', description: '', language: 'JavaScript' });
  const [creating, setCreating] = useState(false);

  const LANGUAGES = ['JavaScript', 'TypeScript', 'Python', 'Java', 'Go', 'Rust', 'C++', 'Ruby', 'PHP', 'Other'];

  const loadRepos = async () => {
    setLoading(true);
    try {
      const res = await api.get('/repos');
      setRepos(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadRepos(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setCreating(true);
    try {
      await api.post('/repos', form);
      setForm({ name: '', description: '', language: 'JavaScript' });
      setShowForm(false);
      loadRepos();
    } catch (err) {
      console.error(err);
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-100">Repositories</h1>
          <p className="text-gray-500 text-sm mt-1">{repos.length} repositories</p>
        </div>
        {user?.role === 'AUTHOR' && (
          <button onClick={() => setShowForm(!showForm)} className="btn-primary">
            <Plus size={15} />
            New Repository
          </button>
        )}
      </div>

      {/* Create form */}
      {showForm && (
        <div className="glass-card p-5 mb-6 animate-slide-up">
          <h3 className="text-lg font-semibold text-gray-100 mb-4">Create Repository</h3>
          <form onSubmit={handleCreate} className="space-y-3">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs text-gray-400 mb-1">Name *</label>
                <input
                  type="text"
                  required
                  value={form.name}
                  onChange={e => setForm({ ...form, name: e.target.value })}
                  placeholder="my-awesome-repo"
                  className="input-field"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-400 mb-1">Language *</label>
                <select
                  value={form.language}
                  onChange={e => setForm({ ...form, language: e.target.value })}
                  className="input-field"
                >
                  {LANGUAGES.map(l => <option key={l}>{l}</option>)}
                </select>
              </div>
            </div>
            <div>
              <label className="block text-xs text-gray-400 mb-1">Description</label>
              <input
                type="text"
                value={form.description}
                onChange={e => setForm({ ...form, description: e.target.value })}
                placeholder="Short description..."
                className="input-field"
              />
            </div>
            <div className="flex gap-2">
              <button type="submit" disabled={creating} className="btn-primary">
                {creating ? 'Creating...' : 'Create Repository'}
              </button>
              <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {loading ? (
        <div className="flex items-center justify-center py-16">
          <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
        </div>
      ) : repos.length === 0 ? (
        <div className="text-center py-16 text-gray-600">
          <BookOpen size={40} className="mx-auto mb-3 opacity-30" />
          <p>No repositories yet.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {repos.map(repo => <RepoCard key={repo.id} repo={repo} />)}
        </div>
      )}
    </div>
  );
}
