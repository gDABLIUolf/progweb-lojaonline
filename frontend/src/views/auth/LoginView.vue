<template>
  <div class="login-container">
    <div class="login-card fade-in">
      <RouterLink to="/" class="brand-logo text-decoration-none d-block">VesteBem.</RouterLink>
      <p class="auth-subtitle">Insira o seu e-mail e senha para entrar.</p>

      <div
        v-if="erro"
        class="alert alert-danger py-2 mb-3"
        style="font-size: 0.9rem; border-radius: var(--radius-sm)"
      >
        <i class="ph ph-warning-circle me-1"></i> {{ erro }}
      </div>

      <form @submit.prevent="fazerLogin">
        <div class="mb-4 position-relative">
          <i class="ph ph-envelope input-icon"></i>
          <input
            type="email"
            class="form-control-premium"
            v-model="email"
            placeholder="Seu e-mail"
            required
            autofocus
          />
        </div>

        <div class="mb-4 position-relative">
          <i class="ph ph-lock-key input-icon"></i>
          <input
            type="password"
            class="form-control-premium"
            v-model="senha"
            placeholder="Senha"
            required
          />
        </div>

        <div
          class="d-flex justify-content-between align-items-center mb-4 px-2"
        >
          <div class="form-check">
            <input type="checkbox" class="form-check-input" id="lembrar" />
            <label
              class="form-check-label text-muted"
              for="lembrar"
              style="font-size: 0.9rem"
              >Lembrar-me</label
            >
          </div>
          <RouterLink
            to="/esqueci-senha"
            class="text-dark text-decoration-none"
            style="font-size: 0.9rem; font-weight: 500"
            >Esqueceu a senha?</RouterLink
          >
        </div>

        <button type="submit" class="btn-premium w-100" :disabled="carregando">
          {{ carregando ? "A entrar..." : "Entrar na Conta" }}
        </button>
      </form>

      <div class="text-center mt-4">
        <router-link to="/cadastro" class="text-dark text-decoration-none" style="font-size: 0.9rem; font-weight: 500">
          Não tem uma conta? Cadastre-se
        </router-link>
      </div>
      
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../../services/api";

// Variáveis de estado
const email = ref("");
const senha = ref("");
const erro = ref("");
const carregando = ref(false);

const router = useRouter(); // Ferramenta para mudar de tela

// Nova função de Login Real
const fazerLogin = async () => {
  erro.value = ""; // Limpa os erros ao tentar de novo
  carregando.value = true;

  try {
    // 1. Bate na porta do Spring Boot
    const resposta = await api.post("/auth/login", {
      email: email.value,
      senha: senha.value,
    });

    // 2. Pega o Token do JSON devolvido e guarda no navegador
    const tokenJWT = resposta.data.token;
    localStorage.setItem("token_vestebem", tokenJWT);

    // Se a sua API devolve a Role do utilizador no login, guardamos também!
    // localStorage.setItem('user_role', resposta.data.role);

    // 3. Sucesso! Manda o utilizador para a Home e recarrega a página para inicializar os estados
    window.location.href = "/";
  } catch (error) {
    console.error("Erro na API:", error);
    // 4. Captura o 401/403 do Java e avisa o utilizador
    erro.value = "E-mail ou senha inválidos. Tente novamente.";
  } finally {
    carregando.value = false;
  }
};

// Animação de entrada
onMounted(() => {
  const card = document.querySelector(".fade-in");
  setTimeout(() => {
    card.style.opacity = "1";
    card.style.transform = "translateY(0)";
  }, 100);
});
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--surface-color);
}

.login-card {
  width: 100%;
  max-width: 450px;
  background: var(--bg-color);
  border-radius: var(--radius-lg);
  padding: 3rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.05);

  opacity: 0;
  transform: translateY(20px);
  transition:
    opacity 0.8s ease,
    transform 0.8s ease;
}

.brand-logo {
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--text-primary);
  text-align: center;
  margin-bottom: 0.5rem;
  transition: opacity 0.2s ease;
}

.brand-logo:hover {
  opacity: 0.8;
}

.auth-subtitle {
  color: var(--text-secondary);
  text-align: center;
  margin-bottom: 2rem;
  font-size: 0.95rem;
}

.input-icon {
  position: absolute;
  left: 1.2rem;
  top: 1.1rem;
  color: var(--text-secondary);
  font-size: 1.2rem;
}
</style>
