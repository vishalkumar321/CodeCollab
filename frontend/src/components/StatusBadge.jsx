import React from 'react';

const STATUS_MAP = {
  DRAFT:             { label: 'Draft',            cls: 'badge-draft' },
  OPEN:              { label: 'Open',             cls: 'badge-open' },
  IN_REVIEW:         { label: 'In Review',        cls: 'badge-in-review' },
  CHANGES_REQUESTED: { label: 'Changes Requested',cls: 'badge-changes' },
  APPROVED:          { label: 'Approved',         cls: 'badge-approved' },
  MERGED:            { label: 'Merged',           cls: 'badge-merged' },
  CLOSED:            { label: 'Closed',           cls: 'badge-closed' },
};

export default function StatusBadge({ status }) {
  const { label, cls } = STATUS_MAP[status] || { label: status, cls: 'badge-draft' };
  return <span className={cls}>{label}</span>;
}
