import { Link, Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth.jsx';
import Products from './pages/Products.jsx';
import Login from './pages/Login.jsx';
import Cart from './pages/Cart.jsx';
import Orders from './pages/Orders.jsx';
import Admin from './pages/Admin.jsx';

function Protected({ children, admin }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (admin && user.role !== 'ADMIN') return <Navigate to="/" replace />;
  return children;
}

export default function App() {
  const { user, logout } = useAuth();
  return (
    <>
      <header className="nav">
        <Link to="/" className="brand">ShopSphere</Link>
        <nav>
          <Link to="/">Shop</Link>
          {user && <Link to="/cart">Cart</Link>}
          {user && <Link to="/orders">My orders</Link>}
          {user?.role === 'ADMIN' && <Link to="/admin">Admin</Link>}
          {user ? (
            <button className="link" onClick={logout}>Log out ({user.name})</button>
          ) : (
            <Link to="/login">Log in</Link>
          )}
        </nav>
      </header>
      <main className="container">
        <Routes>
          <Route path="/" element={<Products />} />
          <Route path="/login" element={<Login />} />
          <Route path="/cart" element={<Protected><Cart /></Protected>} />
          <Route path="/orders" element={<Protected><Orders /></Protected>} />
          <Route path="/admin" element={<Protected admin><Admin /></Protected>} />
        </Routes>
      </main>
    </>
  );
}
