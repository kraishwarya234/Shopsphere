import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api, { errMsg, money } from '../api.js';

export default function Cart() {
  const [cart, setCart] = useState({ items: [], total: 0 });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const run = async (fn) => {
    setError('');
    try {
      const { data } = await fn();
      setCart(data);
    } catch (e) {
      setError(errMsg(e));
    }
  };

  useEffect(() => { run(() => api.get('/cart')); }, []);

  const placeOrder = async () => {
    setError('');
    try {
      await api.post('/orders');
      navigate('/orders');
    } catch (e) {
      setError(errMsg(e));
    }
  };

  if (cart.items.length === 0) return <p className="muted">Your cart is empty. Add something from the shop.</p>;

  return (
    <div className="panel">
      <h2>Your cart</h2>
      {cart.items.map((i) => (
        <div key={i.id} className="line">
          <img src={i.imageUrl} alt="" />
          <div className="grow"><strong>{i.name}</strong><div className="muted">{money(i.price)} each</div></div>
          <input type="number" min="1" value={i.quantity} className="qty"
                 onChange={(e) => run(() => api.put(`/cart/items/${i.id}`, { quantity: Number(e.target.value) || 1 }))} />
          <strong>{money(i.subtotal)}</strong>
          <button className="link" onClick={() => run(() => api.delete(`/cart/items/${i.id}`))}>Remove</button>
        </div>
      ))}
      {error && <p className="error">{error}</p>}
      <div className="row total"><span>Total</span><strong>{money(cart.total)}</strong></div>
      <button className="btn" onClick={placeOrder}>Place order</button>
    </div>
  );
}
