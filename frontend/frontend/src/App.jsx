import { useEffect, useMemo, useState } from 'react';
import './App.css';

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api';
const DEMO_EMAIL = 'demo@cartcloud.test';
const DEMO_PASSWORD = 'cartcloud';

const formatPrice = (value) =>
  typeof value === 'number' || typeof value === 'string'
    ? Number(value).toFixed(2)
    : (value || 0).toFixed(2);

function App() {
  const [user, setUser] = useState(null);
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [cart, setCart] = useState({ items: [], totalPrice: 0 });
  const [lastOrder, setLastOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [showCart, setShowCart] = useState(false);
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');

  const filteredProducts = useMemo(() => {
    const term = search.toLowerCase();
    return products.filter((p) => {
      const matchesTerm =
        p.name?.toLowerCase().includes(term) ||
        p.description?.toLowerCase().includes(term);
      const matchesCategory =
        selectedCategory === 'ALL' ||
        p?.category?.name === selectedCategory ||
        p?.category?.categoryName === selectedCategory ||
        p?.category?.nam === selectedCategory;
      return matchesTerm && matchesCategory;
    });
  }, [products, search, selectedCategory]);

  useEffect(() => {
    const bootstrap = async () => {
      try {
        const ensuredUser = await ensureUser();
        await loadProducts();
        await loadCategories();
        await loadCart(ensuredUser);
      } catch (err) {
        console.error(err);
        setMessage('Nuk u inicializua frontendi: ' + err.message);
      } finally {
        setLoading(false);
      }
    };
    bootstrap();
  }, []);

  const apiRequest = async (path, options = {}) => {
    const res = await fetch(`${API_BASE}${path}`, {
      headers: { 'Content-Type': 'application/json' },
      ...options,
    });
    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(errorText || res.statusText);
    }
    return res.status === 204 ? null : res.json();
  };

  const ensureUser = async () => {
    const cached = localStorage.getItem('cartcloudUser');
    if (cached) {
      const parsed = JSON.parse(cached);
      setUser(parsed);
      return parsed;
    }
    try {
      const loggedIn = await apiRequest('/users/login', {
        method: 'POST',
        body: JSON.stringify({ email: DEMO_EMAIL, password: DEMO_PASSWORD }),
      });
      setUser(loggedIn);
      localStorage.setItem('cartcloudUser', JSON.stringify(loggedIn));
      return loggedIn;
    } catch (err) {
      const created = await apiRequest('/users', {
        method: 'POST',
        body: JSON.stringify({
          name: 'Demo User',
          email: DEMO_EMAIL,
          password: DEMO_PASSWORD,
          role: 'CUSTOMER',
        }),
      });
      setUser(created);
      localStorage.setItem('cartcloudUser', JSON.stringify(created));
      return created;
    }
  };

  const loadProducts = async () => {
    try {
      const data = await apiRequest('/products');
      setProducts(data);
    } catch (err) {
      console.warn('Ska produkte nga API, po shfaqim shembull', err);
      setProducts([
        {
          productId: 1,
          name: 'Cloud Hoodie',
          description: 'Më e butë se reja dhe perfekte për çdo ditë.',
          price: 49.99,
          imageUrl:
            'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=800&q=80',
          category: { name: 'Veshje' },
        },
        {
          productId: 2,
          name: 'CartCloud Mug',
          description: 'Kafe me stil, direkt nga reja.',
          price: 14.99,
          imageUrl:
            'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80',
          category: { name: 'Aksesore' },
        },
      ]);
    }
  };

  const loadCategories = async () => {
    try {
      const data = await apiRequest('/categories');
      const normalized = data.map((c) => ({ name: c.name || c.categoryName || c.nam }));
      setCategories([{ name: 'ALL' }, ...normalized]);
    } catch (err) {
      const derived = Array.from(
        new Set(
          products
            .map((p) => p?.category?.name || p?.category?.categoryName || p?.category?.nam)
            .filter(Boolean),
        ),
      ).map((name) => ({ name }));
      setCategories([{ name: 'ALL' }, ...derived]);
    }
  };

  const loadCart = async (currentUser = user) => {
    if (!currentUser) return;
    try {
      const data = await apiRequest(`/cart/${currentUser.userId}`);
      setCart(data);
    } catch (err) {
      console.warn('Cart fallback to empty', err);
      setCart({ items: [], totalPrice: 0 });
    }
  };

  const handleAddToCart = async (productId) => {
    if (!user) return;
    try {
      const updated = await apiRequest(`/cart/${user.userId}/items?productId=${productId}&quantity=1`, {
        method: 'POST',
      });
      setCart(updated);
      setMessage('Produkti u shtua në shportë.');
      setShowCart(true);
    } catch (err) {
      setMessage('Shtimi në shportë dështoi: ' + err.message);
    }
  };

  const handleRemoveItem = async (itemId) => {
    if (!user) return;
    try {
      const updated = await apiRequest(`/cart/${user.userId}/items/${itemId}`, { method: 'DELETE' });
      setCart(updated);
    } catch (err) {
      setMessage('Heqja dështoi: ' + err.message);
    }
  };

  const handleCheckout = async () => {
    if (!user) return;
    try {
      const order = await apiRequest(`/orders/checkout?userId=${user.userId}&paymentMethod=CARD`, {
        method: 'POST',
      });
      setLastOrder(order);
      await loadCart(user);
      setMessage('Porosia u finalizua me sukses.');
    } catch (err) {
      setMessage('Checkout dështoi: ' + err.message);
    }
  };

  return (
    <div className="page">
      <header className="topbar">
        <div className="brand">
          <span className="brand-mark">☁️</span>
          <div>
            <div className="brand-title">CartCloud</div>
            <div className="brand-subtitle">Shporta juaj në re</div>
          </div>
        </div>
        <div className="search">
          <input
            type="text"
            placeholder="Kërko produkt..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="actions">
          <button className="ghost" onClick={() => setShowCart((v) => !v)}>
            Shporta ({cart.items?.length || 0})
          </button>
          <div className="user-chip">
            <span>{user?.email || 'I ftuar'}</span>
          </div>
        </div>
      </header>

      <main>
        <section className="hero">
          <div>
            <p className="eyebrow">E-commerce • Dynamic</p>
            <h1>Një vitrinë elegante për produktet tuaja</h1>
            <p className="lede">
              Shfletoni, shtoni në shportë dhe përfundoni porosinë në pak klikime. UI e lehtë,
              performante dhe gati për prodhim.
            </p>
            <div className="hero-actions">
              <button onClick={() => setShowCart(true)}>Shiko shportën</button>
              <button className="ghost" onClick={loadProducts}>
                Rifresko produktet
              </button>
            </div>
            {message && <div className="toast">{message}</div>}
          </div>
          <div className="hero-card">
            <div className="stat">
              <span>Total artikuj</span>
              <strong>{cart.items?.length || 0}</strong>
            </div>
            <div className="stat">
              <span>Vlera e shportës</span>
              <strong>€{formatPrice(cart.totalPrice)}</strong>
            </div>
            {lastOrder && (
              <div className="stat success">
                <span>Porosia e fundit</span>
                <strong>€{formatPrice(lastOrder.totalAmount)}</strong>
              </div>
            )}
          </div>
        </section>

        <section className="filters">
          {categories.map((c) => (
            <button
              key={c.name || c.categoryName || c.nam}
              className={
                selectedCategory === (c.name || c.categoryName || c.nam) ? 'pill active' : 'pill'
              }
              onClick={() => setSelectedCategory(c.name || c.categoryName || c.nam || 'ALL')}
            >
              {c.name || c.categoryName || c.nam || 'ALL'}
            </button>
          ))}
        </section>

        <section className="grid">
          {loading && <div className="empty">Duke ngarkuar...</div>}
          {!loading && filteredProducts.length === 0 && <div className="empty">Asnjë produkt</div>}
          {filteredProducts.map((product) => (
            <article key={product.productId} className="card">
              <div
                className="image"
                style={{
                  backgroundImage: `url(${product.imageUrl || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80'})`,
                }}
              />
              <div className="card-body">
                <div className="card-head">
                  <div>
                    <h3>{product.name}</h3>
                    <p className="muted">
                      {product.category?.name || product.category?.categoryName || product.category?.nam}
                    </p>
                  </div>
                  <div className="price">€{formatPrice(product.price)}</div>
                </div>
                <p className="description">{product.description}</p>
                <button onClick={() => handleAddToCart(product.productId)}>Shto në shportë</button>
              </div>
            </article>
          ))}
        </section>
      </main>

      <aside className={showCart ? 'cart open' : 'cart'}>
        <header>
          <h2>Shporta</h2>
          <button className="ghost" onClick={() => setShowCart(false)}>
            Mbyll
          </button>
        </header>
        <div className="cart-items">
          {cart.items && cart.items.length > 0 ? (
            cart.items.map((item) => (
              <div key={item.id} className="cart-row">
                <div>
                  <div className="cart-title">{item.product?.name}</div>
                  <div className="muted">
                    {item.quantity} x €{formatPrice(item.product?.price || item.price)}
                  </div>
                </div>
                <div className="cart-actions">
                  <strong>€{formatPrice((item.product?.price || item.price || 0) * item.quantity)}</strong>
                  <button className="ghost" onClick={() => handleRemoveItem(item.id)}>
                    Hiq
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="empty">Shporta është bosh</div>
          )}
        </div>
        <div className="cart-footer">
          <div>
            <span className="muted">Totali</span>
            <div className="price">€{formatPrice(cart.totalPrice)}</div>
          </div>
          <button disabled={!cart.items || cart.items.length === 0} onClick={handleCheckout}>
            Finalizo porosinë
          </button>
        </div>
      </aside>
    </div>
  );
}

export default App;
