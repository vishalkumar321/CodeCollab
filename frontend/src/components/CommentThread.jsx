import React, { useState } from 'react';
import { MessageSquare, CheckCircle, ChevronDown, ChevronRight, User, CornerDownRight } from 'lucide-react';
import api from '../api/axios';

function CommentItem({ comment, allComments, onToggleResolved, depth = 0 }) {
  const replies = allComments.filter(c => c.parentCommentId === comment.id);
  const [showReplies, setShowReplies] = useState(true);

  return (
    <div className={depth > 0 ? 'ml-6 border-l border-dark-500 pl-4' : ''}>
      <div className={`glass-card p-3 mb-2 ${comment.isResolved ? 'opacity-60' : ''}`}>
        <div className="flex items-start justify-between gap-2">
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded-full bg-brand-800 flex items-center justify-center text-xs font-bold text-brand-300">
              {(comment.author?.name || 'U').charAt(0).toUpperCase()}
            </div>
            <span className="text-xs font-medium text-gray-300">{comment.author?.name || 'Unknown'}</span>
            {comment.lineNumber && (
              <span className="text-xs text-gray-600 font-mono">L{comment.lineNumber}</span>
            )}
            {comment.isResolved && (
              <span className="text-xs text-green-500 flex items-center gap-1">
                <CheckCircle size={10} /> Resolved
              </span>
            )}
          </div>
          <div className="flex items-center gap-2">
            <span className="text-xs text-gray-600">
              {new Date(comment.createdAt).toLocaleString('en-IN', { dateStyle: 'short', timeStyle: 'short' })}
            </span>
            <button
              onClick={() => onToggleResolved(comment.id)}
              className="text-xs text-gray-500 hover:text-brand-400 transition-colors"
            >
              {comment.isResolved ? 'Unresolve' : 'Resolve'}
            </button>
          </div>
        </div>
        <p className="text-sm text-gray-300 mt-2 leading-relaxed">{comment.content}</p>
      </div>

      {/* Replies */}
      {replies.length > 0 && (
        <div>
          <button
            onClick={() => setShowReplies(!showReplies)}
            className="flex items-center gap-1 text-xs text-gray-500 hover:text-gray-300 mb-2 ml-2"
          >
            <CornerDownRight size={11} />
            {replies.length} {replies.length === 1 ? 'reply' : 'replies'}
            {showReplies ? <ChevronDown size={11} /> : <ChevronRight size={11} />}
          </button>
          {showReplies && replies.map(r => (
            <CommentItem
              key={r.id}
              comment={r}
              allComments={allComments}
              onToggleResolved={onToggleResolved}
              depth={depth + 1}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default function CommentThread({ prId, fileId, comments, onCommentAdded }) {
  const [newComment, setNewComment] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Top-level comments only (no parent)
  const topLevel = comments.filter(c =>
    !c.parentCommentId && (fileId ? c.file?.id === fileId : !c.file)
  );

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    setSubmitting(true);
    try {
      await api.post(`/prs/${prId}/comments`, {
        fileId: fileId || null,
        content: newComment.trim(),
      });
      setNewComment('');
      onCommentAdded?.();
    } catch (err) {
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleToggleResolved = async (commentId) => {
    try {
      await api.patch(`/comments/${commentId}/resolve`);
      onCommentAdded?.();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="space-y-3">
      {topLevel.length === 0 && (
        <p className="text-gray-600 text-sm italic flex items-center gap-2">
          <MessageSquare size={14} /> No comments yet.
        </p>
      )}
      {topLevel.map(comment => (
        <CommentItem
          key={comment.id}
          comment={comment}
          allComments={comments}
          onToggleResolved={handleToggleResolved}
        />
      ))}

      {/* Add comment form */}
      <form onSubmit={handleSubmit} className="mt-3">
        <textarea
          value={newComment}
          onChange={e => setNewComment(e.target.value)}
          placeholder="Add a comment..."
          rows={3}
          className="input-field resize-none"
        />
        <button
          type="submit"
          disabled={submitting || !newComment.trim()}
          className="btn-primary mt-2 disabled:opacity-40 disabled:cursor-not-allowed"
        >
          {submitting ? 'Posting...' : 'Add Comment'}
        </button>
      </form>
    </div>
  );
}
