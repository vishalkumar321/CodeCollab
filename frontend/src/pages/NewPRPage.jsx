import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../api/axios';
import { Plus, Trash2, FileCode, Users, ArrowLeft } from 'lucide-react';

export default function NewPRPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const defaultRepoId = searchParams.get('repoId') || '';

  const [repos, setRepos] = useState([]);
  const [reviewers, setReviewers] = useState([]);
  const [form, setForm] = useState({
    title: '',
    description: '',
    repoId: defaultRepoId,
    reviewerIds: [],
    files: [{ filename: '', baseContent: '', changedContent: '' }],
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([
      api.get('/repos'),
      api.get('/users/reviewers'),
    ]).then(([repoRes, revRes]) => {
      setRepos(repoRes.data);
      setReviewers(revRes.data);
    }).catch(console.error);
  }, []);

  const addFile = () => {
    setForm(f => ({ ...f, files: [...f.files, { filename: '', baseContent: '', changedContent: '' }] }));
  };

  const removeFile = (idx) => {
    setForm(f => ({ ...f, files: f.files.filter((_, i) => i !== idx) }));
  };

  const updateFile = (idx, field, value) => {
    setForm(f => {
      const files = [...f.files];
      files[idx] = { ...files[idx], [field]: value };
      return { ...f, files };
    });
  };

  const toggleReviewer = (id) => {
    setForm(f => ({
      ...f,
      reviewerIds: f.reviewerIds.includes(id)
        ? f.reviewerIds.filter(r => r !== id)
        : [...f.reviewerIds, id],
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!form.repoId) { setError('Please select a repository.'); return; }
    if (form.files.some(f => !f.filename.trim())) { setError('All files must have a filename.'); return; }

    setSubmitting(true);
    try {
      const payload = { ...form, repoId: Number(form.repoId) };
      const res = await api.post('/prs', payload);
      navigate(`/pr/${res.data.id}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create PR');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <button onClick={() => navigate(-1)} className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-300 mb-4 transition-colors">
        <ArrowLeft size={14} /> Back
      </button>

      <h1 className="text-2xl font-bold text-gray-100 mb-6">Create Pull Request</h1>

      {error && (
        <div className="bg-red-900/30 border border-red-700/50 text-red-300 text-sm rounded-lg px-4 py-3 mb-4">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Basic info */}
        <div className="glass-card p-5 space-y-4">
          <h2 className="text-base font-semibold text-gray-200">PR Details</h2>
          <div>
            <label className="block text-xs text-gray-400 mb-1.5">Repository *</label>
            <select
              value={form.repoId}
              onChange={e => setForm(f => ({ ...f, repoId: e.target.value }))}
              className="input-field"
              required
            >
              <option value="">Select a repository...</option>
              {repos.map(r => <option key={r.id} value={r.id}>{r.name}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-xs text-gray-400 mb-1.5">PR Title *</label>
            <input
              type="text"
              required
              value={form.title}
              onChange={e => setForm(f => ({ ...f, title: e.target.value }))}
              placeholder="feat: add user authentication"
              className="input-field"
            />
          </div>
          <div>
            <label className="block text-xs text-gray-400 mb-1.5">Description</label>
            <textarea
              rows={3}
              value={form.description}
              onChange={e => setForm(f => ({ ...f, description: e.target.value }))}
              placeholder="Describe what this PR does..."
              className="input-field resize-none"
            />
          </div>
        </div>

        {/* Reviewers */}
        <div className="glass-card p-5">
          <h2 className="text-base font-semibold text-gray-200 mb-3 flex items-center gap-2">
            <Users size={16} className="text-brand-400" /> Assign Reviewers
          </h2>
          {reviewers.length === 0 ? (
            <p className="text-gray-500 text-sm">No reviewers available.</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {reviewers.map(r => (
                <button
                  key={r.id}
                  type="button"
                  onClick={() => toggleReviewer(r.id)}
                  className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-sm border transition-all duration-150 ${
                    form.reviewerIds.includes(r.id)
                      ? 'bg-brand-600/30 border-brand-500 text-brand-300'
                      : 'bg-dark-700 border-dark-400 text-gray-400 hover:border-dark-300'
                  }`}
                >
                  <span className="w-5 h-5 rounded-full bg-dark-500 flex items-center justify-center text-xs font-bold">
                    {r.name.charAt(0).toUpperCase()}
                  </span>
                  {r.name}
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Files */}
        <div className="glass-card p-5 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-semibold text-gray-200 flex items-center gap-2">
              <FileCode size={16} className="text-brand-400" /> Files ({form.files.length})
            </h2>
            <button type="button" onClick={addFile} className="btn-secondary text-xs">
              <Plus size={13} /> Add File
            </button>
          </div>

          {form.files.map((file, idx) => (
            <div key={idx} className="border border-dark-500 rounded-lg p-4 space-y-3 bg-dark-800/50">
              <div className="flex items-center justify-between gap-2">
                <input
                  type="text"
                  value={file.filename}
                  onChange={e => updateFile(idx, 'filename', e.target.value)}
                  placeholder="src/components/Auth.jsx"
                  className="input-field font-mono text-xs flex-1"
                />
                {form.files.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeFile(idx)}
                    className="text-red-500 hover:text-red-400 shrink-0"
                  >
                    <Trash2 size={15} />
                  </button>
                )}
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs text-gray-500 mb-1">Base Content (before)</label>
                  <textarea
                    rows={6}
                    value={file.baseContent}
                    onChange={e => updateFile(idx, 'baseContent', e.target.value)}
                    placeholder="Original code..."
                    className="input-field font-mono text-xs resize-y"
                  />
                </div>
                <div>
                  <label className="block text-xs text-gray-500 mb-1">Changed Content (after)</label>
                  <textarea
                    rows={6}
                    value={file.changedContent}
                    onChange={e => updateFile(idx, 'changedContent', e.target.value)}
                    placeholder="New/modified code..."
                    className="input-field font-mono text-xs resize-y"
                  />
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="flex gap-3">
          <button type="submit" disabled={submitting} className="btn-primary">
            {submitting ? 'Creating PR...' : 'Create Pull Request (DRAFT)'}
          </button>
          <button type="button" onClick={() => navigate(-1)} className="btn-secondary">
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
