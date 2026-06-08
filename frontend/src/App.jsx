import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import ReposPage from './pages/ReposPage';
import RepoDetailPage from './pages/RepoDetailPage';
import NewPRPage from './pages/NewPRPage';
import PRDetailPage from './pages/PRDetailPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import Layout from './components/Layout';

function ProtectedRoute({ children, requiredRole }) {
  const { isAuthenticated, user, loading } = useAuth();
  if (loading) return <div className="flex items-center justify-center h-screen"><div className="w-8 h-8 border-2 border-brand-500 border-t-transparent rounded-full animate-spin"/></div>;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (requiredRole && user?.role !== requiredRole) return <Navigate to="/dashboard" replace />;
  return children;
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="repos" element={<ReposPage />} />
            <Route path="repos/:id" element={<RepoDetailPage />} />
            <Route path="pr/new" element={<NewPRPage />} />
            <Route path="pr/:id" element={<PRDetailPage />} />
            <Route path="admin" element={
              <ProtectedRoute requiredRole="ADMIN">
                <AdminDashboardPage />
              </ProtectedRoute>
            } />
          </Route>
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
