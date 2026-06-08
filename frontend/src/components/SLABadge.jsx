import React from 'react';
import { Clock, AlertTriangle, Zap } from 'lucide-react';

export default function SLABadge({ slaTracker }) {
  if (!slaTracker) return null;

  let cls, label, Icon;
  if (slaTracker.escalatedAt) {
    cls = 'sla-escalated'; label = 'Escalated'; Icon = Zap;
  } else if (slaTracker.breached) {
    cls = 'sla-breached'; label = 'SLA Breached'; Icon = AlertTriangle;
  } else {
    cls = 'sla-on-track'; label = 'On Track'; Icon = Clock;
  }

  return (
    <span className={cls}>
      <Icon size={10} />
      {label}
    </span>
  );
}
