import { useEffect, useState } from 'react';
import api, { money } from '../api.js';

export default function Orders() {
  const [orders, setOrders] = useState([]);
  useEffect(() => { api.get('/orders').then((r) => setOrders(r.data)); }, []);

  if (orders.length === 0) return <p className="muted">You haven't placed any orders yet.</p>;

  return (
    <>
      <h2>My orders</h2>
      {orders.map((o) => (
        <div key={o.id} className="panel">
          <div className="row">
            <strong>Order #{o.id}</strong>
            <span className={`status ${o.status}`}>{o.status}</span>
          </div>
          <p className="muted">{new Date(o.createdAt).toLocaleString()}</p>
          {o.items.map((i) => (
            <div key={i.productId} className="row">
              <span>{i.productName} x {i.quantity}</span>
              <span>{money(i.price * i.quantity)}</span>
            </div>
          ))}
          <div className="row total"><span>Total</span><strong>{money(o.total)}</strong></div>
        </div>
      ))}
    </>
  );
}
