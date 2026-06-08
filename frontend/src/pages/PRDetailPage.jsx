import React, { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import SLABadge from '../components/SLABadge';
import DiffViewer from '../components/DiffViewer';
import AutoReviewPanel from '../components/AutoReviewPanel';
import AuditTimeline from '../components/AuditTimeline';
import CommentThread from '../components/CommentThread';
import {
  ArrowLeft, GitMerge, XCircle, CheckCircle, AlertTriangle,
  FileCode, Bot, Clock, History, MessageSquare, ChevronDown,
  ChevronRight, Trash2, GitBranch, User, RefreshCw
} from 'lucide-react';

function Section({ title, icon: Icon, children, defaultOpen = true }) {
  const [open, setOpen] = useState(defaultOpen);
  return (
    <div className="glass-card overflow-hidden mb-4">
      <button
        onClick={() => setOpen(!open)}
        className="w-full flex items-center justify-between px-5 py-4 text-left hover:bg-dark-600/30 transition-colors"
      >
        <h3 className="font-semibold text-gray-200 flex items-center gap-2 text-sm">
          <Icon size={16} className="text-brand-400" />
          {title}
        </h3>
        {open ? <ChevronDown size={16} className="text-gray-500" /> : <ChevronRight size={16} className="text-gray-500" />}
      </button>
      {open && <div className="px-5 pb-5">{children}</div>}
    </div>
  );
}

const VALID_TRANSITIONS = {
  AUTHOR: {
    DRAFT: ['OPEN'],
    OPEN: ['CLOSED'],
    CHANGES_REQUESTED: ['OPEN'],
  },
  REVIEWER: {
    OPEN: ['IN_REVIEW'],
    IN_REVIEW: ['APPROVED', 'CHANGES_REQUESTED'],
  },
  ADMIN: {
    DRAFT: ['OPEN', 'CLOSED'],
    OPEN: ['IN_REVIEW', 'CLOSED'],
    IN_REVIEW: ['APPROVED', 'CHANGES_REQUESTED', 'CLOSED'],
    CHANGES_REQUESTED: ['OPEN', 'CLOSED'],
    APPROVED: ['MERGED', 'CLOSED'],
  },
};

const STATUS_BUTTON_CONFIG = {
  OPEN:              { label: 'Submit for Review', cls: 'btn-primary', icon: GitBranch },
  IN_REVIEW:         { label: 'Start Review', cls: 'btn-primary', icon: GitBranch },
  APPROVED:          { label: 'Approve', cls: 'bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center gap-2 transition-colors', icon: CheckCircle },
  CHANGES_REQUESTED: { label: 'Request Changes', cls: 'bg-yellow-700 hover:bg-yellow-600 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center gap-2 transition-colors', icon: AlertTriangle },
  MERGED:            { label: 'Merge', cls: 'bg-purple-600 hover:bg-purple-500 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center gap-2 transition-colors', icon: GitMerge },
  CLOSED:            { label: 'Close PR', cls: 'btn-danger', icon: XCircle },
};

export default function PRDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [pr, setPr] = useState(null);
  const [files, setFiles] = useState([]);
  const [diffs, setDiffs] = useState({});
  const [comments, setComments] = useState([]);
  const [autoReview, setAutoReview] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);
  const [slaTracker, setSlaTracker] = useState(null);
  const [conflicts, setConflicts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  const loadData = useCallback(async () => {
    try {
      const [prRes, filesRes, commentsRes, autoRes, auditRes] = await Promise.all([
        api.get(`/prs/${id}`),
        api.get(`/prs/${id}/files`),
        api.get(`/prs/${id}/comments`),
        api.get(`/prs/${id}/auto-review`),
        api.get(`/prs/${id}/audit`),
      ]);
      setPr(prRes.data);
      setFiles(filesRes.data);
      setComments(commentsRes.data);
      setAutoReview(autoRes.data);
      setAuditLogs(auditRes.data);

      // Load SLA if IN_REVIEW
      if (prRes.data.status === 'IN_REVIEW') {
        api.get(`/prs/${id}/sla`).then(r => setSlaTracker(r.data)).catch(() => {});
      }
      // Load conflicts
      api.get(`/prs/${id}/conflicts`).then(r => setConflicts(r.data)).catch(() => {});
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => { loadData(); }, [loadData]);

  const loadDiff = async (fileId) => {
    if (diffs[fileId]) return;
    try {
      const res = await api.get(`/prs/files/${fileId}/diff`);
      setDiffs(d => ({ ...d, [fileId]: res.data }));
    } catch (err) {
      console.error(err);
    }
  };

  const handleTransition = async (newStatus) => {
    setActionLoading(true);
    try {
      await api.patch(`/prs/${id}/status`, { status: newStatus });
      await loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Transition failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleReview = async (action) => {
    setActionLoading(true);
    try {
      await api.post(`/prs/${id}/review`, { action });
      await loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Review action failed');
    } finally {
      setActionLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Delete this PR?')) return;
    try {
      await api.delete(`/prs/${id}`);
      navigate('/dashboard');
    } catch (err) {
      alert(err.response?.data?.message || 'Delete failed');
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center h-64"><div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" /></div>;
  }

  if (!pr) return <div className="p-6 text-gray-500">PR not found.</div>;

  const role = user?.role;
  const transitions = VALID_TRANSITIONS[role]?.[pr.status] || [];

  return (
    <div className="p-6 max-w-5xl mx-auto animate-fade-in">
      {/* Back */}
      <button onClick={() => navigate(-1)} className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-300 mb-4 transition-colors">
        <ArrowLeft size={14} /> Back
      </button>

      {/* PR Header */}
      <div className="glass-card p-5 mb-4">
        <div className="flex items-start justify-between gap-4 flex-wrap">
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-3 flex-wrap mb-2">
              <h1 className="text-xl font-bold text-gray-100">{pr.title}</h1>
              <StatusBadge status={pr.status} />
              {slaTracker && <SLABadge slaTracker={slaTracker} />}
            </div>
            <p className="text-sm text-gray-400 mb-3">{pr.description || 'No description.'}</p>
            <div className="flex items-center gap-4 text-xs text-gray-500 flex-wrap">
              <span className="flex items-center gap-1"><User size={11}/> {pr.author?.name}</span>
              <span>Repo: <span className="text-brand-400">{pr.repository?.name}</span></span>
              <span>Created: {new Date(pr.createdAt).toLocaleString('en-IN')}</span>
              <span>{files.length} file(s)</span>
            </div>
          </div>
          {/* Action buttons */}
          <div className="flex flex-wrap gap-2 shrink-0">
            {/* State transitions */}
            {transitions.map(newStatus => {
              const cfg = STATUS_BUTTON_CONFIG[newStatus] || { label: newStatus, cls: 'btn-secondary', icon: GitBranch };
              const Icon = cfg.icon;
              return (
                <button
                  key={newStatus}
                  onClick={() => handleTransition(newStatus)}
                  disabled={actionLoading}
                  className={`${cfg.cls} disabled:opacity-50`}
                >
                  <Icon size={14} />
                  {cfg.label}
                </button>
              );
            })}

            {/* Reviewer-specific review actions */}
            {role === 'REVIEWER' && pr.status === 'IN_REVIEW' && (
              <>
                <button
                  onClick={() => handleReview('APPROVE')}
                  disabled={actionLoading}
                  className="bg-emerald-700 hover:bg-emerald-600 text-white px-3 py-2 rounded-lg text-sm font-medium flex items-center gap-1.5 transition-colors"
                >
                  <CheckCircle size={14}/> Approve
                </button>
                <button
                  onClick={() => handleReview('REQUEST_CHANGES')}
                  disabled={actionLoading}
                  className="bg-yellow-700 hover:bg-yellow-600 text-white px-3 py-2 rounded-lg text-sm font-medium flex items-center gap-1.5 transition-colors"
                >
                  <AlertTriangle size={14}/> Request Changes
                </button>
              </>
            )}

            {/* Delete (DRAFT only, author) */}
            {pr.status === 'DRAFT' && role === 'AUTHOR' && user?.id === pr.author?.id && (
              <button onClick={handleDelete} className="btn-danger">
                <Trash2 size={14} /> Delete
              </button>
            )}

            <button onClick={loadData} className="btn-secondary" title="Refresh">
              <RefreshCw size={14} />
            </button>
          </div>
        </div>

        {/* Conflicts warning */}
        {conflicts.length > 0 && (
          <div className="mt-4 bg-yellow-900/30 border border-yellow-700/50 rounded-lg p-3">
            <p className="text-yellow-300 text-xs font-semibold mb-1.5 flex items-center gap-1.5">
              <AlertTriangle size={13} /> Potential Merge Conflicts
            </p>
            {conflicts.map((c, i) => <p key={i} className="text-yellow-400/70 text-xs">{c}</p>)}
          </div>
        )}
      </div>

      {/* Reviewers */}
      {pr.reviewers?.length > 0 && (
        <div className="glass-card p-4 mb-4">
          <p className="text-xs text-gray-500 mb-2 font-medium">Assigned Reviewers</p>
          <div className="flex flex-wrap gap-2">
            {pr.reviewers.map(r => (
              <span key={r.id} className="flex items-center gap-1.5 px-2.5 py-1 bg-dark-600 rounded-full text-xs text-gray-300">
                <span className="w-4 h-4 rounded-full bg-brand-700 flex items-center justify-center text-brand-300 text-xs font-bold">
                  {r.reviewer?.name?.charAt(0)}
                </span>
                {r.reviewer?.name}
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Auto Review */}
      <Section title="Auto-Review Report" icon={Bot}>
        <AutoReviewPanel results={autoReview} />
      </Section>

      {/* Files & Diffs */}
      <Section title={`Files (${files.length})`} icon={FileCode}>
        <div className="space-y-4">
          {files.map(file => (
            <div key={file.id} className="border border-dark-500 rounded-lg overflow-hidden">
              <button
                onClick={() => loadDiff(file.id)}
                className="w-full flex items-center gap-2 px-4 py-3 bg-dark-700 hover:bg-dark-600 text-left transition-colors"
              >
                <FileCode size={14} className="text-brand-400" />
                <span className="font-mono text-sm text-gray-300">{file.filename}</span>
                <span className="ml-auto text-xs text-brand-400 hover:text-brand-300">
                  {diffs[file.id] ? 'View Diff ↕' : 'Load Diff →'}
                </span>
              </button>
              {diffs[file.id] && (
                <>
                  <DiffViewer diffs={diffs[file.id]} />
                  {/* Inline comments for this file */}
                  <div className="p-4 bg-dark-800/50 border-t border-dark-500">
                    <p className="text-xs font-medium text-gray-400 mb-3 flex items-center gap-1.5">
                      <MessageSquare size={12} /> Inline Comments
                    </p>
                    <CommentThread
                      prId={id}
                      fileId={file.id}
                      comments={comments}
                      onCommentAdded={loadData}
                    />
                  </div>
                </>
              )}
            </div>
          ))}
        </div>
      </Section>

      {/* General Comments */}
      <Section title="General Comments" icon={MessageSquare}>
        <CommentThread
          prId={id}
          fileId={null}
          comments={comments}
          onCommentAdded={loadData}
        />
      </Section>

      {/* Audit Trail */}
      <Section title="Audit Trail" icon={History}>
        <AuditTimeline logs={auditLogs} />
      </Section>

      {/* SLA Info */}
      {slaTracker && (
        <Section title="SLA Status" icon={Clock}>
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p className="text-xs text-gray-500 mb-1">Started</p>
              <p className="text-gray-300">{new Date(slaTracker.startedAt).toLocaleString('en-IN')}</p>
            </div>
            <div>
              <p className="text-xs text-gray-500 mb-1">Deadline</p>
              <p className="text-gray-300">{new Date(slaTracker.deadline).toLocaleString('en-IN')}</p>
            </div>
            <div>
              <p className="text-xs text-gray-500 mb-1">Status</p>
              <SLABadge slaTracker={slaTracker} />
            </div>
          </div>
        </Section>
      )}
    </div>
  );
}
