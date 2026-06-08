import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { Link } from 'react-router-dom';
import {
  Shield, GitPullRequest, Users, AlertTriangle, Clock,
  BarChart2, RefreshCw, XCircle, UserCheck
} from 'lucide-react';

function StatCard({ icon: Icon, label, value, color = 'text-brand-400' }) {
  return (
    <div className="glass-card p-5 text-center">
      <Icon size={24} className={`${color} mx-auto mb-2`} />
      <p className="text-3xl font-bold text-gray-100">{value}</p>
      <p className="text-xs text-gray-500 mt-1">{label}</p>
    </div>
  );
}

export default function AdminDashboardPage() {
  const [prs, setPrs] = useState([]);
  const [breachedSLAs, setBreachedSLAs] = useState([]);
  const [workload, setWorkload] = useState({});
  const [filterStatus, setFilterStatus] = useState('');
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    setLoading(true);
    try {
      const [prsRes, slaRes, workloadRes] = await Promise.all([
        api.get(`/admin/prs${filterStatus ? `?status=${filterStatus}` : ''}`),
        api.get('/admin/sla/breached'),
        api.get('/admin/workload'),
      ]);
      setPrs(prsRes.data);
      setBreachedSLAs(slaRes.data);
      setWorkload(workloadRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, [filterStatus]);

  const handleForceClose = async (prId) => {
    if (!window.confirm('Force close this PR?')) return;
    try {
      await api.patch(`/admin/prs/${prId}/status`, { status: 'CLOSED' });
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed');
    }
  };

  const STATUS_OPTIONS = ['', 'DRAFT', 'OPEN', 'IN_REVIEW', 'CHANGES_REQUESTED', 'APPROVED', 'MERGED', 'CLOSED'];

  return (
    <div className="p-6 max-w-6xl mx-auto animate-fade-in">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-100 flex items-center gap-2">
            <Shield size={22} className="text-brand-400" /> Admin Dashboard
          </h1>
          <p className="text-gray-500 text-sm mt-1">System overview and management</p>
        </div>
        <button onClick={loadData} className="btn-secondary">
          <RefreshCw size={14} /> Refresh
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        <StatCard icon={GitPullRequest} label="Total PRs" value={prs.length} />
        <StatCard icon={AlertTriangle} label="SLA Breached" value={breachedSLAs.length} color="text-red-400" />
        <StatCard icon={Clock} label="In Review" value={prs.filter(p => p.status === 'IN_REVIEW').length} color="text-blue-400" />
        <StatCard icon={Users} label="Reviewers" value={Object.keys(workload).length} color="text-purple-400" />
      </div>

      {/* Reviewer Workload */}
      <div className="glass-card p-5 mb-6">
        <h2 className="text-base font-semibold text-gray-200 mb-4 flex items-center gap-2">
          <BarChart2 size={16} className="text-brand-400" /> Reviewer Workload
        </h2>
        {Object.keys(workload).length === 0 ? (
          <p className="text-gray-500 text-sm">No active reviewers.</p>
        ) : (
          <div className="space-y-3">
            {Object.entries(workload).map(([name, count]) => (
              <div key={name} className="flex items-center gap-3">
                <div className="w-7 h-7 rounded-full bg-brand-800 flex items-center justify-center text-xs font-bold text-brand-300 shrink-0">
                  {name.charAt(0)}
                </div>
                <span className="text-sm text-gray-300 w-36 truncate">{name}</span>
                <div className="flex-1 h-2 bg-dark-600 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-brand-500 rounded-full transition-all duration-500"
                    style={{ width: `${Math.min((count / 10) * 100, 100)}%` }}
                  />
                </div>
                <span className="text-xs text-gray-400 w-12 text-right">{count} active</span>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* SLA Breaches */}
      {breachedSLAs.length > 0 && (
        <div className="glass-card p-5 mb-6 border-red-700/30">
          <h2 className="text-base font-semibold text-red-400 mb-3 flex items-center gap-2">
            <AlertTriangle size={16} /> SLA Breach Report ({breachedSLAs.length})
          </h2>
          <div className="space-y-2">
            {breachedSLAs.map(sla => (
              <div key={sla.id} className="flex items-center justify-between p-3 bg-red-900/20 border border-red-700/30 rounded-lg">
                <div>
                  <Link
                    to={`/pr/${sla.pullRequest?.id}`}
                    className="text-sm font-medium text-gray-200 hover:text-brand-300"
                  >
                    PR #{sla.pullRequest?.id} — {sla.pullRequest?.title}
                  </Link>
                  <p className="text-xs text-gray-500">
                    Deadline: {new Date(sla.deadline).toLocaleString('en-IN')}
                    {sla.escalatedAt && ` · Escalated: ${new Date(sla.escalatedAt).toLocaleString('en-IN')}`}
                  </p>
                </div>
                <span className={sla.escalatedAt ? 'sla-escalated' : 'sla-breached'}>
                  {sla.escalatedAt ? 'Escalated' : 'Breached'}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* All PRs */}
      <div className="glass-card p-5">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-base font-semibold text-gray-200 flex items-center gap-2">
            <GitPullRequest size={16} className="text-brand-400" /> All Pull Requests
          </h2>
          <select
            value={filterStatus}
            onChange={e => setFilterStatus(e.target.value)}
            className="input-field w-auto text-xs"
          >
            {STATUS_OPTIONS.map(s => <option key={s} value={s}>{s || 'All Status'}</option>)}
          </select>
        </div>

        {loading ? (
          <div className="flex items-center justify-center py-8">
            <div className="w-7 h-7 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" />
          </div>
        ) : prs.length === 0 ? (
          <p className="text-gray-600 text-sm text-center py-8">No PRs found.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-xs text-gray-500 border-b border-dark-500">
                  <th className="pb-2 pr-4">PR</th>
                  <th className="pb-2 pr-4">Repository</th>
                  <th className="pb-2 pr-4">Author</th>
                  <th className="pb-2 pr-4">Status</th>
                  <th className="pb-2 pr-4">Created</th>
                  <th className="pb-2">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-dark-600">
                {prs.map(pr => (
                  <tr key={pr.id} className="hover:bg-dark-700/30 transition-colors">
                    <td className="py-3 pr-4">
                      <Link to={`/pr/${pr.id}`} className="text-brand-400 hover:text-brand-300 font-medium">
                        #{pr.id} {pr.title?.substring(0, 30)}{pr.title?.length > 30 ? '...' : ''}
                      </Link>
                    </td>
                    <td className="py-3 pr-4 text-gray-400 text-xs">{pr.repository?.name}</td>
                    <td className="py-3 pr-4 text-gray-400 text-xs">{pr.author?.name}</td>
                    <td className="py-3 pr-4"><StatusBadge status={pr.status} /></td>
                    <td className="py-3 pr-4 text-gray-500 text-xs">
                      {new Date(pr.createdAt).toLocaleDateString('en-IN')}
                    </td>
                    <td className="py-3">
                      <button
                        onClick={() => handleForceClose(pr.id)}
                        disabled={pr.status === 'CLOSED'}
                        className="text-red-500 hover:text-red-400 disabled:opacity-30 transition-colors"
                        title="Force Close"
                      >
                        <XCircle size={15} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
