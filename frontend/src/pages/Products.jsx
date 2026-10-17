import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api, { errMsg, money } from '../api.js';
import { useAuth } from '../auth.jsx';

export default function Products() {
  const [page, setPage] = useState({ content: [], totalPages: 0, number: 0 });
  const [categories, setCategories] = useState([]);
  const [q, setQ] = useState('');
  const [category, setCategory] = useState('');
  const [sort, setSort] = useState('id,asc');
  const [pageNo, setPageNo] = useState(0);
  const [message, setMessage] = useState('');
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/products/categories').then((r) => setCategories(r.data));
  }, []);

  useEffect(() => {
    const params = { page: pageNo, size: 8, sort };
    if (q) params.q = q;
    if (category) params.category = category;
    api.get('/products', { params }).then((r) => setPage(r.data));
  }, [q, category, sort, pageNo]);

  const addToCart = async (productId) => {
    if (!user) return navigate('/login');
    try {
      await api.post('/cart/items', { productId, quantity: 1 });
      setMessage('Added to cart');
    } catch (e) {
      setMessage(errMsg(e));
    }
    setTimeout(() => setMessage(''), 2500);
  };

  return (
    <>
      <div className="toolbar">
        <input placeholder="Search products" value={q} onChange={(e) => { setQ(e.target.value); setPageNo(0); }} />
        <select value={category} onChange={(e) => { setCategory(e.target.value); setPageNo(0); }}>
          <option value="">All categories</option>
          {categories.map((c) => <option key={c}>{c}</option>)}
        </select>
        <select value={sort} onChange={(e) => setSort(e.target.value)}>
          <option value="id,asc">Newest last</option>
          <option value="price,asc">Price: low to high</option>
          <option value="price,desc">Price: high to low</option>
          <option value="name,asc">Name</option>
        </select>
      </div>
      {message && <p className="toast">{message}</p>}
      {page.content.length === 0 && <p className="muted">No products match your search.</p>}
      <div className="grid">
        {page.content.map((p) => (
          <article key={p.id} className="card">
            <img src={p.imageUrl} alt={p.name} loading="lazy" />
            <h3>{p.name}</h3>
            <p className="muted">{p.description}</p>
            <div className="row">
              <strong>{money(p.price)}</strong>
              <button className="btn" disabled={p.stock === 0} onClick={() => addToCart(p.id)}>
                {p.stock === 0 ? 'Out of stock' : 'Add to cart'}
              </button>
            </div>
          </article>
        ))}
      </div>
      {page.totalPages > 1 && (
        <div className="pager">
          <button className="btn ghost" disabled={page.number === 0} onClick={() => setPageNo(pageNo - 1)}>Previous</button>
          <span>Page {page.number + 1} of {page.totalPages}</span>
          <button className="btn ghost" disabled={page.number + 1 >= page.totalPages} onClick={() => setPageNo(pageNo + 1)}>Next</button>
        </div>
      )}
    </>
  );
}
