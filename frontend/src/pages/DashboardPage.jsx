import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import SLABadge from '../components/SLABadge';
import { GitPullRequest, Plus, RefreshCw, Filter, Search } from 'lucide-react';

function PRCard({ pr }) {
  const [sla, setSla] = useState(null);

  useEffect(() => {
    if (pr.status === 'IN_REVIEW') {
      api.get(`/prs/${pr.id}/sla`).then(r => setSla(r.data)).catch(() => {});
    }
  }, [pr.id, pr.status]);

  return (
    <Link
      to={`/pr/${pr.id}`}
      className="glass-card p-4 block hover:border-brand-600/50 transition-all duration-200 hover:-translate-y-0.5 group"
    >
      <div className="flex items-start justify-between gap-3">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            <GitPullRequest size={15} className="text-brand-400 shrink-0" />
            <span className="text-sm font-semibold text-gray-200 truncate group-hover:text-brand-300 transition-colors">
              {pr.title}
            </span>
          </div>
          <p className="text-xs text-gray-500 truncate">{pr.description || 'No description'}</p>
          <div className="flex items-center gap-2 mt-2">
            <span className="text-xs text-gray-600">
              {pr.repository?.name && <span className="text-brand-500/80">{pr.repository.name}</span>}
            </span>
            <span className="text-gray-700">·</span>
            <span className="text-xs text-gray-600">by {pr.author?.name}</span>
            <span className="text-gray-700">·</span>
            <span className="text-xs text-gray-600">
              {new Date(pr.createdAt).toLocaleDateString('en-IN')}
            </span>
          </div>
        </div>
        <div className="flex flex-col items-end gap-2 shrink-0">
          <StatusBadge status={pr.status} />
          {sla && <SLABadge slaTracker={sla} />}
        </div>
      </div>
    </Link>
  );
}

export default function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [prs, setPrs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('');
  const [search, setSearch] = useState('');

  const loadPRs = async () => {
    setLoading(true);
    try {
      const params = statusFilter ? `?status=${statusFilter}` : '';
      const res = await api.get(`/prs${params}`);
      setPrs(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadPRs(); }, [statusFilter]);

  const filtered = prs.filter(pr =>
    pr.title?.toLowerCase().includes(search.toLowerCase()) ||
    pr.repository?.name?.toLowerCase().includes(search.toLowerCase())
  );

  const STATUS_OPTIONS = ['', 'DRAFT', 'OPEN', 'IN_REVIEW', 'CHANGES_REQUESTED', 'APPROVED', 'MERGED', 'CLOSED'];

  return (
    <div className="p-6 max-w-5xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-100">Dashboard</h1>
          <p className="text-gray-500 text-sm mt-1">Welcome back, {user?.name}</p>
        </div>
        <div className="flex gap-2">
          <button onClick={loadPRs} className="btn-secondary">
            <RefreshCw size={14} />
            Refresh
          </button>
          {user?.role === 'AUTHOR' && (
            <button onClick={() => navigate('/pr/new')} className="btn-primary">
              <Plus size={15} />
              New PR
            </button>
          )}
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-6">
        {['OPEN', 'IN_REVIEW', 'APPROVED', 'CHANGES_REQUESTED'].map(s => (
          <div key={s} className="glass-card p-4 text-center">
            <p className="text-2xl font-bold text-gray-100">{prs.filter(p => p.status === s).length}</p>
            <StatusBadge status={s} />
          </div>
        ))}
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 mb-4">
        <div className="relative flex-1 min-w-48">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500" />
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search PRs..."
            className="input-field pl-9"
          />
        </div>
        <div className="flex items-center gap-2">
          <Filter size={14} className="text-gray-500" />
          <select
            value={statusFilter}
            onChange={e => setStatusFilter(e.target.value)}
            className="input-field w-auto"
          >
            {STATUS_OPTIONS.map(s => (
              <option key={s} value={s}>{s || 'All Status'}</option>
            ))}
          </select>
        </div>
      </div>

      {/* PR list */}
      {loading ? (
        <div className="flex items-center justify-center py-16">
          <div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
        </div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-16 text-gray-600">
          <GitPullRequest size={40} className="mx-auto mb-3 opacity-30" />
          <p>No pull requests found.</p>
          {user?.role === 'AUTHOR' && (
            <button onClick={() => navigate('/pr/new')} className="btn-primary mt-4">
              <Plus size={14} /> Create your first PR
            </button>
          )}
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map(pr => <PRCard key={pr.id} pr={pr} />)}
        </div>
      )}
    </div>
  );
}
