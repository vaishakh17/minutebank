const API = "/api";
const token = localStorage.getItem("token");

if (!token) {
  window.location.href = "login.html";
}

const datePicker     = document.getElementById("datePicker");
const balanceCard    = document.getElementById("balanceCard");
const balanceEl      = document.getElementById("balance");
const spentText      = document.getElementById("spentText");
const overdraftMsg   = document.getElementById("overdraftMsg");
const titleInput     = document.getElementById("title");
const categorySelect = document.getElementById("category");
const minutesInput   = document.getElementById("minutes");
const addBtn         = document.getElementById("addBtn");
const errorEl        = document.getElementById("error");
const entryList      = document.getElementById("entryList");
const logoutBtn      = document.getElementById("logoutBtn");
const weekRange    = document.getElementById("weekRange");
const weekBars     = document.getElementById("weekBars");
const weekInsights = document.getElementById("weekInsights");

datePicker.value = new Date().toLocaleDateString("en-CA");

function logout() {
  localStorage.removeItem("token");
  window.location.href = "login.html";
}

async function api(path, options = {}) {
  const res = await fetch(API + path, {
    ...options,
    headers: { ...options.headers, Authorization: "Bearer " + token }
  });
  if (res.status === 401) {
    logout();
  }
  return res;
}

async function loadSummary() {
  const res = await api(`/summary?date=${datePicker.value}`);
  if (!res.ok) return;
  const data = await res.json();

  balanceEl.textContent = data.balance;
  spentText.textContent = `Spent: ${data.spent} min`;
  overdraftMsg.classList.toggle("hidden", !data.overdraft);
  balanceCard.classList.toggle("overdraft", data.overdraft);
}

async function loadEntries() {
  const res = await api(`/entries?date=${datePicker.value}`);
  if (!res.ok) return;
  const entries = await res.json();

  entryList.innerHTML = "";
  entries.forEach(e => {
    const li = document.createElement("li");
    li.textContent = `${e.title} (${e.category}): ${e.minutes} min`;
    entryList.appendChild(li);
  });
}

async function addEntry() {
  const title = titleInput.value.trim();
  const minutes = parseInt(minutesInput.value, 10);
  
  

  if (!title || !minutes || minutes <= 0) {
    errorEl.textContent = "Enter a title and minutes above 0.";
    return;
  }
  errorEl.textContent = "";

  const entry = {
    title: title,
    category: categorySelect.value,
    minutes: minutes,
    entryDate: datePicker.value
  };

  try {
    const res = await api("/entries", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(entry)
    });

    if (!res.ok) {
      const err = await res.json();
      errorEl.textContent = err.fields ? Object.values(err.fields).join(" ") : err.message;
      return;
    }

    titleInput.value = "";
    minutesInput.value = "";
    refresh();
  } catch (err) {
    errorEl.textContent = "Could not save. Is the backend running?";
  }
}

async function loadWeekly() {
  const res = await api(`/weekly?date=${datePicker.value}`);
  if (!res.ok) return;
  const w = await res.json();

  weekRange.textContent = `${w.weekStart} to ${w.weekEnd}`;
  weekBars.innerHTML = "";

  Object.entries(w.perDay).forEach(([day, minutes]) => {
    const row = document.createElement("div");
    row.className = "bar-row";

    const label = document.createElement("span");
    label.className = "bar-label";
    label.textContent = new Date(day + "T00:00:00")
      .toLocaleDateString("en-IN", { weekday: "short" });

    const track = document.createElement("div");
    track.className = "bar-track";
    const fill = document.createElement("div");
    fill.className = "bar-fill" + (minutes > 1440 ? " over" : "");
    fill.style.width = Math.min((minutes / 1440) * 100, 100) + "%";
    track.appendChild(fill);

    const value = document.createElement("span");
    value.className = "bar-value";
    value.textContent = minutes + " min";

    row.append(label, track, value);
    weekBars.appendChild(row);
  });

  weekInsights.textContent =
    `Average: ${w.averagePerDay} min/day | Top category: ${w.topCategory} | Overdraft days: ${w.overdraftDays}`;
}

function refresh() {
  loadSummary();
  loadEntries();
  loadWeekly();
}

addBtn.addEventListener("click", addEntry);
datePicker.addEventListener("change", refresh);
logoutBtn.addEventListener("click", logout);

refresh();