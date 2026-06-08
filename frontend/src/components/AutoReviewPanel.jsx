import React from 'react';
import { AlertTriangle, Info, XCircle, CheckCircle, Bot } from 'lucide-react';

const SEVERITY_CONFIG = {
  INFO:    { cls: 'severity-info',    icon: Info,          bgCls: 'bg-blue-900/20 border-blue-700/30' },
  WARNING: { cls: 'severity-warning', icon: AlertTriangle, bgCls: 'bg-yellow-900/20 border-yellow-700/30' },
  ERROR:   { cls: 'severity-error',   icon: XCircle,       bgCls: 'bg-red-900/20 border-red-700/30' },
};

export default function AutoReviewPanel({ results }) {
  if (!results || results.length === 0) {
    return (
      <div className="flex items-center gap-3 p-4 bg-green-900/20 border border-green-700/30 rounded-lg">
        <CheckCircle size={18} className="text-green-400" />
        <span className="text-green-300 text-sm">All auto-review checks passed!</span>
      </div>
    );
  }

  const counts = { INFO: 0, WARNING: 0, ERROR: 0 };
  results.forEach(r => counts[r.severity]++);

  return (
    <div className="space-y-3">
      {/* Summary */}
      <div className="flex items-center gap-4 p-3 glass-card rounded-lg">
        <Bot size={16} className="text-brand-400" />
        <span className="text-sm text-gray-300 font-medium">Auto-Review Summary</span>
        <div className="flex gap-3 ml-auto">
          {counts.ERROR > 0 && <span className="severity-error">{counts.ERROR} Error{counts.ERROR > 1 ? 's' : ''}</span>}
          {counts.WARNING > 0 && <span className="severity-warning">{counts.WARNING} Warning{counts.WARNING > 1 ? 's' : ''}</span>}
          {counts.INFO > 0 && <span className="severity-info">{counts.INFO} Info</span>}
        </div>
      </div>

      {/* Results list */}
      {results.map((result, idx) => {
        const { cls, icon: Icon, bgCls } = SEVERITY_CONFIG[result.severity] || SEVERITY_CONFIG.INFO;
        return (
          <div key={idx} className={`flex items-start gap-3 p-3 border rounded-lg ${bgCls}`}>
            <Icon size={16} className="shrink-0 mt-0.5" />
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 mb-1">
                <span className={cls}>{result.severity}</span>
                <span className="text-xs text-gray-500 font-mono">{result.ruleName}</span>
              </div>
              <p className="text-sm text-gray-300">{result.message}</p>
            </div>
          </div>
        );
      })}
    </div>
  );
}
