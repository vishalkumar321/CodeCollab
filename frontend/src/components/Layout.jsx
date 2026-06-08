import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  GitPullRequest, Home, BookOpen, Shield, LogOut, Code2, Menu, X
} from 'lucide-react';
import { useState } from 'react';

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItems = [
    { to: '/dashboard', icon: Home, label: 'Dashboard' },
    { to: '/repos', icon: BookOpen, label: 'Repositories' },
    ...(user?.role === 'ADMIN' ? [{ to: '/admin', icon: Shield, label: 'Admin Panel' }] : []),
  ];

  return (
    <div className="flex h-screen overflow-hidden">
      {/* Sidebar */}
      <aside className={`${sidebarOpen ? 'w-64' : 'w-16'} transition-all duration-300 flex flex-col bg-dark-800 border-r border-dark-500 shrink-0`}>
        {/* Logo */}
        <div className="flex items-center gap-3 px-4 py-5 border-b border-dark-500">
          <div className="w-8 h-8 rounded-lg bg-brand-600 flex items-center justify-center shrink-0 glow-brand">
            <Code2 size={18} className="text-white" />
          </div>
          {sidebarOpen && (
            <span className="font-bold text-gradient text-lg">CodeCollab</span>
          )}
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="ml-auto text-gray-500 hover:text-gray-300 transition-colors"
          >
            {sidebarOpen ? <X size={16} /> : <Menu size={16} />}
          </button>
        </div>

        {/* Nav items */}
        <nav className="flex-1 px-2 py-4 space-y-1">
          {navItems.map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `nav-item ${isActive ? 'active' : ''}`
              }
            >
              <Icon size={18} />
              {sidebarOpen && <span>{label}</span>}
            </NavLink>
          ))}
        </nav>

        {/* User info */}
        <div className="p-3 border-t border-dark-500">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-brand-700 flex items-center justify-center text-xs font-bold text-brand-200 shrink-0">
              {user?.name?.charAt(0).toUpperCase()}
            </div>
            {sidebarOpen && (
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-200 truncate">{user?.name}</p>
                <p className="text-xs text-gray-500 truncate">{user?.role}</p>
              </div>
            )}
            <button
              onClick={handleLogout}
              className="text-gray-500 hover:text-red-400 transition-colors"
              title="Logout"
            >
              <LogOut size={16} />
            </button>
          </div>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex-1 overflow-auto">
        <Outlet />
      </main>
    </div>
  );
}
