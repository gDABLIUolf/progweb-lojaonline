<template>
  <div class="login-container">
    <div class="login-card fade-in">
      <RouterLink to="/" class="brand-logo text-decoration-none d-block">VesteBem.</RouterLink>
      
      <!-- ETAPA 1: SOLICITAR CÓDIGO -->
      <div v-if="etapa === 1">
        <h4 class="fw-bold mb-2 text-center text-dark">Recuperar Senha</h4>
        <p class="auth-subtitle">Introduza o seu e-mail e enviaremos um código de 6 dígitos para redefinir a sua senha.</p>

        <form @submit.prevent="solicitarCodigo">
          <div class="mb-4 position-relative">
            <i class="ph ph-envelope input-icon"></i>
            <input
              type="email"
              class="form-control-premium w-100"
              v-model="email"
              placeholder="Seu e-mail cadastrado"
              required
              autofocus
            />
          </div>

          <button type="submit" class="btn-premium w-100 mb-3" :disabled="carregando">
            <span v-if="carregando" class="spinner-border spinner-border-sm me-2" role="status"></span>
            {{ carregando ? "A enviar código..." : "Enviar Código" }}
          </button>
        </form>

        <div class="text-center mt-3">
          <RouterLink to="/login" class="text-dark text-decoration-none small fw-bold d-inline-flex align-items-center gap-1">
            <i class="ph ph-arrow-left"></i> Voltar para o Login
          </RouterLink>
        </div>
      </div>

      <!-- ETAPA 2: REDEFINIR SENHA -->
      <div v-else-if="etapa === 2">
        <h4 class="fw-bold mb-2 text-center text-dark">Definir Nova Senha</h4>
        <p class="auth-subtitle">Insira o código de 6 dígitos enviado para o seu e-mail e defina a sua nova senha.</p>

        <form @submit.prevent="redefinirSenha">
          <div class="mb-3 position-relative">
            <i class="ph ph-envelope input-icon"></i>
            <input
              type="email"
              class="form-control-premium w-100 bg-light"
              v-model="email"
              placeholder="Seu e-mail"
              disabled
            />
          </div>

          <div class="mb-3 position-relative">
            <i class="ph ph-hash input-icon"></i>
            <input
              type="text"
              class="form-control-premium w-100"
              v-model="codigo"
              placeholder="Código de 6 dígitos"
              required
              maxlength="6"
              pattern="[0-9]{6}"
              title="O código deve ter exatamente 6 dígitos numéricos"
            />
          </div>

          <div class="mb-3 position-relative">
            <i class="ph ph-lock-key input-icon"></i>
            <input
              type="password"
              class="form-control-premium w-100"
              v-model="novaSenha"
              placeholder="Nova senha (min. 6 caracteres)"
              required
            />
          </div>

          <div class="mb-4 position-relative">
            <i class="ph ph-lock-key input-icon"></i>
            <input
              type="password"
              class="form-control-premium w-100"
              v-model="confirmacaoSenha"
              placeholder="Confirme a nova senha"
              required
            />
          </div>

          <button type="submit" class="btn-premium w-100 mb-3" :disabled="carregando">
            <span v-if="carregando" class="spinner-border spinner-border-sm me-2" role="status"></span>
            {{ carregando ? "A redefinir..." : "Redefinir Senha" }}
          </button>
        </form>

        <div class="text-center mt-3 d-flex justify-content-between align-items-center">
          <button @click="etapa = 1" class="btn btn-link text-muted text-decoration-none small p-0 d-inline-flex align-items-center gap-1 border-0 bg-transparent">
            <i class="ph ph-arrow-left"></i> Alterar E-mail
          </button>
          <RouterLink to="/login" class="text-dark text-decoration-none small fw-bold">
            Ir para Login
          </RouterLink>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../../services/api";
import { showToast } from "../../services/toast";

const router = useRouter();

const etapa = ref(1);
const email = ref("");
const codigo = ref("");
const novaSenha = ref("");
const confirmacaoSenha = ref("");
const carregando = ref(false);

const solicitarCodigo = async () => {
  if (!email.value) return;
  try {
    carregando.value = true;
    await api.post("/auth/esqueci-senha", {
      email: email.value,
    });
    showToast("Solicitação de redefinição enviada com sucesso!", "success");
    etapa.value = 2;
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : (rawData?.message || "Erro ao solicitar recuperação.");
    showToast(msg, "error");
  } finally {
    carregando.value = false;
  }
};

const redefinirSenha = async () => {
  if (novaSenha.value !== confirmacaoSenha.value) {
    showToast("As senhas não coincidem.", "error");
    return;
  }
  if (novaSenha.value.length < 6) {
    showToast("A senha deve ter pelo menos 6 caracteres.", "error");
    return;
  }
  try {
    carregando.value = true;
    await api.post("/auth/redefinir-senha", {
      email: email.value,
      codigo: codigo.value,
      novaSenha: novaSenha.value,
    });
    showToast("Senha redefinida com sucesso!", "success");
    router.push("/login");
  } catch (error) {
    const rawData = error.response?.data;
    const msg = typeof rawData === "string" ? rawData : (rawData?.message || "Erro ao redefinir senha.");
    showToast(msg, "error");
  } finally {
    carregando.value = false;
  }
};

onMounted(() => {
  const card = document.querySelector(".fade-in");
  if (card) {
    setTimeout(() => {
      card.style.opacity = "1";
      card.style.transform = "translateY(0)";
    }, 100);
  }
});
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--surface-color, #f8fafc);
}

.login-card {
  width: 100%;
  max-width: 450px;
  background: var(--bg-color, #ffffff);
  border-radius: var(--radius-lg, 16px);
  padding: 3rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.05);
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.8s ease, transform 0.8s ease;
}

.brand-logo {
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--text-primary, #111111);
  text-align: center;
  margin-bottom: 0.5rem;
  transition: opacity 0.2s ease;
}

.brand-logo:hover {
  opacity: 0.8;
}

.auth-subtitle {
  color: var(--text-secondary, #475569);
  text-align: center;
  margin-bottom: 2rem;
  font-size: 0.95rem;
}

.input-icon {
  position: absolute;
  left: 1.2rem;
  top: 1.1rem;
  color: var(--text-secondary, #475569);
  font-size: 1.2rem;
  z-index: 10;
}

.form-control-premium {
  padding-left: 3.2rem !important;
}
</style>
