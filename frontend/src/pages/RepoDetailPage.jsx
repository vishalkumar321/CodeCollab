import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import StatusBadge from '../components/StatusBadge';
import { BookOpen, GitPullRequest, Plus, ArrowLeft, Code } from 'lucide-react';

export default function RepoDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [repo, setRepo] = useState(null);
  const [prs, setPrs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [repoRes, prsRes] = await Promise.all([
          api.get(`/repos/${id}`),
          api.get(`/prs?repoId=${id}`),
        ]);
        setRepo(repoRes.data);
        setPrs(prsRes.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  if (loading) {
    return <div className="flex items-center justify-center h-64"><div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin" /></div>;
  }

  if (!repo) {
    return <div className="p-6 text-gray-500">Repository not found.</div>;
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      {/* Back */}
      <button onClick={() => navigate('/repos')} className="flex items-center gap-1.5 text-sm text-gray-500 hover:text-gray-300 mb-4 transition-colors">
        <ArrowLeft size={14} /> Back to Repositories
      </button>

      {/* Repo header */}
      <div className="glass-card p-6 mb-6">
        <div className="flex items-start justify-between gap-4">
          <div>
            <div className="flex items-center gap-3 mb-2">
              <div className="w-10 h-10 rounded-lg bg-brand-700/40 flex items-center justify-center">
                <BookOpen size={20} className="text-brand-400" />
              </div>
              <div>
                <h1 className="text-xl font-bold text-gray-100">{repo.name}</h1>
                <span className="text-xs text-gray-500">{repo.language}</span>
              </div>
            </div>
            <p className="text-gray-400 text-sm">{repo.description || 'No description'}</p>
            <p className="text-xs text-gray-600 mt-2">Owner: {repo.owner?.name}</p>
          </div>
          {user?.role === 'AUTHOR' && (
            <button
              onClick={() => navigate(`/pr/new?repoId=${id}`)}
              className="btn-primary shrink-0"
            >
              <Plus size={15} /> New PR
            </button>
          )}
        </div>
      </div>

      {/* PR list */}
      <div>
        <h2 className="text-lg font-semibold text-gray-200 mb-3 flex items-center gap-2">
          <GitPullRequest size={18} className="text-brand-400" />
          Pull Requests ({prs.length})
        </h2>

        {prs.length === 0 ? (
          <div className="text-center py-12 text-gray-600">
            <GitPullRequest size={32} className="mx-auto mb-2 opacity-30" />
            <p>No pull requests for this repository.</p>
          </div>
        ) : (
          <div className="space-y-3">
            {prs.map(pr => (
              <Link
                key={pr.id}
                to={`/pr/${pr.id}`}
                className="glass-card p-4 flex items-start justify-between gap-3 hover:border-brand-600/50 transition-all duration-200 hover:-translate-y-0.5 group"
              >
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-gray-200 group-hover:text-brand-300 transition-colors truncate">
                    {pr.title}
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5">
                    by {pr.author?.name} · {new Date(pr.createdAt).toLocaleDateString('en-IN')}
                  </p>
                </div>
                <StatusBadge status={pr.status} />
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
