import React from 'react';
import { GitBranch, ArrowRight, User } from 'lucide-react';

function formatDateTime(ts) {
  if (!ts) return '';
  return new Date(ts).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' });
}

const ACTION_COLOR = {
  'STATE_TRANSITION: DRAFT -> OPEN':               'bg-green-500',
  'STATE_TRANSITION: OPEN -> IN_REVIEW':           'bg-blue-500',
  'STATE_TRANSITION: IN_REVIEW -> APPROVED':       'bg-emerald-500',
  'STATE_TRANSITION: IN_REVIEW -> CHANGES_REQUESTED': 'bg-yellow-500',
  'STATE_TRANSITION: CHANGES_REQUESTED -> OPEN':   'bg-orange-500',
  'STATE_TRANSITION: OPEN -> CLOSED':              'bg-red-500',
  'SLA_BREACH_ESCALATION':                         'bg-red-600',
  'REVIEWER_REASSIGNMENT':                         'bg-purple-500',
};

function getDotColor(entry) {
  return ACTION_COLOR[entry.action] || 'bg-brand-500';
}

function formatAction(action) {
  if (action.startsWith('STATE_TRANSITION:')) {
    const parts = action.replace('STATE_TRANSITION: ', '');
    return parts;
  }
  return action.replace(/_/g, ' ');
}

export default function AuditTimeline({ logs }) {
  if (!logs || logs.length === 0) {
    return <p className="text-gray-500 text-sm italic">No audit entries yet.</p>;
  }

  return (
    <div className="relative space-y-0">
      {/* Vertical line */}
      <div className="absolute left-[5px] top-3 bottom-3 w-0.5 bg-dark-500 z-0" />

      {logs.map((entry, idx) => (
        <div key={idx} className="relative flex items-start gap-4 pb-6 last:pb-0 animate-fade-in">
          {/* Dot */}
          <div className={`timeline-dot ${getDotColor(entry)} z-10 mt-1`} />

          {/* Content */}
          <div className="flex-1 glass-card p-3">
            <div className="flex items-start justify-between gap-2">
              <div>
                <p className="text-sm font-medium text-gray-200 flex items-center gap-1.5">
                  <GitBranch size={13} className="text-brand-400" />
                  {formatAction(entry.action)}
                </p>
                {entry.fromState && entry.toState && entry.fromState !== entry.toState && (
                  <div className="flex items-center gap-1.5 mt-1">
                    <span className="text-xs text-gray-500">{entry.fromState}</span>
                    <ArrowRight size={11} className="text-gray-600" />
                    <span className="text-xs text-gray-300">{entry.toState}</span>
                  </div>
                )}
              </div>
              <span className="text-xs text-gray-500 shrink-0">{formatDateTime(entry.timestamp)}</span>
            </div>
            {entry.actor && (
              <p className="text-xs text-gray-500 mt-1.5 flex items-center gap-1">
                <User size={10} />
                {entry.actor.name || entry.actor.email}
              </p>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}
