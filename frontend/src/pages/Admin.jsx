import { useEffect, useState } from 'react';
import api, { errMsg, money } from '../api.js';

const STATUSES = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];
const empty = { name: '', description: '', price: '', stock: '', imageUrl: '', category: '' };

export default function Admin() {
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [form, setForm] = useState(empty);
  const [error, setError] = useState('');

  const load = () => {
    api.get('/products', { params: { size: 100, sort: 'id,desc' } }).then((r) => setProducts(r.data.content));
    api.get('/admin/orders').then((r) => setOrders(r.data));
  };
  useEffect(load, []);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const create = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/products', { ...form, price: Number(form.price), stock: Number(form.stock) });
      setForm(empty);
      load();
    } catch (err) {
      setError(errMsg(err));
    }
  };

  const remove = async (id) => {
    setError('');
    try { await api.delete(`/products/${id}`); load(); } catch (err) { setError(errMsg(err)); }
  };

  const setStatus = async (id, status) => {
    setError('');
    try { await api.patch(`/admin/orders/${id}/status`, { status }); load(); } catch (err) { setError(errMsg(err)); }
  };

  return (
    <>
      {error && <p className="error">{error}</p>}
      <form className="panel form-grid" onSubmit={create}>
        <h2>Add a product</h2>
        <input placeholder="Name" value={form.name} onChange={set('name')} required />
        <input placeholder="Category" value={form.category} onChange={set('category')} />
        <input type="number" step="0.01" placeholder="Price" value={form.price} onChange={set('price')} required />
        <input type="number" placeholder="Stock" value={form.stock} onChange={set('stock')} required />
        <input placeholder="Image URL" value={form.imageUrl} onChange={set('imageUrl')} />
        <input placeholder="Description" value={form.description} onChange={set('description')} />
        <button className="btn">Add product</button>
      </form>

      <div className="panel">
        <h2>Products</h2>
        {products.map((p) => (
          <div key={p.id} className="row">
            <span>{p.name} <span className="muted">({p.stock} in stock, {money(p.price)})</span></span>
            <button className="link" onClick={() => remove(p.id)}>Delete</button>
          </div>
        ))}
      </div>

      <div className="panel">
        <h2>Orders</h2>
        {orders.length === 0 && <p className="muted">No orders yet.</p>}
        {orders.map((o) => (
          <div key={o.id} className="row">
            <span>#{o.id} - {o.userEmail} - {money(o.total)}</span>
            <select value={o.status} disabled={o.status === 'CANCELLED'} onChange={(e) => setStatus(o.id, e.target.value)}>
              {STATUSES.map((s) => <option key={s}>{s}</option>)}
            </select>
          </div>
        ))}
      </div>
    </>
  );
}
