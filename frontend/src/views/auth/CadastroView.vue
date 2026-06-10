<template>
  <div class="login-container">
    <div class="login-card fade-in">
      <RouterLink to="/" class="brand-logo text-decoration-none d-block">VesteBem.</RouterLink>
      <p class="auth-subtitle">Crie sua conta para começar.</p>

      <div v-if="erro" class="alert alert-danger py-2 mb-3" style="font-size: 0.9rem; border-radius: var(--radius-sm)">
        <i class="ph ph-warning-circle me-1"></i> {{ erro }}
      </div>
      
      <div v-if="sucesso" class="alert alert-success py-2 mb-3" style="font-size: 0.9rem; border-radius: var(--radius-sm)">
        <i class="ph ph-check-circle me-1"></i> Cadastro realizado com sucesso! Redirecionando...
      </div>

      <form @submit.prevent="fazerCadastro">
        <div class="mb-4 position-relative">
          <i class="ph ph-user input-icon"></i>
          <input
            type="text"
            class="form-control-premium"
            v-model="nome"
            placeholder="Seu nome completo"
            required
            autofocus
          />
        </div>

        <div class="mb-4 position-relative">
          <i class="ph ph-envelope input-icon"></i>
          <input
            type="email"
            class="form-control-premium"
            v-model="email"
            placeholder="Seu e-mail"
            required
          />
        </div>

        <div class="mb-4 position-relative">
          <i class="ph ph-lock-key input-icon"></i>
          <input
            type="password"
            class="form-control-premium"
            v-model="senha"
            placeholder="Senha (mínimo 6 caracteres)"
            required
            minlength="6"
          />
        </div>

        <button type="submit" class="btn-premium w-100" :disabled="carregando">
          {{ carregando ? "A cadastrar..." : "Criar Conta" }}
        </button>
      </form>

      <div class="text-center mt-4">
        <router-link to="/login" class="text-dark text-decoration-none" style="font-size: 0.9rem; font-weight: 500">
          Já tem uma conta? Entre aqui
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../../services/api";

const nome = ref("");
const email = ref("");
const senha = ref("");
const erro = ref("");
const sucesso = ref(false);
const carregando = ref(false);

const router = useRouter();

const fazerCadastro = async () => {
  erro.value = "";
  carregando.value = true;

  try {
    await api.post("/usuarios", {
      nome: nome.value,
      email: email.value,
      senha: senha.value,
    });
    
    sucesso.value = true;
    
    // Redireciona para o login após 2 segundos e limpa estados anteriores
    setTimeout(() => {
      window.location.href = "/login";
    }, 2000);
    
  } catch (error) {
    console.error("Erro na API:", error);
    erro.value = error.response?.data || "Erro ao realizar cadastro. Verifique os dados.";
  } finally {
    carregando.value = false;
  }
};

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
  transition: opacity 0.8s ease, transform 0.8s ease;
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