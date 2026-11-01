const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const msg = document.getElementById("msg");

async function submitAuth(path) {
  msg.textContent = "";
  try {
    const res = await fetch("/api/auth/" + path, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        username: usernameInput.value,
        password: passwordInput.value
      })
    });
    const data = await res.json();

    if (!res.ok) {
      msg.textContent = data.fields ? Object.values(data.fields).join(" ") : data.message;
      return;
    }

    if (path === "login") {
      localStorage.setItem("token", data.token);
      window.location.href = "index.html";
    } else {
      msg.textContent = "Registered! Now click Login.";
    }
  } catch (err) {
    msg.textContent = "Could not reach the server.";
  }
}

document.getElementById("loginBtn").addEventListener("click", () => submitAuth("login"));
document.getElementById("registerBtn").addEventListener("click", () => submitAuth("register"));