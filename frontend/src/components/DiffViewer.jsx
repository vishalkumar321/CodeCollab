import React, { useState } from 'react';
import { Plus, Minus, Equal } from 'lucide-react';

export default function DiffViewer({ diffs }) {
  const [collapsed, setCollapsed] = useState(false);

  if (!diffs || diffs.length === 0) {
    return <p className="text-gray-500 text-sm italic p-4">No diff available.</p>;
  }

  return (
    <div className="font-mono text-xs overflow-x-auto">
      <div className="flex items-center justify-between px-3 py-2 bg-dark-600 border-b border-dark-500 rounded-t-lg">
        <span className="text-gray-400 text-xs">
          <span className="text-green-400 mr-3">+{diffs.filter(d => d.type === 'ADDED').length} added</span>
          <span className="text-red-400">-{diffs.filter(d => d.type === 'REMOVED').length} removed</span>
        </span>
        <button
          onClick={() => setCollapsed(!collapsed)}
          className="text-gray-500 hover:text-gray-300 text-xs"
        >
          {collapsed ? 'Expand' : 'Collapse'}
        </button>
      </div>
      {!collapsed && (
        <div className="rounded-b-lg overflow-hidden border border-dark-500 border-t-0">
          {diffs.map((line, idx) => {
            let rowClass = 'diff-unchanged';
            let prefix = ' ';
            if (line.type === 'ADDED')   { rowClass = 'diff-added';   prefix = '+'; }
            if (line.type === 'REMOVED') { rowClass = 'diff-removed'; prefix = '-'; }

            return (
              <div key={idx} className={`flex ${rowClass} px-0`}>
                <span className="w-12 text-right pr-3 py-0.5 text-gray-600 border-r border-dark-500 select-none shrink-0">
                  {line.lineNumber}
                </span>
                <span className="w-5 text-center py-0.5 shrink-0 select-none">{prefix}</span>
                <span className="flex-1 py-0.5 px-2 whitespace-pre overflow-x-auto">{line.content}</span>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
