/* =========================================================
   Təzə Bazar — frontend
   Backend eyni origin-dən gözlənilir (Spring Boot statik resurs
   kimi bu faylları /src/main/resources/static altına qoysan,
   API_BASE-i boş saxlaya bilərsən). Ayrı porta çıxarsan, aşağıda
   API_BASE-i "http://localhost:8080" kimi dəyiş və backend-ə
   CORS konfiqurasiyası əlavə et.
========================================================= */
const API_BASE = "";

const CATEGORIES = [
  { value: "DAIRY_PRODUCTS",     label: "Süd məhsulları" },
  { value: "MEAT",                label: "Ət məhsulları" },
  { value: "VEGETABLES",          label: "Tərəvəz" },
  { value: "FRUITS",              label: "Meyvə" },
  { value: "BAKERY",              label: "Çörək-bulka" },
  { value: "BEVERAGES",           label: "İçkilər" },
  { value: "SNACKS",              label: "Qəlyanaltı" },
  { value: "FROZEN_FOOD",         label: "Dondurulmuş qida" },
  { value: "CANNED_FOOD",         label: "Konservlər" },
  { value: "CLEANING_PRODUCTS",   label: "Təmizlik məhsulları" },
  { value: "PERSONAL_CARE",       label: "Şəxsi qulluq" },
];
const catLabel = (v) => CATEGORIES.find(c => c.value === v)?.label || v;

/* ---------------- state ---------------- */
const state = {
  user: JSON.parse(localStorage.getItem("tb_user") || "null"),
  products: [],
  activeCategory: null,
  searchActive: false,
  basketItems: [],
  favoriteIds: new Set(),
};

/* ---------------- generic API helper ---------------- */
async function api(path, options = {}) {
  const res = await fetch(API_BASE + path, {
    headers: options.body ? { "Content-Type": "application/json" } : {},
    ...options,
  });
  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = text; }
  if (!res.ok) {
    const msg = (data && data.message) ? data.message : (typeof data === "string" ? data : `Xəta (${res.status})`);
    throw new Error(msg);
  }
  return data;
}

/* ---------------- toast ---------------- */
let toastTimer;
function toast(msg, isError = false) {
  const el = document.getElementById("toast");
  el.textContent = msg;
  el.classList.toggle("is-error", isError);
  el.classList.add("is-show");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => el.classList.remove("is-show"), 2600);
}

/* ---------------- tabs ---------------- */
document.getElementById("tabs").addEventListener("click", (e) => {
  const btn = e.target.closest(".tab");
  if (!btn) return;
  switchTab(btn.dataset.tab);
});
function switchTab(name) {
  document.querySelectorAll(".tab").forEach(t => t.classList.toggle("is-active", t.dataset.tab === name));
  document.querySelectorAll(".view").forEach(v => v.classList.toggle("is-active", v.id === `view-${name}`));
  if (name === "basket") loadBasket();
  if (name === "favorites") loadFavorites();
  if (name === "admin") loadAdminTable();
}

/* =========================================================
   USER (login-lite: select or create, no auth backend exists)
========================================================= */
const userModal = document.getElementById("userModal");
document.getElementById("userChip").addEventListener("click", openUserModal);

async function openUserModal() {
  userModal.classList.add("is-open");
  document.getElementById("modalError").textContent = "";
  try {
    const users = await api("/users");
    const select = document.getElementById("userSelect");
    select.innerHTML = users.map(u =>
      `<option value="${u.id}">${u.name} ${u.surname} — ${u.email}</option>`
    ).join("") || `<option disabled selected>İstifadəçi tapılmadı</option>`;
  } catch (err) {
    document.getElementById("modalError").textContent = "İstifadəçilər yüklənmədi: " + err.message;
  }
}

document.getElementById("useSelectedUserBtn").addEventListener("click", async () => {
  const select = document.getElementById("userSelect");
  if (!select.value) return;
  try {
    const users = await api("/users");
    const u = users.find(x => String(x.id) === select.value);
    if (u) setCurrentUser(u);
  } catch (err) {
    document.getElementById("modalError").textContent = err.message;
  }
});

document.getElementById("newUserForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const body = {
    name: nu_name.value.trim(),
    surname: nu_surname.value.trim(),
    age: Number(nu_age.value),
    phone: nu_phone.value.trim(),
    email: nu_email.value.trim(),
    balance: Number(nu_balance.value),
  };
  try {
    const created = await api("/users", { method: "POST", body: JSON.stringify(body) });
    setCurrentUser(created);
    e.target.reset();
  } catch (err) {
    document.getElementById("modalError").textContent = err.message;
  }
});

function setCurrentUser(u) {
  state.user = u;
  localStorage.setItem("tb_user", JSON.stringify(u));
  userModal.classList.remove("is-open");
  refreshUserChip();
  loadBasket();
  loadFavorites();
  toast(`Xoş gördük, ${u.name}!`);
}
function refreshUserChip() {
  document.getElementById("userChipLabel").textContent =
    state.user ? `${state.user.name} ${state.user.surname}` : "İstifadəçi seç";
}
function requireUser() {
  if (!state.user) { openUserModal(); return false; }
  return true;
}

/* =========================================================
   STORE (product grid, filter, search)
========================================================= */
function renderCategoryChips() {
  const row = document.getElementById("categoryChips");
  const allChip = `<button class="chip ${!state.activeCategory ? "is-active" : ""}" data-cat="">Hamısı</button>`;
  const chips = CATEGORIES.map(c =>
    `<button class="chip ${state.activeCategory === c.value ? "is-active" : ""}" data-cat="${c.value}">${c.label}</button>`
  ).join("");
  row.innerHTML = allChip + chips;
}
document.getElementById("categoryChips").addEventListener("click", (e) => {
  const chip = e.target.closest(".chip");
  if (!chip) return;
  state.activeCategory = chip.dataset.cat || null;
  state.searchActive = false;
  document.getElementById("searchInput").value = "";
  document.getElementById("clearSearchBtn").hidden = true;
  loadProducts();
});

async function loadProducts() {
  try {
    let products;
    if (state.activeCategory) {
      products = await api(`/products/category/${state.activeCategory}`);
    } else {
      products = await api("/products");
    }
    state.products = products;
    renderCategoryChips();
    renderGrid(products, "productGrid", "storeEmpty");
  } catch (err) {
    toast("Məhsullar yüklənmədi: " + err.message, true);
  }
}

document.getElementById("searchBtn").addEventListener("click", runSearch);
document.getElementById("searchInput").addEventListener("keydown", (e) => { if (e.key === "Enter") runSearch(); });
document.getElementById("clearSearchBtn").addEventListener("click", () => {
  document.getElementById("searchInput").value = "";
  document.getElementById("clearSearchBtn").hidden = true;
  state.searchActive = false;
  loadProducts();
});
async function runSearch() {
  const q = document.getElementById("searchInput").value.trim();
  if (!q) return loadProducts();
  try {
    const result = await api(`/products/search/${encodeURIComponent(q)}`);
    state.searchActive = true;
    document.getElementById("clearSearchBtn").hidden = false;
    renderGrid(result ? [result] : [], "productGrid", "storeEmpty");
  } catch (err) {
    renderGrid([], "productGrid", "storeEmpty");
    toast("Nəticə tapılmadı: " + err.message, true);
  }
}

function productCard(p, { showFavToggle = true } = {}) {
  const isFav = state.favoriteIds.has(p.id);
  const img = p.imgURL
    ? `<img class="card__img" src="${p.imgURL}" alt="${p.name}" onerror="this.replaceWith(Object.assign(document.createElement('div'),{className:'card__img card__img--placeholder',textContent:'TB'}))">`
    : `<div class="card__img card__img--placeholder">TB</div>`;
  return `
  <div class="card" data-id="${p.id}">
    ${img}
    <div class="card__body">
      <span class="card__cat">${catLabel(p.category)}</span>
      <span class="card__name">${p.name}</span>
      <div class="card__meta">
        <span class="card__price">${Number(p.price).toFixed(2)} ₼</span>
        <span class="card__stock ${p.stock <= 5 ? "is-low" : ""}">${p.stock} ədəd</span>
      </div>
      <div class="card__actions">
        <button class="primary-btn" data-action="add-basket" data-id="${p.id}" ${p.stock === 0 ? "disabled" : ""}>Səbətə at</button>
        ${showFavToggle ? `<button class="icon-btn ${isFav ? "is-fav" : ""}" data-action="toggle-fav" data-id="${p.id}" title="Sevimlilərə əlavə et">${isFav ? "♥" : "♡"}</button>` : ""}
      </div>
    </div>
  </div>`;
}

function renderGrid(items, gridId, emptyId) {
  const grid = document.getElementById(gridId);
  const empty = document.getElementById(emptyId);
  grid.innerHTML = items.map(p => productCard(p)).join("");
  empty.hidden = items.length > 0;
}

/* delegated clicks for both store & favorites grids */
document.addEventListener("click", async (e) => {
  const btn = e.target.closest("[data-action]");
  if (!btn) return;
  const id = Number(btn.dataset.id);

  if (btn.dataset.action === "add-basket") {
    if (!requireUser()) return;
    try {
      await api(`/baskets?userId=${state.user.id}&productId=${id}&quantity=1`, { method: "POST" });
      toast("Səbətə əlavə olundu.");
      loadBasket();
    } catch (err) { toast(err.message, true); }
  }

  if (btn.dataset.action === "toggle-fav") {
    if (!requireUser()) return;
    try {
      if (state.favoriteIds.has(id)) {
        await api(`/favorites/${state.user.id}/items/${id}`, { method: "DELETE" });
        state.favoriteIds.delete(id);
        toast("Sevimlilərdən çıxarıldı.");
      } else {
        await api(`/favorites?userId=${state.user.id}&productId=${id}`, { method: "POST" });
        state.favoriteIds.add(id);
        toast("Sevimlilərə əlavə olundu.");
      }
      renderGrid(state.products, "productGrid", "storeEmpty");
      if (document.getElementById("view-favorites").classList.contains("is-active")) loadFavorites();
    } catch (err) { toast(err.message, true); }
  }

  if (btn.dataset.action === "remove-basket-item") {
    if (!requireUser()) return;
    try {
      await api(`/baskets/${state.user.id}/items/${id}`, { method: "DELETE" });
      toast("Məhsul səbətdən silindi.");
      loadBasket();
    } catch (err) { toast(err.message, true); }
  }

  if (btn.dataset.action === "remove-fav") {
    if (!requireUser()) return;
    try {
      await api(`/favorites/${state.user.id}/items/${id}`, { method: "DELETE" });
      state.favoriteIds.delete(id);
      toast("Sevimlilərdən çıxarıldı.");
      loadFavorites();
    } catch (err) { toast(err.message, true); }
  }

  if (btn.dataset.action === "edit-product") {
    const p = state.products.find(x => x.id === id) || (await api(`/products/${id}`));
    fillProductForm(p);
    switchTab("admin");
  }

  if (btn.dataset.action === "delete-product") {
    if (!confirm("Bu məhsulu silmək istəyirsən?")) return;
    try {
      await api(`/products/${id}`, { method: "DELETE" });
      toast("Məhsul silindi.");
      loadAdminTable();
      loadProducts();
    } catch (err) { toast(err.message, true); }
  }
});

/* =========================================================
   BASKET
========================================================= */
async function loadBasket() {
  if (!state.user) { renderBasket([]); return; }
  try {
    const items = await api(`/baskets/${state.user.id}`);
    state.basketItems = items;
    renderBasket(items);
  } catch (err) {
    toast("Səbət yüklənmədi: " + err.message, true);
  }
}

function renderBasket(items) {
  const list = document.getElementById("basketList");
  const empty = document.getElementById("basketEmpty");
  const badge = document.getElementById("basketBadge");

  badge.textContent = items.reduce((sum, it) => sum + (it.quantity || 0), 0);
  empty.hidden = items.length > 0;

  list.innerHTML = items.map(it => {
    const p = it.product || {};
    const img = p.imgURL
      ? `<img class="basket-item__img" src="${p.imgURL}" onerror="this.style.visibility='hidden'">`
      : `<div class="basket-item__img"></div>`;
    return `
    <div class="basket-item">
      ${img}
      <div class="basket-item__info">
        <div class="basket-item__name">${p.name || "—"}</div>
        <div class="basket-item__qty">${it.quantity} ədəd × ${Number(p.price || 0).toFixed(2)} ₼</div>
      </div>
      <div class="basket-item__price">${(Number(p.price || 0) * it.quantity).toFixed(2)} ₼</div>
      <button class="basket-item__remove" data-action="remove-basket-item" data-id="${p.id}" title="Sil">✕</button>
    </div>`;
  }).join("");

  const total = items.reduce((sum, it) => sum + Number(it.product?.price || 0) * it.quantity, 0);
  document.getElementById("summaryCount").textContent = items.reduce((s, it) => s + it.quantity, 0);
  document.getElementById("summaryTotal").textContent = total.toFixed(2) + " ₼";
  document.getElementById("summaryBalance").textContent = state.user ? Number(state.user.balance).toFixed(2) + " ₼" : "—";

  const payBtn = document.getElementById("payBtn");
  payBtn.disabled = items.length === 0;
  document.getElementById("basketHint").textContent =
    !state.user ? "Əvvəlcə istifadəçi seç." : (items.length === 0 ? "Səbətdə məhsul yoxdur." : "");
}

document.getElementById("payBtn").addEventListener("click", async () => {
  if (!requireUser()) return;
  try {
    await api(`/baskets/payment/${state.user.id}`, { method: "DELETE" });
    toast("Ödəniş uğurla tamamlandı!");
    loadBasket();
    try {
      const users = await api("/users");
      const fresh = users.find(u => u.id === state.user.id);
      if (fresh) setCurrentUser(fresh);
    } catch { /* balance refresh best-effort */ }
  } catch (err) {
    toast("Ödəniş alınmadı: " + err.message, true);
  }
});

/* =========================================================
   FAVORITES
========================================================= */
async function loadFavorites() {
  if (!state.user) { renderGrid([], "favoritesGrid", "favoritesEmpty"); return; }
  try {
    const favs = await api(`/favorites/${state.user.id}`);
    state.favoriteIds = new Set(favs.map(f => f.id));
    const grid = document.getElementById("favoritesGrid");
    grid.innerHTML = favs.map(p => `
      <div class="card" data-id="${p.id}">
        ${p.imgURL ? `<img class="card__img" src="${p.imgURL}" onerror="this.replaceWith(Object.assign(document.createElement('div'),{className:'card__img card__img--placeholder',textContent:'TB'}))">` : `<div class="card__img card__img--placeholder">TB</div>`}
        <div class="card__body">
          <span class="card__cat">${catLabel(p.category)}</span>
          <span class="card__name">${p.name}</span>
          <div class="card__meta">
            <span class="card__price">${Number(p.price).toFixed(2)} ₼</span>
            <span class="card__stock ${p.stock <= 5 ? "is-low" : ""}">${p.stock} ədəd</span>
          </div>
          <div class="card__actions">
            <button class="primary-btn" data-action="add-basket" data-id="${p.id}" ${p.stock === 0 ? "disabled" : ""}>Səbətə at</button>
            <button class="icon-btn is-fav" data-action="remove-fav" data-id="${p.id}" title="Sevimlilərdən çıxar">♥</button>
          </div>
        </div>
      </div>`).join("");
    document.getElementById("favoritesEmpty").hidden = favs.length > 0;
  } catch (err) {
    toast("Sevimlilər yüklənmədi: " + err.message, true);
  }
}

/* =========================================================
   ADMIN — product CRUD
========================================================= */
function populateCategorySelect() {
  const sel = document.getElementById("f_category");
  sel.innerHTML = CATEGORIES.map(c => `<option value="${c.value}">${c.label}</option>`).join("");
}

function fillProductForm(p) {
  document.getElementById("productId").value = p.id;
  document.getElementById("f_name").value = p.name;
  document.getElementById("f_price").value = p.price;
  document.getElementById("f_category").value = p.category;
  document.getElementById("f_stock").value = p.stock;
  document.getElementById("f_img").value = p.imgURL || "";
  document.getElementById("formSubmitBtn").textContent = "Dəyişikliyi yadda saxla";
  document.getElementById("formResetBtn").hidden = false;
}
function resetProductForm() {
  document.getElementById("productForm").reset();
  document.getElementById("productId").value = "";
  document.getElementById("formSubmitBtn").textContent = "Məhsul əlavə et";
  document.getElementById("formResetBtn").hidden = true;
}
document.getElementById("formResetBtn").addEventListener("click", resetProductForm);

document.getElementById("productForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = document.getElementById("productId").value;
  const body = {
    name: f_name.value.trim(),
    price: Number(f_price.value),
    category: f_category.value,
    stock: Number(f_stock.value),
    imgURL: f_img.value.trim(),
  };
  try {
    if (id) {
      await api(`/products/${id}`, { method: "PUT", body: JSON.stringify(body) });
      toast("Məhsul yeniləndi.");
    } else {
      await api("/products", { method: "POST", body: JSON.stringify(body) });
      toast("Məhsul əlavə olundu.");
    }
    resetProductForm();
    loadAdminTable();
    loadProducts();
  } catch (err) {
    toast(err.message, true);
  }
});

async function loadAdminTable() {
  try {
    const products = await api("/products");
    state.products = products;
    const body = document.getElementById("adminTableBody");
    body.innerHTML = products.map(p => `
      <tr>
        <td>${p.imgURL ? `<img src="${p.imgURL}" onerror="this.style.visibility='hidden'">` : ""}</td>
        <td>${p.name}</td>
        <td>${catLabel(p.category)}</td>
        <td>${Number(p.price).toFixed(2)} ₼</td>
        <td>${p.stock}</td>
        <td>
          <div class="row-actions">
            <button data-action="edit-product" data-id="${p.id}">Redaktə</button>
            <button class="danger" data-action="delete-product" data-id="${p.id}">Sil</button>
          </div>
        </td>
      </tr>`).join("");
  } catch (err) {
    toast("Cədvəl yüklənmədi: " + err.message, true);
  }
}

/* =========================================================
   INIT
========================================================= */
function init() {
  populateCategorySelect();
  refreshUserChip();
  loadProducts();
  if (!state.user) {
    // don't force the modal open on first paint — let them browse first
  } else {
    loadBasket();
  }
}
init();
