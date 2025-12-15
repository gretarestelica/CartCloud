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
  const [orders, setOrders] = useState([]);
  const [initialLoading, setInitialLoading] = useState(true);
  const [productsLoading, setProductsLoading] = useState(false);
  const [productsError, setProductsError] = useState('');
  const [cartLoading, setCartLoading] = useState(false);
  const [cartError, setCartError] = useState('');
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [ordersError, setOrdersError] = useState('');
  const [message, setMessage] = useState('');
  const [showCart, setShowCart] = useState(false);
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [activeSection, setActiveSection] = useState('products'); // products | orders | profile
  const [authMode, setAuthMode] = useState('login'); // login | register
  const [authForm, setAuthForm] = useState({ name: '', email: '', password: '' });
  const [authErrors, setAuthErrors] = useState({});
  const [authServerError, setAuthServerError] = useState('');
  const [checkoutForm, setCheckoutForm] = useState({
    name: '',
    street: '',
    city: '',
    state: '',
    country: '',
    postalCode: '',
    paymentMethod: 'CARD',
  });
  const [checkoutErrors, setCheckoutErrors] = useState({});
  const [checkoutServerError, setCheckoutServerError] = useState('');

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
        await Promise.all([
          loadProducts(),
          loadCategories(),
          loadCart(ensuredUser),
          loadOrders(ensuredUser),
        ]);
      } catch (err) {
        console.error(err);
        setMessage('Nuk u inicializua frontendi: ' + err.message);
      } finally {
        setInitialLoading(false);
      }
    };
    bootstrap();
  }, []);

  const apiRequest = async (path, options = {}) => {
    const res = await fetch(`${API_BASE}${path}`, {
      headers: { 'Content-Type': 'application/json' },
      ...options,
    });
    const text = await res.text();
    const isJson = text && (text.startsWith('{') || text.startsWith('['));
    const data = text ? (isJson ? JSON.parse(text) : text) : null;
    if (!res.ok) {
      const message =
        typeof data === 'string'
          ? data
          : data?.message || data?.error || res.statusText || 'Kërkesa dështoi';
      throw new Error(message);
    }
    return data;
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
    setProductsLoading(true);
    setProductsError('');
    try {
      const data = await apiRequest('/products');
      setProducts(data);
    } catch (err) {
      console.warn('Ska produkte nga API, po shfaqim shembull', err);
      setProductsError('Nuk u arrit të ngarkohen produktet. Po shfaqim disa shembuj.');
      setProducts([
        {
          productId: 1,
          name: 'Cloud Hoodie',
          description: 'Më e butë se reja dhe perfekte për çdo ditë.',
          price: 49.99,
          imageUrl:
            'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=600&q=70',
          category: { name: 'Veshje' },
        },
        {
          productId: 2,
          name: 'CartCloud Mug',
          description: 'Kafe me stil, direkt nga reja.',
          price: 14.99,
          imageUrl:
            'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=600&q=70',
          category: { name: 'Aksesore' },
        },
      ]);
    } finally {
      setProductsLoading(false);
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
    setCartLoading(true);
    setCartError('');
    try {
      const data = await apiRequest(`/cart/${currentUser.userId}`);
      setCart(data);
    } catch (err) {
      console.warn('Cart fallback to empty', err);
      setCartError('Nuk u arrit të ngarkohet shporta. Po shfaqim një shportë bosh.');
      setCart({ items: [], totalPrice: 0 });
    } finally {
      setCartLoading(false);
    }
  };

  const loadOrders = async (currentUser = user) => {
    if (!currentUser) return;
    setOrdersLoading(true);
    setOrdersError('');
    try {
      const data = await apiRequest(`/orders/user/${currentUser.userId}`);
      setOrders(data);
    } catch (err) {
      console.warn('Ska porosi ende', err);
      setOrdersError('Nuk u arrit të ngarkohen porositë.');
      setOrders([]);
    } finally {
      setOrdersLoading(false);
    }
  };

  const handleAddToCart = async (productId) => {
    if (!user) return;
    try {
      setCartLoading(true);
      const updated = await apiRequest(`/cart/${user.userId}/items?productId=${productId}&quantity=1`, {
        method: 'POST',
      });
      setCart(updated);
      setMessage('Produkti u shtua në shportë.');
      setShowCart(true);
    } catch (err) {
      setMessage('Shtimi në shportë dështoi: ' + err.message);
      setCartError('Shtimi në shportë dështoi: ' + err.message);
    } finally {
      setCartLoading(false);
    }
  };

  const handleRemoveItem = async (itemId) => {
    if (!user) return;
    try {
      setCartLoading(true);
      const updated = await apiRequest(`/cart/${user.userId}/items/${itemId}`, { method: 'DELETE' });
      setCart(updated);
    } catch (err) {
      setMessage('Heqja dështoi: ' + err.message);
      setCartError('Heqja e artikullit dështoi: ' + err.message);
    } finally {
      setCartLoading(false);
    }
  };

  const handleCheckout = async () => {
    if (!user) return;
    const errs = {};
    if (!checkoutForm.name.trim()) errs.name = 'Emri është i detyrueshëm';
    if (!checkoutForm.street.trim()) errs.street = 'Rruga është e detyrueshme';
    if (!checkoutForm.city.trim()) errs.city = 'Qyteti është i detyrueshëm';
    if (!checkoutForm.country.trim()) errs.country = 'Shteti është i detyrueshëm';
    if (!checkoutForm.postalCode.trim()) errs.postalCode = 'Kodi postar është i detyrueshëm';
    if (!checkoutForm.paymentMethod) errs.paymentMethod = 'Zgjidh një metodë pagese';
    setCheckoutErrors(errs);
    if (Object.keys(errs).length > 0) {
      setCheckoutServerError('Plotëso fushat e detyrueshme përpara se të vazhdosh.');
      return;
    }
    try {
      setCheckoutServerError('');
      setCartLoading(true);
      const order = await apiRequest(
        `/orders/checkout?userId=${user.userId}&paymentMethod=${checkoutForm.paymentMethod}`,
        {
          method: 'POST',
        },
      );
      setLastOrder(order);
      await loadCart(user);
      await loadOrders(user);
      setMessage('Porosia u finalizua me sukses.');
    } catch (err) {
      setCheckoutServerError(err.message);
    } finally {
      setCartLoading(false);
    }
  };

  const validateEmail = (email) =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.toLowerCase());

  const handleAuthSubmit = async (e) => {
    e.preventDefault();
    const errs = {};
    if (!authForm.email.trim()) errs.email = 'Email është i detyrueshëm';
    else if (!validateEmail(authForm.email)) errs.email = 'Email nuk është i vlefshëm';

    if (!authForm.password.trim()) errs.password = 'Fjalëkalimi është i detyrueshëm';
    else if (authForm.password.length < 6)
      errs.password = 'Fjalëkalimi duhet të ketë të paktën 6 karaktere';

    if (authMode === 'register') {
      if (!authForm.name.trim()) errs.name = 'Emri është i detyrueshëm';
    }

    setAuthErrors(errs);
    if (Object.keys(errs).length > 0) return;

    try {
      setAuthServerError('');
      if (authMode === 'login') {
        const loggedIn = await apiRequest('/users/login', {
          method: 'POST',
          body: JSON.stringify({
            email: authForm.email,
            password: authForm.password,
          }),
        });
        setUser(loggedIn);
        localStorage.setItem('cartcloudUser', JSON.stringify(loggedIn));
      } else {
        const created = await apiRequest('/users', {
          method: 'POST',
          body: JSON.stringify({
            name: authForm.name,
            email: authForm.email,
            password: authForm.password,
            role: 'CUSTOMER',
          }),
        });
        setUser(created);
        localStorage.setItem('cartcloudUser', JSON.stringify(created));
        setAuthMode('login');
      }
      setMessage('Përdoruesi u autentikua me sukses.');
    } catch (err) {
      setAuthServerError(err.message);
    }
  };

  return (
    <div className="page">
      <a href="#main-content" className="skip-link">
        Kalo te përmbajtja kryesore
      </a>
      <header className="topbar" role="banner">
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
            aria-label="Kërko produkte"
          />
        </div>
        <div className="actions">
          <div className="tabs" role="tablist" aria-label="Navigimi i seksioneve">
            <button
              className={activeSection === 'products' ? 'ghost tab active' : 'ghost tab'}
              onClick={() => setActiveSection('products')}
              role="tab"
              aria-selected={activeSection === 'products'}
            >
              Produkte
            </button>
            <button
              className={activeSection === 'orders' ? 'ghost tab active' : 'ghost tab'}
              onClick={() => setActiveSection('orders')}
              role="tab"
              aria-selected={activeSection === 'orders'}
            >
              Porositë
            </button>
            <button
              className={activeSection === 'profile' ? 'ghost tab active' : 'ghost tab'}
              onClick={() => setActiveSection('profile')}
              role="tab"
              aria-selected={activeSection === 'profile'}
            >
              Profili
            </button>
          </div>
          <button
            className="ghost"
            onClick={() => setShowCart((v) => !v)}
            aria-pressed={showCart}
            aria-label="Hape ose mbyll shportën"
          >
            Shporta ({cart.items?.length || 0})
          </button>
          <div className="user-chip">
            <span>{user?.email || 'I ftuar'}</span>
          </div>
        </div>
      </header>

      <main id="main-content" role="main">
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
            {initialLoading && (
              <div className="banner loading" aria-live="polite">
                Po ngarkojmë të dhënat kryesore...
              </div>
            )}
            {productsError && (
              <div className="banner error" role="alert">
                {productsError}
              </div>
            )}
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
        {activeSection === 'products' && (
          <>
            <section
              className="filters"
              aria-label="Filtra të kategorive"
            >
              {categories.map((c) => (
                <button
                  key={c.name || c.categoryName || c.nam}
                  className={
                    selectedCategory === (c.name || c.categoryName || c.nam)
                      ? 'pill active'
                      : 'pill'
                  }
                  onClick={() =>
                    setSelectedCategory(c.name || c.categoryName || c.nam || 'ALL')
                  }
                >
                  {c.name || c.categoryName || c.nam || 'ALL'}
                </button>
              ))}
            </section>

            <section className="grid" aria-label="Lista e produkteve">
              {productsLoading &&
                Array.from({ length: 4 }).map((_, idx) => (
                  <article key={idx} className="card skeleton">
                    <div className="image shimmer" />
                    <div className="card-body">
                      <div className="skeleton-line w-60" />
                      <div className="skeleton-line w-40" />
                      <div className="skeleton-line w-80" />
                    </div>
                  </article>
                ))}
              {!productsLoading && filteredProducts.length === 0 && (
                <div className="empty">Asnjë produkt</div>
              )}
              {!productsLoading &&
                filteredProducts.map((product) => (
                <article key={product.productId} className="card">
                  <div className="image">
                    <img
                      loading="lazy"
                      src={
                        product.imageUrl ||
                        'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=70'
                      }
                      alt={product.name || 'Produkt'}
                    />
                  </div>
                  <div className="card-body">
                    <div className="card-head">
                      <div>
                        <h3>{product.name}</h3>
                        <p className="muted">
                          {product.category?.name ||
                            product.category?.categoryName ||
                            product.category?.nam}
                        </p>
                      </div>
                      <div className="price">€{formatPrice(product.price)}</div>
                    </div>
                    <p className="description">{product.description}</p>
                    <button onClick={() => handleAddToCart(product.productId)}>
                      Shto në shportë
                    </button>
                  </div>
                </article>
              ))}
            </section>
          </>
        )}

        {activeSection === 'orders' && (
          <section className="panel" aria-label="Porositë e mia">
            <h2>Porositë e mia</h2>
            {ordersLoading && (
              <div className="empty">Duke ngarkuar porositë...</div>
            )}
            {ordersError && !ordersLoading && (
              <div className="banner error" role="alert">
                {ordersError}
              </div>
            )}
            {orders.length === 0 && !ordersLoading && (
              <div className="empty">Nuk ka porosi ende.</div>
            )}
            <div className="orders-list">
              {orders.map((order) => (
                <article key={order.orderId} className="order-card">
                  <div className="order-head">
                    <div>
                      <div className="muted">
                        ID: <strong>{order.orderId}</strong>
                      </div>
                      <div className="muted">
                        Status: <strong>{order.status}</strong>
                      </div>
                    </div>
                    <div className="price">€{formatPrice(order.totalAmount)}</div>
                  </div>
                  <div className="order-items">
                    {order.orderItems?.map((oi) => (
                      <div key={oi.orderItemId} className="order-row">
                        <span>{oi.product?.name}</span>
                        <span className="muted">
                          {oi.quantity} x €{formatPrice(oi.unitPrice)}
                        </span>
                        <span>€{formatPrice(oi.lineTotal)}</span>
                      </div>
                    ))}
                  </div>
                </article>
              ))}
            </div>
          </section>
        )}

        {activeSection === 'profile' && (
          <section className="panel" aria-label="Profili dhe autentikimi">
            <h2>Profili i përdoruesit</h2>
            <div className="profile-grid">
              <div>
                <h3>Kredencialet</h3>
                <form className="form" onSubmit={handleAuthSubmit} noValidate>
                  {authMode === 'register' && (
                    <div className="field">
                      <label htmlFor="auth-name">
                        Emri <span className="required">*</span>
                      </label>
                      <input
                        id="auth-name"
                        type="text"
                        value={authForm.name}
                        onChange={(e) =>
                          setAuthForm((f) => ({ ...f, name: e.target.value }))
                        }
                        aria-invalid={Boolean(authErrors.name)}
                        aria-describedby={authErrors.name ? 'auth-name-error' : undefined}
                      />
                      {authErrors.name && (
                        <div id="auth-name-error" className="field-error">
                          {authErrors.name}
                        </div>
                      )}
                    </div>
                  )}
                  <div className="field">
                    <label htmlFor="auth-email">
                      Email <span className="required">*</span>
                    </label>
                    <input
                      id="auth-email"
                      type="email"
                      value={authForm.email}
                      onChange={(e) =>
                        setAuthForm((f) => ({ ...f, email: e.target.value }))
                      }
                      aria-invalid={Boolean(authErrors.email)}
                      aria-describedby={authErrors.email ? 'auth-email-error' : undefined}
                    />
                    {authErrors.email && (
                      <div id="auth-email-error" className="field-error">
                        {authErrors.email}
                      </div>
                    )}
                  </div>
                  <div className="field">
                    <label htmlFor="auth-password">
                      Fjalëkalimi <span className="required">*</span>
                    </label>
                    <input
                      id="auth-password"
                      type="password"
                      value={authForm.password}
                      onChange={(e) =>
                        setAuthForm((f) => ({ ...f, password: e.target.value }))
                      }
                      aria-invalid={Boolean(authErrors.password)}
                      aria-describedby={authErrors.password ? 'auth-password-error' : undefined}
                    />
                    {authErrors.password && (
                      <div id="auth-password-error" className="field-error">
                        {authErrors.password}
                      </div>
                    )}
                  </div>
                  {authServerError && (
                    <div className="form-error" role="alert">
                      {authServerError}
                    </div>
                  )}
                  <div className="form-actions">
                    <button type="submit">
                      {authMode === 'login' ? 'Hyr' : 'Regjistrohu'}
                    </button>
                    <button
                      type="button"
                      className="ghost"
                      onClick={() =>
                        setAuthMode((m) => (m === 'login' ? 'register' : 'login'))
                      }
                    >
                      {authMode === 'login'
                        ? 'Nuk ke llogari? Regjistrohu'
                        : 'Ke llogari? Hyr'}
                    </button>
                  </div>
                </form>
              </div>
              <div>
                <h3>Detajet e profilit</h3>
                {user ? (
                  <>
                    <div className="profile-row">
                      <span className="muted">Emri</span>
                      <span>{user.name}</span>
                    </div>
                    <div className="profile-row">
                      <span className="muted">Email</span>
                      <span>{user.email}</span>
                    </div>
                    <div className="profile-row">
                      <span className="muted">Roli</span>
                      <span>{user.role}</span>
                    </div>
                    <div className="profile-row">
                      <span className="muted">Statusi</span>
                      <span>{user.accountStatus}</span>
                    </div>
                  </>
                ) : (
                  <div className="empty">Nuk ka përdorues aktiv.</div>
                )}
              </div>
            </div>
          </section>
        )}
      </main>

      <aside
        className={showCart ? 'cart open' : 'cart'}
        role="dialog"
        aria-modal="true"
        aria-labelledby="cart-title"
      >
        <header>
          <h2 id="cart-title">Shporta</h2>
          <button className="ghost" onClick={() => setShowCart(false)}>
            Mbyll
          </button>
        </header>
        <div className="cart-items">
          {cartLoading && (
            <div className="empty">Duke përditësuar shportën...</div>
          )}
          {cartError && !cartLoading && (
            <div className="banner error" role="alert">
              {cartError}
            </div>
          )}
          {cart.items && cart.items.length > 0 && !cartLoading ? (
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
          ) : !cartLoading ? (
            <div className="empty">Shporta është bosh</div>
          ) : null}
        </div>
        <div className="cart-footer">
          <div>
            <span className="muted">Totali</span>
            <div className="price">€{formatPrice(cart.totalPrice)}</div>
          </div>
          <button
            disabled={!cart.items || cart.items.length === 0}
            onClick={handleCheckout}
          >
            Finalizo porosinë
          </button>
        </div>
        <div className="checkout-form" aria-label="Detajet e dërgesës">
          <h3>Detajet e dërgesës</h3>
          <div className="field">
            <label htmlFor="checkout-name">
              Emri i marrësit <span className="required">*</span>
            </label>
            <input
              id="checkout-name"
              type="text"
              value={checkoutForm.name}
              onChange={(e) =>
                setCheckoutForm((f) => ({ ...f, name: e.target.value }))
              }
              aria-invalid={Boolean(checkoutErrors.name)}
              aria-describedby={checkoutErrors.name ? 'checkout-name-error' : undefined}
            />
            {checkoutErrors.name && (
              <div id="checkout-name-error" className="field-error">
                {checkoutErrors.name}
              </div>
            )}
          </div>
          <div className="field">
            <label htmlFor="checkout-street">
              Rruga <span className="required">*</span>
            </label>
            <input
              id="checkout-street"
              type="text"
              value={checkoutForm.street}
              onChange={(e) =>
                setCheckoutForm((f) => ({ ...f, street: e.target.value }))
              }
              aria-invalid={Boolean(checkoutErrors.street)}
              aria-describedby={checkoutErrors.street ? 'checkout-street-error' : undefined}
            />
            {checkoutErrors.street && (
              <div id="checkout-street-error" className="field-error">
                {checkoutErrors.street}
              </div>
            )}
          </div>
          <div className="field">
            <label htmlFor="checkout-city">
              Qyteti <span className="required">*</span>
            </label>
            <input
              id="checkout-city"
              type="text"
              value={checkoutForm.city}
              onChange={(e) =>
                setCheckoutForm((f) => ({ ...f, city: e.target.value }))
              }
              aria-invalid={Boolean(checkoutErrors.city)}
              aria-describedby={checkoutErrors.city ? 'checkout-city-error' : undefined}
            />
            {checkoutErrors.city && (
              <div id="checkout-city-error" className="field-error">
                {checkoutErrors.city}
              </div>
            )}
          </div>
          <div className="field-row">
            <div className="field">
              <label htmlFor="checkout-state">Shteti</label>
              <input
                id="checkout-state"
                type="text"
                value={checkoutForm.state}
                onChange={(e) =>
                  setCheckoutForm((f) => ({ ...f, state: e.target.value }))
                }
              />
            </div>
            <div className="field">
              <label htmlFor="checkout-country">
                Vendi <span className="required">*</span>
              </label>
              <input
                id="checkout-country"
                type="text"
                value={checkoutForm.country}
                onChange={(e) =>
                  setCheckoutForm((f) => ({ ...f, country: e.target.value }))
                }
                aria-invalid={Boolean(checkoutErrors.country)}
                aria-describedby={
                  checkoutErrors.country ? 'checkout-country-error' : undefined
                }
              />
              {checkoutErrors.country && (
                <div id="checkout-country-error" className="field-error">
                  {checkoutErrors.country}
                </div>
              )}
            </div>
          </div>
          <div className="field-row">
            <div className="field">
              <label htmlFor="checkout-postal">
                Kodi postar <span className="required">*</span>
              </label>
              <input
                id="checkout-postal"
                type="text"
                value={checkoutForm.postalCode}
                onChange={(e) =>
                  setCheckoutForm((f) => ({ ...f, postalCode: e.target.value }))
                }
                aria-invalid={Boolean(checkoutErrors.postalCode)}
                aria-describedby={
                  checkoutErrors.postalCode ? 'checkout-postal-error' : undefined
                }
              />
              {checkoutErrors.postalCode && (
                <div id="checkout-postal-error" className="field-error">
                  {checkoutErrors.postalCode}
                </div>
              )}
            </div>
            <div className="field">
              <label htmlFor="checkout-payment">
                Metoda e pagesës <span className="required">*</span>
              </label>
              <select
                id="checkout-payment"
                value={checkoutForm.paymentMethod}
                onChange={(e) =>
                  setCheckoutForm((f) => ({
                    ...f,
                    paymentMethod: e.target.value,
                  }))
                }
                aria-invalid={Boolean(checkoutErrors.paymentMethod)}
                aria-describedby={
                  checkoutErrors.paymentMethod ? 'checkout-payment-error' : undefined
                }
              >
                <option value="CARD">Kartë</option>
                <option value="CASH_ON_DELIVERY">Kesh në dorëzim</option>
              </select>
              {checkoutErrors.paymentMethod && (
                <div id="checkout-payment-error" className="field-error">
                  {checkoutErrors.paymentMethod}
                </div>
              )}
            </div>
          </div>
          {checkoutServerError && (
            <div className="form-error" role="alert">
              {checkoutServerError}
            </div>
          )}
        </div>
      </aside>
    </div>
  );
}

export default App;
